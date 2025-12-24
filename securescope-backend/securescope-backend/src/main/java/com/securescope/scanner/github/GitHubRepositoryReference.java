package com.securescope.scanner.github;

public record GitHubRepositoryReference(
	String owner,
	String repository,
	String normalizedUrl,
	String zipballApiUrl
) {
	public String displayName() {
		return owner + "/" + repository;
	}
}
