package com.securescope.report.dto;

import com.securescope.persistence.entity.Report;
import java.time.Instant;
import java.util.UUID;

public record ReportResponse(
	UUID id,
	UUID scanId,
	String title,
	String format,
	Instant generatedAt,
	int findingCount,
	String downloadUrl
) {

	public static ReportResponse from(Report report) {
		return new ReportResponse(
			report.getId(),
			report.getScan().getId(),
			report.getTitle(),
			report.getFormat(),
			report.getGeneratedAt(),
			report.getFindingCount(),
			"/api/reports/" + report.getId() + "/download"
		);
	}
}
