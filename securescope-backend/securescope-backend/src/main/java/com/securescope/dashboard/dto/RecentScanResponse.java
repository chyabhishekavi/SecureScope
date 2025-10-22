package com.securescope.dashboard.dto;

import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.ScanStatus;
import com.securescope.persistence.entity.Scan;
import java.time.Instant;
import java.util.UUID;

public record RecentScanResponse(
	UUID id,
	String scanName,
	ScanStatus status,
	int securityScore,
	RiskLevel riskLevel,
	int totalFindings,
	Instant completedAt
) {

	public static RecentScanResponse from(Scan scan) {
		return new RecentScanResponse(
			scan.getId(),
			scan.getScanName(),
			scan.getStatus(),
			scan.getSecurityScore(),
			scan.getRiskLevel(),
			scan.getFindings().size(),
			scan.getCompletedAt()
		);
	}
}
