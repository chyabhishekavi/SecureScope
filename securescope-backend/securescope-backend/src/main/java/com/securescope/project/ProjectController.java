package com.securescope.project;

import com.securescope.common.response.ApiResponse;
import com.securescope.project.dto.ProjectRequest;
import com.securescope.project.dto.ProjectResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects")
public class ProjectController {

	private final ProjectService projectService;

	public ProjectController(ProjectService projectService) {
		this.projectService = projectService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ProjectResponse>> createProject(
		@Valid @RequestBody ProjectRequest request,
		Principal principal
	) {
		ProjectResponse project = projectService.create(request, principal.getName());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("Project created", project));
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<ProjectResponse>>> getProjects(Principal principal) {
		List<ProjectResponse> projects = projectService.getMyProjects(principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Projects loaded", projects));
	}

	@GetMapping("/{projectId}")
	public ResponseEntity<ApiResponse<ProjectResponse>> getProject(
		@PathVariable UUID projectId,
		Principal principal
	) {
		ProjectResponse project = projectService.getProject(projectId, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Project loaded", project));
	}

	@PutMapping("/{projectId}")
	public ResponseEntity<ApiResponse<ProjectResponse>> updateProject(
		@PathVariable UUID projectId,
		@Valid @RequestBody ProjectRequest request,
		Principal principal
	) {
		ProjectResponse project = projectService.update(projectId, request, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Project updated", project));
	}

	@DeleteMapping("/{projectId}")
	public ResponseEntity<ApiResponse<Void>> deleteProject(
		@PathVariable UUID projectId,
		Principal principal
	) {
		projectService.delete(projectId, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Project deleted", null));
	}
}
