package com.securescope.project;

import com.securescope.common.exception.ResourceNotFoundException;
import com.securescope.persistence.entity.Project;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.ProjectRepository;
import com.securescope.persistence.repository.ScanRepository;
import com.securescope.persistence.repository.UserRepository;
import com.securescope.project.dto.ProjectRequest;
import com.securescope.project.dto.ProjectResponse;
import com.securescope.project.dto.ProjectScanSummary;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {

	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final ScanRepository scanRepository;

	public ProjectService(
		ProjectRepository projectRepository,
		UserRepository userRepository,
		ScanRepository scanRepository
	) {
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
		this.scanRepository = scanRepository;
	}

	@Transactional
	public ProjectResponse create(ProjectRequest request, String userEmail) {
		User owner = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		Project project = new Project();
		project.setOwner(owner);
		applyRequest(project, request);

		Project savedProject = projectRepository.save(project);
		return ProjectResponse.from(savedProject, List.of());
	}

	@Transactional(readOnly = true)
	public List<ProjectResponse> getMyProjects(String userEmail) {
		return projectRepository.findByOwnerEmailOrderByCreatedAtDesc(userEmail)
			.stream()
			.map(project -> ProjectResponse.from(project, getScanHistory(project.getId(), userEmail)))
			.toList();
	}

	@Transactional(readOnly = true)
	public ProjectResponse getProject(UUID projectId, String userEmail) {
		Project project = getOwnedProject(projectId, userEmail);
		return ProjectResponse.from(project, getScanHistory(projectId, userEmail));
	}

	@Transactional
	public ProjectResponse update(UUID projectId, ProjectRequest request, String userEmail) {
		Project project = getOwnedProject(projectId, userEmail);
		applyRequest(project, request);

		Project savedProject = projectRepository.save(project);
		return ProjectResponse.from(savedProject, getScanHistory(projectId, userEmail));
	}

	@Transactional
	public void delete(UUID projectId, String userEmail) {
		Project project = getOwnedProject(projectId, userEmail);
		projectRepository.delete(project);
	}

	private Project getOwnedProject(UUID projectId, String userEmail) {
		return projectRepository.findByIdAndOwnerEmail(projectId, userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private List<ProjectScanSummary> getScanHistory(UUID projectId, String userEmail) {
		return scanRepository.findByProjectIdAndRequestedByEmailOrderByCreatedAtDesc(projectId, userEmail)
			.stream()
			.map(ProjectScanSummary::from)
			.toList();
	}

	private void applyRequest(Project project, ProjectRequest request) {
		project.setName(request.name().trim());
		project.setDescription(cleanOptionalText(request.description()));
		project.setSourceType(request.sourceType());
		project.setTechnology(cleanOptionalText(request.technology()));
		project.setGithubUrl(cleanOptionalText(request.githubUrl()));
	}

	private String cleanOptionalText(String value) {
		if (value == null || value.isBlank()) {
			return null;
		}
		return value.trim();
	}
}
