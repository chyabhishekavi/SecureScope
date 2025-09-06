package com.securescope.auth;

import com.securescope.auth.dto.AuthResponse;
import com.securescope.auth.dto.AuthUserResponse;
import com.securescope.auth.dto.LoginRequest;
import com.securescope.auth.dto.RegisterRequest;
import com.securescope.auth.security.JwtService;
import com.securescope.common.exception.BadRequestException;
import com.securescope.common.exception.ResourceNotFoundException;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
		UserRepository userRepository,
		PasswordEncoder passwordEncoder,
		AuthenticationManager authenticationManager,
		JwtService jwtService
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	public AuthResponse register(RegisterRequest request) {
		String normalizedEmail = normalizeEmail(request.email());

		if (userRepository.existsByEmail(normalizedEmail)) {
			throw new BadRequestException("Email is already registered");
		}

		User user = new User();
		user.setFullName(request.name().trim());
		user.setEmail(normalizedEmail);
		user.setPasswordHash(passwordEncoder.encode(request.password()));

		User savedUser = userRepository.save(user);
		String token = jwtService.generateToken(savedUser.getEmail());

		return AuthResponse.bearer(token, AuthUserResponse.from(savedUser));
	}

	public AuthResponse login(LoginRequest request) {
		String normalizedEmail = normalizeEmail(request.email());

		try {
			authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
			);
		} catch (AuthenticationException exception) {
			throw new com.securescope.common.exception.UnauthorizedException("Invalid email or password");
		}

		User user = userRepository.findByEmail(normalizedEmail)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		String token = jwtService.generateToken(user.getEmail());

		return AuthResponse.bearer(token, AuthUserResponse.from(user));
	}

	public AuthUserResponse getCurrentUser(String email) {
		User user = userRepository.findByEmail(normalizeEmail(email))
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		return AuthUserResponse.from(user);
	}

	private String normalizeEmail(String email) {
		return email.trim().toLowerCase();
	}
}
