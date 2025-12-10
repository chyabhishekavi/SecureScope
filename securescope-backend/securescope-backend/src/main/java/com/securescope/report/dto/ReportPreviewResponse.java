package com.securescope.report.dto;

public record ReportPreviewResponse(
	ReportResponse report,
	String htmlContent
) {
}
