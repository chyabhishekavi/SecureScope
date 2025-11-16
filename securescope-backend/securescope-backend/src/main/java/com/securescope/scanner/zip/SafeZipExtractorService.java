package com.securescope.scanner.zip;

import com.securescope.common.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Service;

@Service
public class SafeZipExtractorService {

	private static final long MAX_EXTRACTED_BYTES = 25L * 1024L * 1024L;
	private static final int MAX_EXTRACTED_FILES = 1_000;
	private static final Set<String> IGNORED_FOLDERS = Set.of(
		"node_modules",
		"target",
		"build",
		"dist",
		".git",
		".idea",
		".vscode"
	);
	private static final Set<String> SUPPORTED_EXTENSIONS = Set.of(
		".java",
		".ts",
		".js",
		".json",
		".yml",
		".yaml",
		".properties",
		".xml",
		".env"
	);

	public ZipExtractionResult extractSupportedFiles(InputStream inputStream, Path targetDirectory) {
		List<ExtractedZipFile> extractedFiles = new ArrayList<>();
		int skippedEntries = 0;
		long totalExtractedBytes = 0;

		try (ZipInputStream zipInputStream = new ZipInputStream(inputStream)) {
			ZipEntry entry;
			while ((entry = zipInputStream.getNextEntry()) != null) {
				String entryName = normalizeEntryName(entry.getName());

				if (entryName.isBlank() || shouldIgnore(entryName)) {
					skippedEntries++;
					continue;
				}

				Path entryPath = resolveEntryPath(targetDirectory, entryName);

				if (entry.isDirectory()) {
					Files.createDirectories(entryPath);
					continue;
				}

				if (!isSupportedFile(entryName)) {
					skippedEntries++;
					continue;
				}

				if (extractedFiles.size() >= MAX_EXTRACTED_FILES) {
					throw new BadRequestException("ZIP contains too many supported files");
				}

				Files.createDirectories(entryPath.getParent());
				long copiedBytes = Files.copy(zipInputStream, entryPath, StandardCopyOption.REPLACE_EXISTING);
				totalExtractedBytes += copiedBytes;

				if (totalExtractedBytes > MAX_EXTRACTED_BYTES) {
					throw new BadRequestException("ZIP extracted content is too large");
				}

				extractedFiles.add(new ExtractedZipFile(entryPath, entryName));
			}
		} catch (IOException exception) {
			throw new BadRequestException("Unable to read ZIP file");
		}

		return new ZipExtractionResult(extractedFiles, skippedEntries);
	}

	public Path resolveEntryPath(Path targetDirectory, String entryName) {
		Path normalizedTarget = targetDirectory.toAbsolutePath().normalize();
		Path resolvedPath = normalizedTarget.resolve(normalizeEntryName(entryName)).normalize();

		if (!resolvedPath.startsWith(normalizedTarget)) {
			throw new BadRequestException("ZIP file contains an unsafe path");
		}

		return resolvedPath;
	}

	public boolean shouldIgnore(String entryName) {
		String[] pathSegments = normalizeEntryName(entryName).split("/");

		for (String pathSegment : pathSegments) {
			if (IGNORED_FOLDERS.contains(pathSegment)) {
				return true;
			}
		}

		return false;
	}

	public boolean isSupportedFile(String entryName) {
		String normalizedName = normalizeEntryName(entryName).toLowerCase(Locale.ROOT);

		if (normalizedName.endsWith(".env")) {
			return true;
		}

		for (String extension : SUPPORTED_EXTENSIONS) {
			if (normalizedName.endsWith(extension)) {
				return true;
			}
		}

		return false;
	}

	private String normalizeEntryName(String entryName) {
		return entryName == null ? "" : entryName.replace('\\', '/');
	}
}
