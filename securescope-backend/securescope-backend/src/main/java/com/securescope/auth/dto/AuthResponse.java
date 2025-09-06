package com.securescope.auth.dto;

public record AuthResponse(
	String token,
	String tokenType,
	AuthUserResponse user
) {

	public static AuthResponse bearer(String token, AuthUserResponse user) {
		return new AuthResponse(token, "Bearer", user);
	}
}
