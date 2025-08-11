package com.securescope.scanner.dto;

import jakarta.validation.constraints.NotBlank;

public record QuickScanRequest(
	String snippetName,
	String language,
	String fileName,
	@NotBlank(message = "Code content is required")
	String codeContent
) {
}
