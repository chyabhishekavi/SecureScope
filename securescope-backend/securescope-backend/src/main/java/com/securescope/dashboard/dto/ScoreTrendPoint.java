package com.securescope.dashboard.dto;

import java.time.Instant;
import java.util.UUID;

public record ScoreTrendPoint(
	UUID scanId,
	String scanName,
	int securityScore,
	Instant completedAt
) {
}
