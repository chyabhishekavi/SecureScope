package com.securescope.dashboard;

import com.securescope.common.enums.Severity;
import com.securescope.dashboard.dto.DashboardMetric;
import com.securescope.dashboard.dto.DashboardSummaryResponse;
import com.securescope.dashboard.dto.RecentScanResponse;
import com.securescope.dashboard.dto.ScoreTrendPoint;
import com.securescope.persistence.entity.Finding;
import com.securescope.persistence.entity.Scan;
import com.securescope.persistence.repository.FindingRepository;
import com.securescope.persistence.repository.ProjectRepository;
import com.securescope.persistence.repository.ScanRepository;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

	private final ProjectRepository projectRepository;
	private final ScanRepository scanRepository;
	private final FindingRepository findingRepository;

	public DashboardService(
		ProjectRepository projectRepository,
		ScanRepository scanRepository,
		FindingRepository findingRepository
	) {
		this.projectRepository = projectRepository;
		this.scanRepository = scanRepository;
		this.findingRepository = findingRepository;
	}

	@Transactional(readOnly = true)
	public DashboardSummaryResponse getSummary(String userEmail) {
		List<Scan> scans = scanRepository.findByRequestedByEmailOrderByCreatedAtDesc(userEmail);
		List<Finding> findings = findingRepository.findByScanRequestedByEmail(userEmail);

		long criticalFindings = countBySeverity(findings, Severity.CRITICAL);
		long highFindings = countBySeverity(findings, Severity.HIGH);
		int averageSecurityScore = averageScore(scans);
		List<RecentScanResponse> recentScans = scanRepository.findTop5ByRequestedByEmailOrderByCreatedAtDesc(userEmail)
			.stream()
			.map(RecentScanResponse::from)
			.toList();

		return new DashboardSummaryResponse(
			projectRepository.countByOwnerEmail(userEmail),
			scans.size(),
			averageSecurityScore,
			criticalFindings,
			highFindings,
			recentScans
		);
	}

	@Transactional(readOnly = true)
	public List<DashboardMetric> getSeveritySummary(String userEmail) {
		List<Finding> findings = findingRepository.findByScanRequestedByEmail(userEmail);
		Map<Severity, Long> counts = new EnumMap<>(Severity.class);

		for (Severity severity : Severity.values()) {
			counts.put(severity, 0L);
		}

		for (Finding finding : findings) {
			counts.computeIfPresent(finding.getSeverity(), (key, count) -> count + 1);
		}

		List<DashboardMetric> summary = new ArrayList<>();
		for (Severity severity : Severity.values()) {
			summary.add(new DashboardMetric(severity.name(), counts.get(severity)));
		}

		return summary;
	}

	@Transactional(readOnly = true)
	public List<DashboardMetric> getOwaspSummary(String userEmail) {
		Map<String, Long> counts = new java.util.TreeMap<>();

		for (Finding finding : findingRepository.findByScanRequestedByEmail(userEmail)) {
			String category = finding.getOwaspCategory() == null || finding.getOwaspCategory().isBlank()
				? "Unmapped"
				: finding.getOwaspCategory();
			counts.merge(category, 1L, Long::sum);
		}

		return counts.entrySet()
			.stream()
			.map(entry -> new DashboardMetric(entry.getKey(), entry.getValue()))
			.toList();
	}

	@Transactional(readOnly = true)
	public List<ScoreTrendPoint> getScoreTrend(String userEmail) {
		return scanRepository.findByRequestedByEmailOrderByCreatedAtDesc(userEmail)
			.stream()
			.sorted(Comparator.comparing(scan -> scan.getCompletedAt() == null ? scan.getCreatedAt() : scan.getCompletedAt()))
			.map(scan -> new ScoreTrendPoint(
				scan.getId(),
				scan.getScanName(),
				scan.getSecurityScore(),
				scan.getCompletedAt()
			))
			.toList();
	}

	private long countBySeverity(List<Finding> findings, Severity severity) {
		return findings.stream()
			.filter(finding -> finding.getSeverity() == severity)
			.count();
	}

	private int averageScore(List<Scan> scans) {
		if (scans.isEmpty()) {
			return 100;
		}

		double average = scans.stream()
			.mapToInt(Scan::getSecurityScore)
			.average()
			.orElse(100);

		return (int) Math.round(average);
	}
}
