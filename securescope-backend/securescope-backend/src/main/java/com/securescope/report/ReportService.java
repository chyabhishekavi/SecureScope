package com.securescope.report;

import com.securescope.common.exception.ResourceNotFoundException;
import com.securescope.persistence.entity.Report;
import com.securescope.persistence.entity.Scan;
import com.securescope.persistence.repository.ReportRepository;
import com.securescope.persistence.repository.ScanRepository;
import com.securescope.report.dto.ReportPreviewResponse;
import com.securescope.report.dto.ReportResponse;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReportService {

	private final ScanRepository scanRepository;
	private final ReportRepository reportRepository;
	private final ReportHtmlBuilder reportHtmlBuilder;

	public ReportService(
		ScanRepository scanRepository,
		ReportRepository reportRepository,
		ReportHtmlBuilder reportHtmlBuilder
	) {
		this.scanRepository = scanRepository;
		this.reportRepository = reportRepository;
		this.reportHtmlBuilder = reportHtmlBuilder;
	}

	@Transactional
	public ReportPreviewResponse generateReport(UUID scanId, String userEmail) {
		Scan scan = getOwnedScan(scanId, userEmail);
		String htmlContent = reportHtmlBuilder.build(scan);

		Report report = new Report();
		report.setTitle("SecureScope Report - " + scan.getScanName());
		report.setFormat("HTML");
		report.setGeneratedAt(Instant.now());
		report.setFindingCount(scan.getFindings().size());
		report.setHtmlContent(htmlContent);
		report.setScan(scan);

		Report savedReport = reportRepository.save(report);
		return new ReportPreviewResponse(ReportResponse.from(savedReport), savedReport.getHtmlContent());
	}

	@Transactional(readOnly = true)
	public List<ReportResponse> getScanReports(UUID scanId, String userEmail) {
		getOwnedScan(scanId, userEmail);
		return reportRepository.findByScanIdAndScanRequestedByEmailOrderByGeneratedAtDesc(scanId, userEmail)
			.stream()
			.map(ReportResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public ReportPreviewResponse getReport(UUID reportId, String userEmail) {
		Report report = getOwnedReport(reportId, userEmail);
		return new ReportPreviewResponse(ReportResponse.from(report), report.getHtmlContent());
	}

	@Transactional(readOnly = true)
	public Report getReportForDownload(UUID reportId, String userEmail) {
		return getOwnedReport(reportId, userEmail);
	}

	private Scan getOwnedScan(UUID scanId, String userEmail) {
		return scanRepository.findByIdAndRequestedByEmail(scanId, userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Scan not found"));
	}

	private Report getOwnedReport(UUID reportId, String userEmail) {
		return reportRepository.findByIdAndScanRequestedByEmail(reportId, userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Report not found"));
	}
}
