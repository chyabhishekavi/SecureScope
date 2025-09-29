package com.securescope.scanner.orchestrator;

import com.securescope.common.enums.FindingStatus;
import com.securescope.common.enums.ScanSourceType;
import com.securescope.common.enums.ScanStatus;
import com.securescope.common.exception.ResourceNotFoundException;
import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.dto.QuickScanRequest;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.dto.ScoreResult;
import com.securescope.scanner.pattern.RiskyPatternScannerService;
import com.securescope.scanner.scoring.SecurityScoringService;
import com.securescope.scanner.secret.SecretScannerService;
import com.securescope.persistence.entity.Finding;
import com.securescope.persistence.entity.Project;
import com.securescope.persistence.entity.Scan;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.FindingRepository;
import com.securescope.persistence.repository.ProjectRepository;
import com.securescope.persistence.repository.ScanRepository;
import com.securescope.persistence.repository.UserRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuickCodeScannerService {

	private final SecretScannerService secretScannerService;
	private final RiskyPatternScannerService riskyPatternScannerService;
	private final SecurityScoringService securityScoringService;
	private final UserRepository userRepository;
	private final ProjectRepository projectRepository;
	private final ScanRepository scanRepository;
	private final FindingRepository findingRepository;

	public QuickCodeScannerService(
		SecretScannerService secretScannerService,
		RiskyPatternScannerService riskyPatternScannerService,
		SecurityScoringService securityScoringService,
		UserRepository userRepository,
		ProjectRepository projectRepository,
		ScanRepository scanRepository,
		FindingRepository findingRepository
	) {
		this.secretScannerService = secretScannerService;
		this.riskyPatternScannerService = riskyPatternScannerService;
		this.securityScoringService = securityScoringService;
		this.userRepository = userRepository;
		this.projectRepository = projectRepository;
		this.scanRepository = scanRepository;
		this.findingRepository = findingRepository;
	}

	@Transactional
	public ScanResult scan(QuickScanRequest request, String userEmail) {
		String filePath = resolveFilePath(request);
		List<CodeLine> lines = toCodeLines(request.codeContent());
		List<FindingResult> findings = new ArrayList<>();

		findings.addAll(secretScannerService.scan(lines, filePath));
		findings.addAll(riskyPatternScannerService.scan(lines, filePath));

		ScoreResult scoreResult = securityScoringService.calculateScore(findings);
		Scan savedScan = saveScan(request, userEmail, scoreResult, findings);

		return new ScanResult(
			savedScan.getId().toString(),
			savedScan.getStatus(),
			scoreResult.securityScore(),
			scoreResult.riskLevel(),
			findings.size(),
			findings
		);
	}

	@Transactional(readOnly = true)
	public ScanResult getScan(UUID scanId, String userEmail) {
		Scan scan = getOwnedScan(scanId, userEmail);
		return toScanResult(scan, toFindingResults(scan.getFindings()));
	}

	@Transactional(readOnly = true)
	public List<FindingResult> getFindings(UUID scanId, String userEmail) {
		getOwnedScan(scanId, userEmail);
		return toFindingResults(findingRepository.findByScanIdOrderByCreatedAtAsc(scanId));
	}

	@Transactional(readOnly = true)
	public List<ScanResult> getMyScans(String userEmail) {
		return scanRepository.findByRequestedByEmailOrderByCreatedAtDesc(userEmail)
			.stream()
			.map(scan -> toScanResult(scan, toFindingResults(scan.getFindings())))
			.toList();
	}

	private Scan getOwnedScan(UUID scanId, String userEmail) {
		return scanRepository.findByIdAndRequestedByEmail(scanId, userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Scan not found"));
	}

	private Scan saveScan(
		QuickScanRequest request,
		String userEmail,
		ScoreResult scoreResult,
		List<FindingResult> findingResults
	) {
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		Project project = resolveProject(request, userEmail);
		Instant now = Instant.now();

		Scan scan = new Scan();
		scan.setScanName(resolveScanName(request));
		scan.setSourceType(ScanSourceType.QUICK_CODE);
		scan.setStatus(ScanStatus.COMPLETED);
		scan.setSecurityScore(scoreResult.securityScore());
		scan.setRiskLevel(scoreResult.riskLevel());
		scan.setStartedAt(now);
		scan.setCompletedAt(now);
		scan.setRequestedBy(user);
		scan.setProject(project);

		for (FindingResult findingResult : findingResults) {
			Finding finding = toFindingEntity(findingResult, scan);
			scan.getFindings().add(finding);
		}

		return scanRepository.save(scan);
	}

	private Project resolveProject(QuickScanRequest request, String userEmail) {
		if (request.projectId() == null) {
			return null;
		}

		return projectRepository.findByIdAndOwnerEmail(request.projectId(), userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
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

	private ScanResult toScanResult(Scan scan, List<FindingResult> findings) {
		return new ScanResult(
			scan.getId().toString(),
			scan.getStatus(),
			scan.getSecurityScore(),
			scan.getRiskLevel(),
			findings.size(),
			findings
		);
	}

	private List<FindingResult> toFindingResults(List<Finding> findings) {
		return findings.stream()
			.map(this::toFindingResult)
			.toList();
	}

	private FindingResult toFindingResult(Finding finding) {
		return new FindingResult(
			finding.getTitle(),
			finding.getDescription(),
			finding.getSeverity(),
			finding.getCategory(),
			finding.getOwaspCategory(),
			finding.getFilePath(),
			finding.getLineNumber(),
			finding.getEvidence(),
			finding.getRecommendation()
		);
	}

	private String resolveFilePath(QuickScanRequest request) {
		if (request.fileName() != null && !request.fileName().isBlank()) {
			return request.fileName();
		}
		if (request.snippetName() != null && !request.snippetName().isBlank()) {
			return request.snippetName();
		}
		return "quick-scan-snippet";
	}

	private String resolveScanName(QuickScanRequest request) {
		if (request.snippetName() != null && !request.snippetName().isBlank()) {
			return request.snippetName();
		}
		return resolveFilePath(request);
	}

	private List<CodeLine> toCodeLines(String codeContent) {
		String[] rawLines = codeContent.split("\\R", -1);
		List<CodeLine> lines = new ArrayList<>();

		for (int index = 0; index < rawLines.length; index++) {
			lines.add(new CodeLine(index + 1, rawLines[index]));
		}

		return lines;
	}
}
