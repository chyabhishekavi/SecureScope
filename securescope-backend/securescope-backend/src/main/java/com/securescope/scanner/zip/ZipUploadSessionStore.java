package com.securescope.scanner.zip;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Component;

@Component
public class ZipUploadSessionStore {

	private final Map<UUID, ZipUploadSession> sessions = new ConcurrentHashMap<>();

	public void save(ZipUploadSession session) {
		sessions.put(session.uploadId(), session);
	}

	public Optional<ZipUploadSession> find(UUID uploadId) {
		return Optional.ofNullable(sessions.get(uploadId));
	}

	public void remove(UUID uploadId) {
		sessions.remove(uploadId);
	}
}
