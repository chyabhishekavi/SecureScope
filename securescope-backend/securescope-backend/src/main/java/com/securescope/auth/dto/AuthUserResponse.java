package com.securescope.auth.dto;

import com.securescope.persistence.entity.User;
import java.util.UUID;

public record AuthUserResponse(
	UUID id,
	String name,
	String email
) {

	public static AuthUserResponse from(User user) {
		return new AuthUserResponse(user.getId(), user.getFullName(), user.getEmail());
	}
}
