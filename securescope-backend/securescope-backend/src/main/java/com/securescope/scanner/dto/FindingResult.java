package com.securescope.scanner.dto;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.Severity;

public record FindingResult(
	String title,
	String description,
	Severity severity,
	FindingCategory category,
	String owaspCategory,
	String filePath,
	Integer lineNumber,
	String evidence,
	String recommendation
) {
}
