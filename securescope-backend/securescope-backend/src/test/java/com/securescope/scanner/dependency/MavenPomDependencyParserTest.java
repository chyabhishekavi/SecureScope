package com.securescope.scanner.dependency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import org.junit.jupiter.api.Test;

class MavenPomDependencyParserTest {

	private final MavenPomDependencyParser parser = new MavenPomDependencyParser();

	@Test
	void parseExtractsDependencyNameAndVersionFromPomXml() {
		String pomXml = """
			<project>
			  <dependencies>
			    <dependency>
			      <groupId>org.apache.logging.log4j</groupId>
			      <artifactId>log4j-core</artifactId>
			      <version>2.14.1</version>
			    </dependency>
			    <dependency>
			      <groupId>org.springframework</groupId>
			      <artifactId>spring-core</artifactId>
			      <version>${spring.version}</version>
			    </dependency>
			  </dependencies>
			</project>
			""";

		assertThat(parser.parse(pomXml))
			.extracting(DependencyDescriptor::name, DependencyDescriptor::version, DependencyDescriptor::ecosystem)
			.containsExactly(tuple("org.apache.logging.log4j:log4j-core", "2.14.1", "Maven"));
	}
}
