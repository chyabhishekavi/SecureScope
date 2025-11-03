package com.securescope.finding.dto;

import com.securescope.common.enums.FindingStatus;
import jakarta.validation.constraints.NotNull;

public record FindingStatusUpdateRequest(
	@NotNull(message = "Finding status is required")
	FindingStatus status
) {
}
