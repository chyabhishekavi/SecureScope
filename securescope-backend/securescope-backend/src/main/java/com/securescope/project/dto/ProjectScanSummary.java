package com.securescope.project.dto;

import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.ScanStatus;
import com.securescope.persistence.entity.Scan;
import java.time.Instant;
import java.util.UUID;

public record ProjectScanSummary(
	UUID id,
	String scanName,
	ScanStatus status,
	int securityScore,
	RiskLevel riskLevel,
	int totalFindings,
	Instant startedAt,
	Instant completedAt
) {

	public static ProjectScanSummary from(Scan scan) {
		return new ProjectScanSummary(
			scan.getId(),
			scan.getScanName(),
			scan.getStatus(),
			scan.getSecurityScore(),
			scan.getRiskLevel(),
			scan.getFindings().size(),
			scan.getStartedAt(),
			scan.getCompletedAt()
		);
	}
}
