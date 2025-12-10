package com.securescope.persistence.repository;

import com.securescope.persistence.entity.Report;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, UUID> {

	List<Report> findByScanIdAndScanRequestedByEmailOrderByGeneratedAtDesc(UUID scanId, String email);

	Optional<Report> findByIdAndScanRequestedByEmail(UUID id, String email);
}
