package com.securescope.persistence.repository;

import com.securescope.common.enums.Severity;
import com.securescope.persistence.entity.Finding;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingRepository extends JpaRepository<Finding, UUID> {

	List<Finding> findByScanId(UUID scanId);

	List<Finding> findByScanIdOrderByCreatedAtAsc(UUID scanId);

	List<Finding> findByScanRequestedByEmail(String email);

	Optional<Finding> findByIdAndScanRequestedByEmail(UUID id, String email);

	List<Finding> findBySeverity(Severity severity);
}
