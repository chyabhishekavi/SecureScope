package com.securescope.auth.security;

import com.securescope.common.exception.UnauthorizedException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

	private static final String HMAC_SHA256 = "HmacSHA256";
	private static final Pattern SUBJECT_PATTERN = Pattern.compile("\"sub\":\"([^\"]+)\"");
	private static final Pattern EXPIRATION_PATTERN = Pattern.compile("\"exp\":(\\d+)");

	private final String secret;
	private final long expirationSeconds;

	public JwtService(
		@Value("${security.jwt.secret}") String secret,
		@Value("${security.jwt.expiration-minutes}") long expirationMinutes
	) {
		this.secret = secret;
		this.expirationSeconds = expirationMinutes * 60;
	}

	public String generateToken(String email) {
		long issuedAt = Instant.now().getEpochSecond();
		long expiresAt = issuedAt + expirationSeconds;

		String header = "{\"alg\":\"HS256\",\"typ\":\"JWT\"}";
		String payload = "{\"sub\":\"" + escapeJson(email) + "\",\"iat\":" + issuedAt + ",\"exp\":" + expiresAt + "}";
		String unsignedToken = base64Url(header) + "." + base64Url(payload);

		return unsignedToken + "." + sign(unsignedToken);
	}

	public String extractEmail(String token) {
		String payload = decodePayload(token);
		Matcher matcher = SUBJECT_PATTERN.matcher(payload);

		if (!matcher.find()) {
			throw new UnauthorizedException("Invalid token subject");
		}

		return matcher.group(1);
	}

	public boolean isTokenValid(String token, String expectedEmail) {
		try {
			if (!isSignatureValid(token)) {
				return false;
			}

			String payload = decodePayload(token);
			String email = extractEmail(token);
			long expiresAt = extractExpiration(payload);

			return expectedEmail.equals(email) && Instant.now().getEpochSecond() < expiresAt;
		} catch (RuntimeException exception) {
			return false;
		}
	}

	private boolean isSignatureValid(String token) {
		String[] parts = splitToken(token);
		String unsignedToken = parts[0] + "." + parts[1];
		String expectedSignature = sign(unsignedToken);

		return MessageDigest.isEqual(
			expectedSignature.getBytes(StandardCharsets.UTF_8),
			parts[2].getBytes(StandardCharsets.UTF_8)
		);
	}

	private long extractExpiration(String payload) {
		Matcher matcher = EXPIRATION_PATTERN.matcher(payload);

		if (!matcher.find()) {
			throw new UnauthorizedException("Invalid token expiration");
		}

		return Long.parseLong(matcher.group(1));
	}

	private String decodePayload(String token) {
		String[] parts = splitToken(token);
		byte[] decoded = Base64.getUrlDecoder().decode(parts[1]);

		return new String(decoded, StandardCharsets.UTF_8);
	}

	private String[] splitToken(String token) {
		String[] parts = token.split("\\.");

		if (parts.length != 3) {
			throw new UnauthorizedException("Invalid token format");
		}

		return parts;
	}

	private String sign(String value) {
		try {
			Mac mac = Mac.getInstance(HMAC_SHA256);
			SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
			mac.init(secretKey);

			return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to sign JWT token", exception);
		}
	}

	private String base64Url(String value) {
		return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
	}

	private String escapeJson(String value) {
		return value.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
