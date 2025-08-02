package com.securescope.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MaskingUtilTest {

	@Test
	void maskSensitiveValueKeepsOnlyPrefixAndSuffix() {
		String maskedValue = MaskingUtil.maskSensitiveValue("sk_live_1234567890");

		assertThat(maskedValue).isEqualTo("sk_l****7890");
	}

	@Test
	void maskSensitiveValueMasksShortValuesCompletely() {
		String maskedValue = MaskingUtil.maskSensitiveValue("secret");

		assertThat(maskedValue).isEqualTo("****");
	}
}
