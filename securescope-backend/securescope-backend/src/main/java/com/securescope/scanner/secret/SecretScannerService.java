package com.securescope.scanner.secret;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.Severity;
import com.securescope.common.util.MaskingUtil;
import com.securescope.scanner.dto.CodeLine;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.owasp.OwaspMappingService;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Service;

@Service
public class SecretScannerService {

	private static final Pattern PASSWORD_PATTERN = Pattern.compile(
		"(?i)\\b(password|passwd|pwd)\\b\\s*[:=]\\s*[\"']([^\"']+)[\"']"
	);
	private static final Pattern TOKEN_PATTERN = Pattern.compile(
		"(?i)\\b(access_token|auth_token|token)\\b\\s*[:=]\\s*[\"']([^\"']+)[\"']"
	);
	private static final Pattern API_KEY_PATTERN = Pattern.compile(
		"(?i)\\b(api_key|apikey|apiKey|x-api-key)\\b\\s*[:=]\\s*[\"']([^\"']+)[\"']"
	);
	private static final Pattern JWT_SECRET_PATTERN = Pattern.compile(
		"(?i)\\b(jwt_secret|jwtSecret|jwt\\.secret)\\b\\s*[:=]\\s*[\"']([^\"']+)[\"']"
	);
	private static final Pattern PRIVATE_KEY_PATTERN = Pattern.compile(
		"-----BEGIN\\s+(RSA\\s+|EC\\s+|DSA\\s+|OPENSSH\\s+)?PRIVATE KEY-----"
	);

	private final OwaspMappingService owaspMappingService;

	public SecretScannerService(OwaspMappingService owaspMappingService) {
		this.owaspMappingService = owaspMappingService;
	}

	public List<FindingResult> scan(List<CodeLine> lines, String filePath) {
		List<FindingResult> findings = new ArrayList<>();

		for (CodeLine line : lines) {
			findings.addAll(findAssignmentSecret(line, filePath, PASSWORD_PATTERN, "Hardcoded password"));
			findings.addAll(findAssignmentSecret(line, filePath, TOKEN_PATTERN, "Hardcoded token"));
			findings.addAll(findAssignmentSecret(line, filePath, API_KEY_PATTERN, "Hardcoded API key"));
			findings.addAll(findAssignmentSecret(line, filePath, JWT_SECRET_PATTERN, "JWT secret in source code"));

			if (PRIVATE_KEY_PATTERN.matcher(line.content()).find()) {
				findings.add(new FindingResult(
					"Private key block committed to source",
					"A private key header was found in the submitted code.",
					Severity.CRITICAL,
					FindingCategory.HARDCODED_SECRET,
					owaspMappingService.secretsExposure(),
					filePath,
					line.lineNumber(),
					"-----BEGIN **** PRIVATE KEY-----",
					"Remove private keys from source code, rotate the key, and load secrets from a secure vault."
				));
			}
		}

		return findings;
	}

	private List<FindingResult> findAssignmentSecret(
		CodeLine line,
		String filePath,
		Pattern pattern,
		String title
	) {
		List<FindingResult> findings = new ArrayList<>();
		Matcher matcher = pattern.matcher(line.content());

		while (matcher.find()) {
			String secretValue = matcher.group(2);
			String maskedEvidence = line.content().replace(secretValue, MaskingUtil.maskSensitiveValue(secretValue));

			findings.add(new FindingResult(
				title,
				"A sensitive value appears to be hardcoded in the submitted code.",
				Severity.CRITICAL,
				FindingCategory.HARDCODED_SECRET,
				owaspMappingService.secretsExposure(),
				filePath,
				line.lineNumber(),
				maskedEvidence.trim(),
				"Move this value to environment variables or a secrets manager, then rotate the exposed secret."
			));
		}

		return findings;
	}
}
