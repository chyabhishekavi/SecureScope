package com.securescope.scanner.dependency;

import static org.assertj.core.api.Assertions.assertThat;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.Severity;
import com.securescope.scanner.owasp.OwaspMappingService;
import org.junit.jupiter.api.Test;

class DependencyScannerServiceTest {

	private final DependencyScannerService scannerService = new DependencyScannerService(
		new MavenPomDependencyParser(),
		new PackageJsonDependencyParser(),
		new MockDependencyVulnerabilityDatabase(),
		new OwaspMappingService()
	);

	@Test
	void scanDetectsVulnerablePackageJsonDependency() {
		String packageJson = """
			{
			  "dependencies": {
			    "lodash": "4.17.20"
			  }
			}
			""";

		var findings = scannerService.scan("package.json", packageJson);

		assertThat(findings).hasSize(1);
		assertThat(findings.get(0).category()).isEqualTo(FindingCategory.VULNERABLE_DEPENDENCY);
		assertThat(findings.get(0).severity()).isEqualTo(Severity.HIGH);
		assertThat(findings.get(0).evidence()).contains("lodash", "4.17.20", "CVE-2021-23337");
		assertThat(findings.get(0).owaspCategory()).contains("Vulnerable and Outdated Components");
	}
}
