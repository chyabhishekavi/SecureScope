package com.securescope.scanner.github.dto;

import java.util.UUID;

public record GitHubRepositoryResponse(
	UUID projectId,
	String repositoryUrl,
	String repositoryName
) {
}
