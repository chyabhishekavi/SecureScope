package com.securescope.dashboard.dto;

import java.util.List;

public record DashboardSummaryResponse(
	long totalProjects,
	long totalScans,
	int averageSecurityScore,
	long criticalFindings,
	long highFindings,
	List<RecentScanResponse> recentScans
) {
}
