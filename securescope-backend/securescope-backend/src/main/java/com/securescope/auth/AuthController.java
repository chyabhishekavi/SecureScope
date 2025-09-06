package com.securescope.auth;

import com.securescope.auth.dto.AuthResponse;
import com.securescope.auth.dto.AuthUserResponse;
import com.securescope.auth.dto.LoginRequest;
import com.securescope.auth.dto.RegisterRequest;
import com.securescope.common.response.ApiResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/register")
	public ResponseEntity<ApiResponse<AuthResponse>> register(
		@Valid @RequestBody RegisterRequest request
	) {
		AuthResponse authResponse = authService.register(request);

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Registration successful", authResponse));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<AuthResponse>> login(
		@Valid @RequestBody LoginRequest request
	) {
		AuthResponse authResponse = authService.login(request);

		return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
	}

	@GetMapping("/me")
	public ResponseEntity<ApiResponse<AuthUserResponse>> me(Principal principal) {
		AuthUserResponse user = authService.getCurrentUser(principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Current user loaded", user));
	}
}
