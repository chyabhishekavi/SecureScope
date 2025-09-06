package com.securescope.auth.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class JwtServiceTest {

	private final JwtService jwtService = new JwtService(
		"test-secret-that-is-long-enough-for-hmac-signing",
		60
	);

	@Test
	void generateTokenCreatesValidTokenForEmail() {
		String token = jwtService.generateToken("dev@securescope.test");

		assertThat(jwtService.extractEmail(token)).isEqualTo("dev@securescope.test");
		assertThat(jwtService.isTokenValid(token, "dev@securescope.test")).isTrue();
		assertThat(jwtService.isTokenValid(token, "other@securescope.test")).isFalse();
	}
}
