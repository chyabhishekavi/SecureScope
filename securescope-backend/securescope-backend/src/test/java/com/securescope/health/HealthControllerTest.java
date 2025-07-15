package com.securescope.health;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class HealthControllerTest {

	private final HealthController healthController = new HealthController();

	@Test
	void getHealthReturnsBackendStatus() {
		HealthResponse response = healthController.getHealth();

		assertThat(response.status()).isEqualTo("UP");
		assertThat(response.message()).isEqualTo("SecureScope backend is running");
	}
}
