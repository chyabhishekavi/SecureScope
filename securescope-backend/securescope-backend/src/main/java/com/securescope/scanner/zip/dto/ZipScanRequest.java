package com.securescope.scanner.zip.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ZipScanRequest(
	@NotNull(message = "Upload id is required")
	UUID uploadId
) {
}
