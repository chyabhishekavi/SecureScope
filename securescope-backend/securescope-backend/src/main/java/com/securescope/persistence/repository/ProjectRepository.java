package com.securescope.persistence.repository;

import com.securescope.persistence.entity.Project;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, UUID> {

	List<Project> findByOwnerId(UUID ownerId);

	Optional<Project> findByIdAndOwnerEmail(UUID id, String email);
}
