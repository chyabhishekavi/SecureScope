package com.securescope.scanner.pattern;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.Severity;
import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.owasp.OwaspMappingService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class RiskyPatternScannerService {

	private static final Pattern MD5_PATTERN = Pattern.compile("(?i)\\b(MD5|MessageDigest\\.getInstance\\([\"']MD5[\"']\\))\\b");
	private static final Pattern SHA1_PATTERN = Pattern.compile("(?i)\\b(SHA1|SHA-1|MessageDigest\\.getInstance\\([\"']SHA-?1[\"']\\))\\b");
	private static final Pattern DISABLED_CSRF_PATTERN = Pattern.compile("(?i)(csrf\\(\\)\\.disable\\(\\)|csrf\\s*[:=]\\s*false)");
	private static final Pattern PERMISSIVE_CORS_PATTERN = Pattern.compile("(?i)(allowedOrigins\\([\"']\\*[\"']\\)|Access-Control-Allow-Origin\\s*[:=]\\s*[\"']\\*[\"'])");
	private static final Pattern SQL_CONCAT_PATTERN = Pattern.compile("(?i)(SELECT|INSERT|UPDATE|DELETE)\\s+.+[\"']\\s*\\+\\s*");

	private final OwaspMappingService owaspMappingService;

	public RiskyPatternScannerService(OwaspMappingService owaspMappingService) {
		this.owaspMappingService = owaspMappingService;
	}

	public List<FindingResult> scan(List<CodeLine> lines, String filePath) {
		List<FindingResult> findings = new ArrayList<>();

		for (CodeLine line : lines) {
			addIfMatches(
				findings,
				line,
				filePath,
				MD5_PATTERN,
				"Weak hash algorithm: MD5",
				"MD5 is no longer considered safe for security-sensitive hashing.",
				Severity.HIGH,
				owaspMappingService.weakCryptography(),
				"Use a modern password hashing or cryptographic algorithm such as bcrypt, Argon2, SHA-256, or stronger depending on the use case."
			);
			addIfMatches(
				findings,
				line,
				filePath,
				SHA1_PATTERN,
				"Weak hash algorithm: SHA1",
				"SHA1 is no longer considered safe for collision-resistant security use cases.",
				Severity.HIGH,
				owaspMappingService.weakCryptography(),
				"Use SHA-256 or stronger for general hashing, and use bcrypt or Argon2 for passwords."
			);
			addIfMatches(
				findings,
				line,
				filePath,
				DISABLED_CSRF_PATTERN,
				"CSRF protection disabled",
				"CSRF protection appears to be disabled.",
				Severity.MEDIUM,
				owaspMappingService.securityMisconfiguration(),
				"Keep CSRF protection enabled for browser-based sessions unless the API is stateless and protected another way."
			);
			addIfMatches(
				findings,
				line,
				filePath,
				PERMISSIVE_CORS_PATTERN,
				"Permissive CORS configuration",
				"CORS appears to allow every origin.",
				Severity.MEDIUM,
				owaspMappingService.securityMisconfiguration(),
				"Restrict allowed origins to trusted frontend domains."
			);
			addIfMatches(
				findings,
				line,
				filePath,
				SQL_CONCAT_PATTERN,
				"SQL query built with string concatenation",
				"SQL appears to be assembled with string concatenation, which can lead to injection.",
				Severity.HIGH,
				owaspMappingService.injection(),
				"Use prepared statements, parameterized queries, or a safe query builder."
			);
		}

		return findings;
	}

	private void addIfMatches(
		List<FindingResult> findings,
		CodeLine line,
		String filePath,
		Pattern pattern,
		String title,
		String description,
		Severity severity,
		String owaspCategory,
		String recommendation
	) {
		if (!pattern.matcher(line.content()).find()) {
			return;
		}

		findings.add(new FindingResult(
			title,
			description,
			severity,
			FindingCategory.RISKY_CODE_PATTERN,
			owaspCategory,
			filePath,
			line.lineNumber(),
			line.content().trim(),
			recommendation
		));
	}
}
