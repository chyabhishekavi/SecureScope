package com.securescope.scanner.dto;

import com.securescope.common.enums.RiskLevel;

public record ScoreResult(
	int securityScore,
	RiskLevel riskLevel
) {
}
