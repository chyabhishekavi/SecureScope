package com.securescope.finding.dto;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.FindingStatus;
import com.securescope.common.enums.Severity;
import com.securescope.persistence.entity.Finding;
import java.time.Instant;
import java.util.UUID;

public record FindingResponse(
	UUID id,
	UUID scanId,
	String scanName,
	String title,
	String description,
	Severity severity,
	FindingCategory category,
	FindingStatus status,
	String owaspCategory,
	String filePath,
	Integer lineNumber,
	String evidence,
	String recommendation,
	Instant createdAt,
	Instant updatedAt
) {

	public static FindingResponse from(Finding finding) {
		return new FindingResponse(
			finding.getId(),
			finding.getScan().getId(),
			finding.getScan().getScanName(),
			finding.getTitle(),
			finding.getDescription(),
			finding.getSeverity(),
			finding.getCategory(),
			finding.getStatus(),
			finding.getOwaspCategory(),
			finding.getFilePath(),
			finding.getLineNumber(),
			finding.getEvidence(),
			finding.getRecommendation(),
			finding.getCreatedAt(),
			finding.getUpdatedAt()
		);
	}
}
