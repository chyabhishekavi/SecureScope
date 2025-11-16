package com.securescope.scanner.zip;

import java.nio.file.Path;

public record ExtractedZipFile(
	Path absolutePath,
	String relativePath
) {
}
