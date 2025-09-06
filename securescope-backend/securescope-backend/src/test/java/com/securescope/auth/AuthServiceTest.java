package com.securescope.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.securescope.auth.dto.AuthResponse;
import com.securescope.auth.dto.LoginRequest;
import com.securescope.auth.dto.RegisterRequest;
import com.securescope.auth.security.JwtService;
import com.securescope.common.exception.BadRequestException;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private AuthenticationManager authenticationManager;

	private final JwtService jwtService = new JwtService(
		"test-secret-that-is-long-enough-for-hmac-signing",
		60
	);

	private AuthService authService;

	@Test
	void registerEncryptsPasswordAndReturnsToken() {
		authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService);
		when(userRepository.existsByEmail("dev@securescope.test")).thenReturn(false);
		when(passwordEncoder.encode("password123")).thenReturn("encoded-password");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		AuthResponse response = authService.register(
			new RegisterRequest("Dev User", "DEV@SecureScope.test", "password123")
		);

		ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
		verify(userRepository).save(userCaptor.capture());

		assertThat(userCaptor.getValue().getEmail()).isEqualTo("dev@securescope.test");
		assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("encoded-password");
		assertThat(response.tokenType()).isEqualTo("Bearer");
		assertThat(response.token()).isNotBlank();
		assertThat(response.user().email()).isEqualTo("dev@securescope.test");
	}

	@Test
	void registerRejectsDuplicateEmail() {
		authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService);
		when(userRepository.existsByEmail("dev@securescope.test")).thenReturn(true);

		assertThatThrownBy(() -> authService.register(
			new RegisterRequest("Dev User", "dev@securescope.test", "password123")
		)).isInstanceOf(BadRequestException.class);
	}

	@Test
	void loginAuthenticatesAndReturnsToken() {
		authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService);
		User user = new User();
		user.setFullName("Dev User");
		user.setEmail("dev@securescope.test");
		user.setPasswordHash("encoded-password");
		when(userRepository.findByEmail("dev@securescope.test")).thenReturn(Optional.of(user));

		AuthResponse response = authService.login(
			new LoginRequest("dev@securescope.test", "password123")
		);

		verify(authenticationManager).authenticate(any());
		assertThat(response.token()).isNotBlank();
		assertThat(response.user().email()).isEqualTo("dev@securescope.test");
	}
}
