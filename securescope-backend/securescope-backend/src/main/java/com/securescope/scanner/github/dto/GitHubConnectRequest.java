package com.securescope.scanner.github.dto;

import jakarta.validation.constraints.NotBlank;

public record GitHubConnectRequest(
	@NotBlank(message = "GitHub repository URL is required")
	String repositoryUrl
) {
}
