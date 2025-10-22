package com.securescope.dashboard;

import com.securescope.common.response.ApiResponse;
import com.securescope.dashboard.dto.DashboardMetric;
import com.securescope.dashboard.dto.DashboardSummaryResponse;
import com.securescope.dashboard.dto.ScoreTrendPoint;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

	private final DashboardService dashboardService;

	public DashboardController(DashboardService dashboardService) {
		this.dashboardService = dashboardService;
	}

	@GetMapping("/summary")
	public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getSummary(Principal principal) {
		DashboardSummaryResponse summary = dashboardService.getSummary(principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Dashboard summary loaded", summary));
	}

	@GetMapping("/severity-summary")
	public ResponseEntity<ApiResponse<List<DashboardMetric>>> getSeveritySummary(Principal principal) {
		List<DashboardMetric> summary = dashboardService.getSeveritySummary(principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Severity summary loaded", summary));
	}

	@GetMapping("/owasp-summary")
	public ResponseEntity<ApiResponse<List<DashboardMetric>>> getOwaspSummary(Principal principal) {
		List<DashboardMetric> summary = dashboardService.getOwaspSummary(principal.getName());

		return ResponseEntity.ok(ApiResponse.success("OWASP summary loaded", summary));
	}

	@GetMapping("/score-trend")
	public ResponseEntity<ApiResponse<List<ScoreTrendPoint>>> getScoreTrend(Principal principal) {
		List<ScoreTrendPoint> trend = dashboardService.getScoreTrend(principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Score trend loaded", trend));
	}
}
