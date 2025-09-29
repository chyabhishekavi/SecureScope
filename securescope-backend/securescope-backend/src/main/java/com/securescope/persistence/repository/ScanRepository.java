package com.securescope.persistence.repository;

import com.securescope.common.enums.ScanStatus;
import com.securescope.persistence.entity.Scan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRepository extends JpaRepository<Scan, UUID> {

	List<Scan> findByProjectId(UUID projectId);

	List<Scan> findByRequestedById(UUID requestedById);

	Optional<Scan> findByIdAndRequestedByEmail(UUID id, String email);

	List<Scan> findByRequestedByEmailOrderByCreatedAtDesc(String email);

	List<Scan> findByStatus(ScanStatus status);
}
