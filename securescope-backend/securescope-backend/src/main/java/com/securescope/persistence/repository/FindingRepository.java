package com.securescope.persistence.repository;

import com.securescope.common.enums.Severity;
import com.securescope.persistence.entity.Finding;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FindingRepository extends JpaRepository<Finding, UUID> {

	List<Finding> findByScanId(UUID scanId);

	List<Finding> findBySeverity(Severity severity);
}
