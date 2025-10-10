package com.securescope.project.dto;

import com.securescope.common.enums.ScanSourceType;
import com.securescope.persistence.entity.Project;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProjectResponse(
	UUID id,
	String name,
	String description,
	ScanSourceType sourceType,
	String technology,
	String githubUrl,
	Instant createdAt,
	Instant updatedAt,
	List<ProjectScanSummary> scanHistory
) {

	public static ProjectResponse from(Project project, List<ProjectScanSummary> scanHistory) {
		return new ProjectResponse(
			project.getId(),
			project.getName(),
			project.getDescription(),
			project.getSourceType(),
			project.getTechnology(),
			project.getGithubUrl(),
			project.getCreatedAt(),
			project.getUpdatedAt(),
			scanHistory
		);
	}
}
