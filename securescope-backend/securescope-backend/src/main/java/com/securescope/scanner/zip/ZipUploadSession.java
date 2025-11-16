package com.securescope.scanner.zip;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ZipUploadSession(
	UUID uploadId,
	UUID projectId,
	String userEmail,
	String fileName,
	long fileSizeBytes,
	Path extractionDirectory,
	List<ExtractedZipFile> files,
	int skippedEntryCount,
	Instant uploadedAt
) {
}
