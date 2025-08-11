package com.securescope.scanner.secret;

import static org.assertj.core.api.Assertions.assertThat;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.Severity;
import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.owasp.OwaspMappingService;
import java.util.List;
import org.junit.jupiter.api.Test;

class SecretScannerServiceTest {

	private final SecretScannerService secretScannerService = new SecretScannerService(new OwaspMappingService());

	@Test
	void scanFindsAndMasksHardcodedSecrets() {
		List<CodeLine> lines = List.of(
			new CodeLine(1, "const password = \"super-secret-password\";"),
			new CodeLine(2, "api_key = \"abcdef1234567890\""),
			new CodeLine(3, "-----BEGIN PRIVATE KEY-----")
		);

		List<FindingResult> findings = secretScannerService.scan(lines, "app.js");

		assertThat(findings).hasSize(3);
		assertThat(findings)
			.extracting(FindingResult::severity)
			.containsOnly(Severity.CRITICAL);
		assertThat(findings)
			.extracting(FindingResult::category)
			.containsOnly(FindingCategory.HARDCODED_SECRET);
		assertThat(findings.get(0).evidence()).contains("supe****word");
		assertThat(findings.get(0).evidence()).doesNotContain("super-secret-password");
	}
}
