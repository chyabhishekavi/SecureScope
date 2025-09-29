package com.securescope.scanner.dto;

import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.ScanStatus;
import java.util.List;

public record ScanResult(
	String scanId,
	ScanStatus status,
	int securityScore,
	RiskLevel riskLevel,
	int totalFindings,
	List<FindingResult> findings
) {
}
