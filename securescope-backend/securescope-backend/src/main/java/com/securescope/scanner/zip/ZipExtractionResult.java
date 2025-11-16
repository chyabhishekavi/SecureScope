package com.securescope.scanner.zip;

import java.util.List;

public record ZipExtractionResult(
	List<ExtractedZipFile> files,
	int skippedEntryCount
) {
}
