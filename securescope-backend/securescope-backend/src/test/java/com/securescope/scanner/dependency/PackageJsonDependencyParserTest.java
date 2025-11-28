package com.securescope.scanner.dependency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;

class PackageJsonDependencyParserTest {

	private final PackageJsonDependencyParser parser = new PackageJsonDependencyParser();

	@Test
	void parseExtractsDependenciesAndDevDependenciesFromPackageJson() {
		String packageJson = """
			{
			  "dependencies": {
			    "lodash": "4.17.20",
			    "axios": "^0.21.1"
			  },
			  "devDependencies": {
			    "typescript": "5.9.2"
			  }
			}
			""";

		assertThat(parser.parse(packageJson))
			.extracting(DependencyDescriptor::name, DependencyDescriptor::version, DependencyDescriptor::ecosystem)
			.containsExactlyInAnyOrder(
				tuple("lodash", "4.17.20", "npm"),
				tuple("axios", "^0.21.1", "npm"),
				tuple("typescript", "5.9.2", "npm")
			);
	}
}
