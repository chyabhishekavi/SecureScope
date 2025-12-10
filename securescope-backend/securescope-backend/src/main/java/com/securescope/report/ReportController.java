package com.securescope.report;

import com.securescope.common.response.ApiResponse;
import com.securescope.persistence.entity.Report;
import com.securescope.report.dto.ReportPreviewResponse;
import com.securescope.report.dto.ReportResponse;
import java.nio.charset.StandardCharsets;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReportController {

	private final ReportService reportService;

	public ReportController(ReportService reportService) {
		this.reportService = reportService;
	}

	@PostMapping("/api/scans/{scanId}/reports")
	public ResponseEntity<ApiResponse<ReportPreviewResponse>> generateReport(
		@PathVariable UUID scanId,
		Principal principal
	) {
		ReportPreviewResponse report = reportService.generateReport(scanId, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Report generated", report));
	}

	@GetMapping("/api/scans/{scanId}/reports")
	public ResponseEntity<ApiResponse<List<ReportResponse>>> getScanReports(
		@PathVariable UUID scanId,
		Principal principal
	) {
		List<ReportResponse> reports = reportService.getScanReports(scanId, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Reports loaded", reports));
	}

	@GetMapping("/api/reports/{reportId}")
	public ResponseEntity<ApiResponse<ReportPreviewResponse>> getReport(
		@PathVariable UUID reportId,
		Principal principal
	) {
		ReportPreviewResponse report = reportService.getReport(reportId, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Report loaded", report));
	}

	@GetMapping("/api/reports/{reportId}/download")
	public ResponseEntity<byte[]> downloadReport(
		@PathVariable UUID reportId,
		Principal principal
	) {
		Report report = reportService.getReportForDownload(reportId, principal.getName());
		String fileName = "securescope-report-" + report.getId() + ".html";

		return ResponseEntity.ok()
			.contentType(MediaType.TEXT_HTML)
			.header(
				HttpHeaders.CONTENT_DISPOSITION,
				ContentDisposition.attachment().filename(fileName).build().toString()
			)
			.body(report.getHtmlContent().getBytes(StandardCharsets.UTF_8));
	}
}
