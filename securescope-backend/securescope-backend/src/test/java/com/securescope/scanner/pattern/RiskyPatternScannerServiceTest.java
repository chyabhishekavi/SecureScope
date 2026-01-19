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

	@Test
	void scanFindsSha1AndPermissiveCors() {
		List<CodeLine> lines = List.of(
			new CodeLine(1, "MessageDigest.getInstance(\"SHA-1\");"),
			new CodeLine(2, "config.allowedOrigins(\"*\");")
		);

		List<FindingResult> findings = riskyPatternScannerService.scan(lines, "SecurityConfig.java");

		assertThat(findings)
			.extracting(FindingResult::title)
			.containsExactly("Weak hash algorithm: SHA1", "Permissive CORS configuration");
		assertThat(findings)
			.extracting(FindingResult::severity)
			.containsExactly(com.securescope.common.enums.Severity.HIGH, com.securescope.common.enums.Severity.MEDIUM);
	}

	@Test
	void scanReturnsNoFindingsForParameterizedQueryAndSafeCors() {
		List<CodeLine> lines = List.of(
			new CodeLine(1, "PreparedStatement statement = connection.prepareStatement(\"SELECT * FROM users WHERE id = ?\");"),
			new CodeLine(2, "config.allowedOrigins(\"https://app.example.com\");")
		);

		assertThat(riskyPatternScannerService.scan(lines, "Repository.java")).isEmpty();
	}
}
