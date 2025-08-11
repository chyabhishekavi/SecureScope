package com.securescope.scanner.orchestrator;

import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.dto.QuickScanRequest;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.dto.ScoreResult;
import com.securescope.scanner.pattern.RiskyPatternScannerService;
import com.securescope.scanner.scoring.SecurityScoringService;
import com.securescope.scanner.secret.SecretScannerService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class QuickCodeScannerService {

	private final SecretScannerService secretScannerService;
	private final RiskyPatternScannerService riskyPatternScannerService;
	private final SecurityScoringService securityScoringService;

	public QuickCodeScannerService(
		SecretScannerService secretScannerService,
		RiskyPatternScannerService riskyPatternScannerService,
		SecurityScoringService securityScoringService
	) {
		this.secretScannerService = secretScannerService;
		this.riskyPatternScannerService = riskyPatternScannerService;
		this.securityScoringService = securityScoringService;
	}

	public ScanResult scan(QuickScanRequest request) {
		String filePath = resolveFilePath(request);
		List<CodeLine> lines = toCodeLines(request.codeContent());
		List<FindingResult> findings = new ArrayList<>();

		findings.addAll(secretScannerService.scan(lines, filePath));
		findings.addAll(riskyPatternScannerService.scan(lines, filePath));

		ScoreResult scoreResult = securityScoringService.calculateScore(findings);

		return new ScanResult(
			UUID.randomUUID().toString(),
			scoreResult.securityScore(),
			scoreResult.riskLevel(),
			findings.size(),
			findings
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

	private List<CodeLine> toCodeLines(String codeContent) {
		String[] rawLines = codeContent.split("\\R", -1);
		List<CodeLine> lines = new ArrayList<>();

		for (int index = 0; index < rawLines.length; index++) {
			lines.add(new CodeLine(index + 1, rawLines[index]));
		}

		return lines;
	}
}
