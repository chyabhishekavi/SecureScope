package com.securescope.scanner.github;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.securescope.common.exception.BadRequestException;
import org.junit.jupiter.api.Test;

class GitHubRepositoryUrlValidatorTest {

	private final GitHubRepositoryUrlValidator validator = new GitHubRepositoryUrlValidator();

	@Test
	void validatesHttpsGithubRepositoryUrl() {
		GitHubRepositoryReference reference = validator.validate("https://github.com/example/securescope");

		assertThat(reference.owner()).isEqualTo("example");
		assertThat(reference.repository()).isEqualTo("securescope");
		assertThat(reference.normalizedUrl()).isEqualTo("https://github.com/example/securescope");
		assertThat(reference.zipballApiUrl()).isEqualTo("https://api.github.com/repos/example/securescope/zipball");
	}

	@Test
	void removesGitSuffixFromRepositoryUrl() {
		GitHubRepositoryReference reference = validator.validate("https://github.com/example/securescope.git");

		assertThat(reference.repository()).isEqualTo("securescope");
		assertThat(reference.normalizedUrl()).isEqualTo("https://github.com/example/securescope");
	}

	@Test
	void rejectsNonGithubUrls() {
		assertThatThrownBy(() -> validator.validate("https://gitlab.com/example/securescope"))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("Only github.com repository URLs are supported");
	}

	@Test
	void rejectsHttpUrls() {
		assertThatThrownBy(() -> validator.validate("http://github.com/example/securescope"))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("GitHub repository URL must use HTTPS");
	}

	@Test
	void rejectsUrlsWithoutOwnerAndRepository() {
		assertThatThrownBy(() -> validator.validate("https://github.com/example"))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("GitHub repository URL must include owner and repository name");
	}
}
