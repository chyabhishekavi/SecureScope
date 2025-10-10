package com.securescope.project.dto;

import com.securescope.common.enums.ScanSourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProjectRequest(
	@NotBlank(message = "Project name is required")
	@Size(max = 140, message = "Project name must be 140 characters or fewer")
	String name,

	@Size(max = 800, message = "Description must be 800 characters or fewer")
	String description,

	@NotNull(message = "Source type is required")
	ScanSourceType sourceType,

	@Size(max = 120, message = "Technology must be 120 characters or fewer")
	String technology,

	@Size(max = 500, message = "GitHub URL must be 500 characters or fewer")
	String githubUrl
) {
}
