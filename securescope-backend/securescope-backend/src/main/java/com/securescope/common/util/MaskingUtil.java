package com.securescope.common.util;

public final class MaskingUtil {

	private static final int VISIBLE_PREFIX_LENGTH = 4;
	private static final int VISIBLE_SUFFIX_LENGTH = 4;

	private MaskingUtil() {
	}

	public static String maskSensitiveValue(String value) {
		if (value == null || value.isBlank()) {
			return "";
		}

		if (value.length() <= VISIBLE_PREFIX_LENGTH + VISIBLE_SUFFIX_LENGTH) {
			return "****";
		}

		String prefix = value.substring(0, VISIBLE_PREFIX_LENGTH);
		String suffix = value.substring(value.length() - VISIBLE_SUFFIX_LENGTH);

		return prefix + "****" + suffix;
	}
}
