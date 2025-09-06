package com.securescope.auth.security;

import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class SecureScopeUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public SecureScopeUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) {
		User user = userRepository.findByEmail(email)
			.orElseThrow(() -> new UsernameNotFoundException("User not found"));

		return new org.springframework.security.core.userdetails.User(
			user.getEmail(),
			user.getPasswordHash(),
			List.of()
		);
	}
}
