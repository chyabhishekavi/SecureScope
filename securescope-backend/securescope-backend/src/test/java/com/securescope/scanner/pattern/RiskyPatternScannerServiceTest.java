package com.securescope.scanner.pattern;

import static org.assertj.core.api.Assertions.assertThat;

import com.securescope.common.enums.FindingCategory;
import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.owasp.OwaspMappingService;
import java.util.List;
import org.junit.jupiter.api.Test;

class RiskyPatternScannerServiceTest {

	private final RiskyPatternScannerService riskyPatternScannerService =
		new RiskyPatternScannerService(new OwaspMappingService());

	@Test
	void scanFindsRiskyPatterns() {
		List<CodeLine> lines = List.of(
			new CodeLine(1, "MessageDigest.getInstance(\"MD5\");"),
			new CodeLine(2, "http.csrf().disable();"),
			new CodeLine(3, "\"SELECT * FROM users WHERE id = \" + userId")
		);

		List<FindingResult> findings = riskyPatternScannerService.scan(lines, "UserController.java");

		assertThat(findings).hasSize(3);
		assertThat(findings)
			.extracting(FindingResult::category)
			.containsOnly(FindingCategory.RISKY_CODE_PATTERN);
		assertThat(findings)
			.extracting(FindingResult::owaspCategory)
			.contains("A03:2021 - Injection", "A05:2021 - Security Misconfiguration");
	}
}
