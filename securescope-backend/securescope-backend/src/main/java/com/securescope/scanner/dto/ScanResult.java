package com.securescope.scanner.dto;

import com.securescope.common.enums.RiskLevel;
import java.util.List;

public record ScanResult(
	String scanId,
	int securityScore,
	RiskLevel riskLevel,
	int totalFindings,
	List<FindingResult> findings
) {
}
