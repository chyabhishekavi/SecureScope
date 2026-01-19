package com.securescope.scanner.owasp;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class OwaspMappingServiceTest {

	private final OwaspMappingService owaspMappingService = new OwaspMappingService();

	@Test
	void mapsScannerConceptsToOwaspCategories() {
		assertThat(owaspMappingService.secretsExposure()).isEqualTo("A02:2021 - Cryptographic Failures");
		assertThat(owaspMappingService.weakCryptography()).isEqualTo("A02:2021 - Cryptographic Failures");
		assertThat(owaspMappingService.injection()).isEqualTo("A03:2021 - Injection");
		assertThat(owaspMappingService.securityMisconfiguration()).isEqualTo("A05:2021 - Security Misconfiguration");
		assertThat(owaspMappingService.softwareSupplyChainFailures())
			.isEqualTo("A06:2021 - Vulnerable and Outdated Components");
	}
}
