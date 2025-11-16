package com.securescope.scanner.zip;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.securescope.common.exception.BadRequestException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class SafeZipExtractorServiceTest {

	private final SafeZipExtractorService safeZipExtractorService = new SafeZipExtractorService();

	@TempDir
	private Path tempDirectory;

	@Test
	void resolveEntryPathRejectsZipSlipTraversal() {
		assertThatThrownBy(() -> safeZipExtractorService.resolveEntryPath(tempDirectory, "../secret.txt"))
			.isInstanceOf(BadRequestException.class)
			.hasMessage("ZIP file contains an unsafe path");
	}

	@Test
	void resolveEntryPathAllowsSafeNestedFiles() {
		Path resolvedPath = safeZipExtractorService.resolveEntryPath(tempDirectory, "src/main/App.java");

		assertThat(resolvedPath.toString()).startsWith(tempDirectory.toAbsolutePath().normalize().toString());
		assertThat(resolvedPath.getFileName().toString()).isEqualTo("App.java");
	}

	@Test
	void shouldIgnoreConfiguredBuildAndDependencyFolders() {
		assertThat(safeZipExtractorService.shouldIgnore("app/node_modules/package/index.js")).isTrue();
		assertThat(safeZipExtractorService.shouldIgnore("service/target/classes/App.class")).isTrue();
		assertThat(safeZipExtractorService.shouldIgnore("src/main/App.java")).isFalse();
	}

	@Test
	void supportsOnlyConfiguredSourceFileTypes() {
		assertThat(safeZipExtractorService.isSupportedFile("src/main/App.java")).isTrue();
		assertThat(safeZipExtractorService.isSupportedFile("frontend/src/main.ts")).isTrue();
		assertThat(safeZipExtractorService.isSupportedFile(".env")).isTrue();
		assertThat(safeZipExtractorService.isSupportedFile("image.png")).isFalse();
	}
}
