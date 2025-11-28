package com.securescope.scanner.zip;

import com.securescope.common.enums.FindingStatus;
import com.securescope.common.enums.ScanSourceType;
import com.securescope.common.enums.ScanStatus;
import com.securescope.common.exception.BadRequestException;
import com.securescope.common.exception.ResourceNotFoundException;
import com.securescope.persistence.entity.Finding;
import com.securescope.persistence.entity.Project;
import com.securescope.persistence.entity.Scan;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.ProjectRepository;
import com.securescope.persistence.repository.ScanRepository;
import com.securescope.persistence.repository.UserRepository;
import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.dto.ScoreResult;
import com.securescope.scanner.dependency.DependencyScannerService;
import com.securescope.scanner.pattern.RiskyPatternScannerService;
import com.securescope.scanner.scoring.SecurityScoringService;
import com.securescope.scanner.secret.SecretScannerService;
import com.securescope.scanner.zip.dto.ZipScanRequest;
import com.securescope.scanner.zip.dto.ZipUploadResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProjectZipScanService {

	private static final long MAX_ZIP_SIZE_BYTES = 10L * 1024L * 1024L;

	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final ScanRepository scanRepository;
	private final SafeZipExtractorService safeZipExtractorService;
	private final ZipUploadSessionStore zipUploadSessionStore;
	private final SecretScannerService secretScannerService;
	private final RiskyPatternScannerService riskyPatternScannerService;
	private final DependencyScannerService dependencyScannerService;
	private final SecurityScoringService securityScoringService;

	public ProjectZipScanService(
		ProjectRepository projectRepository,
		UserRepository userRepository,
		ScanRepository scanRepository,
		SafeZipExtractorService safeZipExtractorService,
		ZipUploadSessionStore zipUploadSessionStore,
		SecretScannerService secretScannerService,
		RiskyPatternScannerService riskyPatternScannerService,
		DependencyScannerService dependencyScannerService,
		SecurityScoringService securityScoringService
	) {
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
		this.scanRepository = scanRepository;
		this.safeZipExtractorService = safeZipExtractorService;
		this.zipUploadSessionStore = zipUploadSessionStore;
		this.secretScannerService = secretScannerService;
		this.riskyPatternScannerService = riskyPatternScannerService;
		this.dependencyScannerService = dependencyScannerService;
		this.securityScoringService = securityScoringService;
	}

	public ZipUploadResponse uploadZip(UUID projectId, MultipartFile file, String userEmail) {
		getOwnedProject(projectId, userEmail);
		validateZipFile(file);

		UUID uploadId = UUID.randomUUID();
		Path extractionDirectory = createExtractionDirectory(uploadId);

		try {
			ZipExtractionResult extractionResult = safeZipExtractorService.extractSupportedFiles(
				file.getInputStream(),
				extractionDirectory
			);
			ZipUploadSession session = new ZipUploadSession(
				uploadId,
				projectId,
				userEmail,
				file.getOriginalFilename(),
				file.getSize(),
				extractionDirectory,
				extractionResult.files(),
				extractionResult.skippedEntryCount(),
				Instant.now()
			);

			zipUploadSessionStore.save(session);

			return new ZipUploadResponse(
				session.uploadId(),
				session.projectId(),
				session.fileName(),
				session.fileSizeBytes(),
				session.files().size(),
				session.skippedEntryCount(),
				session.uploadedAt()
			);
		} catch (IOException exception) {
			throw new BadRequestException("Unable to read uploaded ZIP file");
		}
	}

	@Transactional
	public ScanResult scanUploadedZip(UUID projectId, ZipScanRequest request, String userEmail) {
		Project project = getOwnedProject(projectId, userEmail);
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		ZipUploadSession session = zipUploadSessionStore.find(request.uploadId())
			.filter(upload -> upload.projectId().equals(projectId))
			.filter(upload -> upload.userEmail().equals(userEmail))
			.orElseThrow(() -> new ResourceNotFoundException("Uploaded ZIP not found"));

		List<FindingResult> findingResults = scanFiles(session.files());
		ScoreResult scoreResult = securityScoringService.calculateScore(findingResults);
		Scan scan = saveScan(project, user, session.fileName(), scoreResult, findingResults);

		zipUploadSessionStore.remove(session.uploadId());
		deleteQuietly(session.extractionDirectory());

		return new ScanResult(
			scan.getId().toString(),
			scan.getStatus(),
			scan.getSecurityScore(),
			scan.getRiskLevel(),
			findingResults.size(),
			findingResults
		);
	}

	private List<FindingResult> scanFiles(List<ExtractedZipFile> files) {
		List<FindingResult> findings = new ArrayList<>();

		for (ExtractedZipFile file : files) {
			String content = readTextFile(file.absolutePath());
			List<CodeLine> lines = toCodeLines(content);
			findings.addAll(secretScannerService.scan(lines, file.relativePath()));
			findings.addAll(riskyPatternScannerService.scan(lines, file.relativePath()));
			findings.addAll(dependencyScannerService.scan(file.relativePath(), content));
		}

		return findings;
	}

	private Scan saveScan(
		Project project,
		User user,
		String fileName,
		ScoreResult scoreResult,
		List<FindingResult> findingResults
	) {
		Instant now = Instant.now();
		Scan scan = new Scan();
		scan.setScanName("ZIP scan: " + fileName);
		scan.setSourceType(ScanSourceType.ZIP_UPLOAD);
		scan.setStatus(ScanStatus.COMPLETED);
		scan.setSecurityScore(scoreResult.securityScore());
		scan.setRiskLevel(scoreResult.riskLevel());
		scan.setStartedAt(now);
		scan.setCompletedAt(now);
		scan.setRequestedBy(user);
		scan.setProject(project);

		for (FindingResult findingResult : findingResults) {
			scan.getFindings().add(toFindingEntity(findingResult, scan));
		}

		return scanRepository.save(scan);
	}

	private Finding toFindingEntity(FindingResult findingResult, Scan scan) {
		Finding finding = new Finding();
		finding.setTitle(findingResult.title());
		finding.setDescription(findingResult.description());
		finding.setSeverity(findingResult.severity());
		finding.setCategory(findingResult.category());
		finding.setStatus(FindingStatus.OPEN);
		finding.setOwaspCategory(findingResult.owaspCategory());
		finding.setFilePath(findingResult.filePath());
		finding.setLineNumber(findingResult.lineNumber());
		finding.setEvidence(findingResult.evidence());
		finding.setRecommendation(findingResult.recommendation());
		finding.setScan(scan);
		return finding;
	}

	private Project getOwnedProject(UUID projectId, String userEmail) {
		return projectRepository.findByIdAndOwnerEmail(projectId, userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private void validateZipFile(MultipartFile file) {
		if (file == null || file.isEmpty()) {
			throw new BadRequestException("ZIP file is required");
		}
		if (file.getSize() > MAX_ZIP_SIZE_BYTES) {
			throw new BadRequestException("ZIP file must be 10 MB or smaller");
		}
		String fileName = file.getOriginalFilename();
		if (fileName == null || !fileName.toLowerCase().endsWith(".zip")) {
			throw new BadRequestException("Only .zip files are supported");
		}
	}

	private Path createExtractionDirectory(UUID uploadId) {
		try {
			return Files.createTempDirectory("securescope-zip-" + uploadId + "-");
		} catch (IOException exception) {
			throw new BadRequestException("Unable to prepare ZIP upload workspace");
		}
	}

	private String readTextFile(Path path) {
		try {
			return Files.readString(path, StandardCharsets.UTF_8);
		} catch (IOException exception) {
			return "";
		}
	}

	private List<CodeLine> toCodeLines(String codeContent) {
		String[] rawLines = codeContent.split("\\R", -1);
		List<CodeLine> lines = new ArrayList<>();

		for (int index = 0; index < rawLines.length; index++) {
			lines.add(new CodeLine(index + 1, rawLines[index]));
		}

		return lines;
	}

	private void deleteQuietly(Path directory) {
		try (var paths = Files.walk(directory)) {
			paths.sorted(Comparator.reverseOrder())
				.forEach(path -> {
					try {
						Files.deleteIfExists(path);
					} catch (IOException ignored) {
						// Temporary upload cleanup should never fail the completed scan response.
					}
				});
		} catch (IOException ignored) {
			// Temporary upload cleanup should never fail the completed scan response.
		}
	}
}
