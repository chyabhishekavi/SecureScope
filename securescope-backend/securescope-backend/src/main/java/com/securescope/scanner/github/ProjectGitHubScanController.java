package com.securescope.scanner.github;

import com.securescope.common.response.ApiResponse;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.github.dto.GitHubConnectRequest;
import com.securescope.scanner.github.dto.GitHubRepositoryResponse;
import com.securescope.scanner.github.dto.GitHubScanRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/projects/{projectId}/github")
public class ProjectGitHubScanController {

	private final ProjectGitHubScanService projectGitHubScanService;

	public ProjectGitHubScanController(ProjectGitHubScanService projectGitHubScanService) {
		this.projectGitHubScanService = projectGitHubScanService;
	}

	@PostMapping("/connect")
	public ResponseEntity<ApiResponse<GitHubRepositoryResponse>> connectRepository(
		@PathVariable UUID projectId,
		@Valid @RequestBody GitHubConnectRequest request,
		Principal principal
	) {
		GitHubRepositoryResponse response = projectGitHubScanService.connectRepository(projectId, request, principal.getName());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("GitHub repository connected", response));
	}

	@PostMapping("/scan")
	public ResponseEntity<ApiResponse<ScanResult>> scanRepository(
		@PathVariable UUID projectId,
		@RequestBody(required = false) GitHubScanRequest request,
		Principal principal
	) {
		ScanResult scanResult = projectGitHubScanService.scanRepository(projectId, request, principal.getName());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("GitHub repository scan completed", scanResult));
	}
}
