package com.securescope.scanner.orchestrator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.ScanStatus;
import com.securescope.persistence.entity.Scan;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.FindingRepository;
import com.securescope.persistence.repository.ProjectRepository;
import com.securescope.persistence.repository.ScanRepository;
import com.securescope.persistence.repository.UserRepository;
import com.securescope.scanner.dto.QuickScanRequest;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.owasp.OwaspMappingService;
import com.securescope.scanner.pattern.RiskyPatternScannerService;
import com.securescope.scanner.scoring.SecurityScoringService;
import com.securescope.scanner.secret.SecretScannerService;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class QuickCodeScannerServiceTest {

	private final OwaspMappingService owaspMappingService = new OwaspMappingService();
	private final UserRepository userRepository = org.mockito.Mockito.mock(UserRepository.class);
	private final ProjectRepository projectRepository = org.mockito.Mockito.mock(ProjectRepository.class);
	private final ScanRepository scanRepository = org.mockito.Mockito.mock(ScanRepository.class);
	private final FindingRepository findingRepository = org.mockito.Mockito.mock(FindingRepository.class);
	private final QuickCodeScannerService quickCodeScannerService = buildService();

	@Test
	void scanReturnsFindingsScoreAndGeneratedId() {
		User user = new User();
		user.setEmail("developer@example.com");
		when(userRepository.findByEmail("developer@example.com")).thenReturn(Optional.of(user));
		when(scanRepository.save(any(Scan.class))).thenAnswer(invocation -> {
			Scan scan = invocation.getArgument(0);
			ReflectionTestUtils.setField(scan, "id", UUID.randomUUID());
			return scan;
		});

		QuickScanRequest request = new QuickScanRequest(
			"Auth sample",
			"JavaScript",
			"auth.js",
			"const token = \"abcdef1234567890\";\nMessageDigest.getInstance(\"SHA-1\");",
			null
		);

		ScanResult result = quickCodeScannerService.scan(request, "developer@example.com");

		assertThat(result.scanId()).isNotBlank();
		assertThat(result.status()).isEqualTo(ScanStatus.COMPLETED);
		assertThat(result.totalFindings()).isEqualTo(2);
		assertThat(result.securityScore()).isEqualTo(50);
		assertThat(result.riskLevel()).isEqualTo(RiskLevel.MODERATE);
		assertThat(result.findings().get(0).evidence()).doesNotContain("abcdef1234567890");
	}

	private QuickCodeScannerService buildService() {
		return new QuickCodeScannerService(
			new SecretScannerService(owaspMappingService),
			new RiskyPatternScannerService(owaspMappingService),
			new SecurityScoringService(),
			userRepository,
			projectRepository,
			scanRepository,
			findingRepository
		);
	}
}
