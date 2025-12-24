package com.securescope.scanner.archive;

import com.securescope.common.enums.FindingStatus;
import com.securescope.common.enums.ScanSourceType;
import com.securescope.common.enums.ScanStatus;
import com.securescope.persistence.entity.Finding;
import com.securescope.persistence.entity.Project;
import com.securescope.persistence.entity.Scan;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.ScanRepository;
import com.securescope.scanner.dependency.DependencyScannerService;
import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.dto.ScoreResult;
import com.securescope.scanner.pattern.RiskyPatternScannerService;
import com.securescope.scanner.scoring.SecurityScoringService;
import com.securescope.scanner.secret.SecretScannerService;
import com.securescope.scanner.zip.ExtractedZipFile;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProjectArchiveScanService {

	private final ScanRepository scanRepository;
	private final SecretScannerService secretScannerService;
	private final RiskyPatternScannerService riskyPatternScannerService;
	private final DependencyScannerService dependencyScannerService;
	private final SecurityScoringService securityScoringService;

	public ProjectArchiveScanService(
		ScanRepository scanRepository,
		SecretScannerService secretScannerService,
		RiskyPatternScannerService riskyPatternScannerService,
		DependencyScannerService dependencyScannerService,
		SecurityScoringService securityScoringService
	) {
		this.scanRepository = scanRepository;
		this.secretScannerService = secretScannerService;
		this.riskyPatternScannerService = riskyPatternScannerService;
		this.dependencyScannerService = dependencyScannerService;
		this.securityScoringService = securityScoringService;
	}

	public ScanResult scanAndSave(
		Project project,
		User user,
		String scanName,
		ScanSourceType sourceType,
		List<ExtractedZipFile> files
	) {
		List<FindingResult> findingResults = scanFiles(files);
		ScoreResult scoreResult = securityScoringService.calculateScore(findingResults);
		Scan scan = saveScan(project, user, scanName, sourceType, scoreResult, findingResults);

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
		String scanName,
		ScanSourceType sourceType,
		ScoreResult scoreResult,
		List<FindingResult> findingResults
	) {
		Instant now = Instant.now();
		Scan scan = new Scan();
		scan.setScanName(scanName);
		scan.setSourceType(sourceType);
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
}
