package com.securescope.scanner.orchestrator;

import static org.assertj.core.api.Assertions.assertThat;

import com.securescope.common.enums.RiskLevel;
import com.securescope.scanner.dto.QuickScanRequest;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.owasp.OwaspMappingService;
import com.securescope.scanner.pattern.RiskyPatternScannerService;
import com.securescope.scanner.scoring.SecurityScoringService;
import com.securescope.scanner.secret.SecretScannerService;
import org.junit.jupiter.api.Test;

class QuickCodeScannerServiceTest {

	private final OwaspMappingService owaspMappingService = new OwaspMappingService();
	private final QuickCodeScannerService quickCodeScannerService = new QuickCodeScannerService(
		new SecretScannerService(owaspMappingService),
		new RiskyPatternScannerService(owaspMappingService),
		new SecurityScoringService()
	);

	@Test
	void scanReturnsFindingsScoreAndGeneratedId() {
		QuickScanRequest request = new QuickScanRequest(
			"Auth sample",
			"JavaScript",
			"auth.js",
			"const token = \"abcdef1234567890\";\nMessageDigest.getInstance(\"SHA-1\");"
		);

		ScanResult result = quickCodeScannerService.scan(request);

		assertThat(result.scanId()).isNotBlank();
		assertThat(result.totalFindings()).isEqualTo(2);
		assertThat(result.securityScore()).isEqualTo(50);
		assertThat(result.riskLevel()).isEqualTo(RiskLevel.MODERATE);
		assertThat(result.findings().get(0).evidence()).doesNotContain("abcdef1234567890");
	}
}
