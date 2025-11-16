package com.securescope.scanner.zip.dto;

import java.time.Instant;
import java.util.UUID;

public record ZipUploadResponse(
	UUID uploadId,
	UUID projectId,
	String fileName,
	long fileSizeBytes,
	int extractedFileCount,
	int skippedEntryCount,
	Instant uploadedAt
) {
}
