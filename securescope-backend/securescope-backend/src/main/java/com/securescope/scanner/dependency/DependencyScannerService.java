package com.securescope.scanner.dependency;

import com.securescope.common.enums.FindingCategory;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.owasp.OwaspMappingService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class DependencyScannerService {

	private final MavenPomDependencyParser mavenPomDependencyParser;
	private final PackageJsonDependencyParser packageJsonDependencyParser;
	private final MockDependencyVulnerabilityDatabase vulnerabilityDatabase;
	private final OwaspMappingService owaspMappingService;

	public DependencyScannerService(
		MavenPomDependencyParser mavenPomDependencyParser,
		PackageJsonDependencyParser packageJsonDependencyParser,
		MockDependencyVulnerabilityDatabase vulnerabilityDatabase,
		OwaspMappingService owaspMappingService
	) {
		this.mavenPomDependencyParser = mavenPomDependencyParser;
		this.packageJsonDependencyParser = packageJsonDependencyParser;
		this.vulnerabilityDatabase = vulnerabilityDatabase;
		this.owaspMappingService = owaspMappingService;
	}

	public List<FindingResult> scan(String filePath, String content) {
		if (filePath.endsWith("pom.xml")) {
			return scanDependencies(filePath, mavenPomDependencyParser.parse(content));
		}
		if (filePath.endsWith("package.json")) {
			return scanDependencies(filePath, packageJsonDependencyParser.parse(content));
		}

		return List.of();
	}

	private List<FindingResult> scanDependencies(String filePath, List<DependencyDescriptor> dependencies) {
		return dependencies.stream()
			.flatMap(dependency -> vulnerabilityDatabase.findVulnerability(dependency)
				.map(vulnerability -> toFinding(filePath, dependency, vulnerability))
				.stream())
			.toList();
	}

	private FindingResult toFinding(
		String filePath,
		DependencyDescriptor dependency,
		DependencyVulnerability vulnerability
	) {
		String packageName = dependency.name();
		String evidence = "Package: %s | Current version: %s | Vulnerability: %s | Fixed version: %s".formatted(
			packageName,
			dependency.version(),
			vulnerability.vulnerabilityId(),
			vulnerability.fixedVersion()
		);
		String recommendation = "%s Fixed version: %s.".formatted(
			vulnerability.recommendation(),
			vulnerability.fixedVersion()
		);

		return new FindingResult(
			"Vulnerable dependency: " + packageName,
			"%s %s is affected by %s.".formatted(packageName, dependency.version(), vulnerability.vulnerabilityId()),
			vulnerability.severity(),
			FindingCategory.VULNERABLE_DEPENDENCY,
			owaspMappingService.softwareSupplyChainFailures(),
			filePath,
			1,
			evidence,
			recommendation
		);
	}
}
