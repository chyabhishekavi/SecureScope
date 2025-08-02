package com.securescope.common.response;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ApiResponseTest {

	@Test
	void successBuildsSuccessfulResponse() {
		ApiResponse<String> response = ApiResponse.success("Request completed", "result");

		assertThat(response.success()).isTrue();
		assertThat(response.message()).isEqualTo("Request completed");
		assertThat(response.data()).isEqualTo("result");
		assertThat(response.timestamp()).isNotNull();
	}

	@Test
	void errorBuildsFailedResponse() {
		ApiResponse<Void> response = ApiResponse.error("Request failed");

		assertThat(response.success()).isFalse();
		assertThat(response.message()).isEqualTo("Request failed");
		assertThat(response.data()).isNull();
		assertThat(response.timestamp()).isNotNull();
	}
}
