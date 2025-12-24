package com.securescope.scanner.github;

import com.securescope.common.enums.ScanSourceType;
import com.securescope.common.exception.BadRequestException;
import com.securescope.common.exception.ResourceNotFoundException;
import com.securescope.persistence.entity.Project;
import com.securescope.persistence.entity.User;
import com.securescope.persistence.repository.ProjectRepository;
import com.securescope.persistence.repository.UserRepository;
import com.securescope.scanner.archive.ProjectArchiveScanService;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.github.dto.GitHubConnectRequest;
import com.securescope.scanner.github.dto.GitHubRepositoryResponse;
import com.securescope.scanner.github.dto.GitHubScanRequest;
import com.securescope.scanner.zip.SafeZipExtractorService;
import com.securescope.scanner.zip.ZipExtractionResult;
import java.io.InputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectGitHubScanService {

	private final ProjectRepository projectRepository;
	private final UserRepository userRepository;
	private final GitHubRepositoryUrlValidator urlValidator;
	private final GitHubRepositoryDownloadService downloadService;
	private final SafeZipExtractorService safeZipExtractorService;
	private final ProjectArchiveScanService projectArchiveScanService;

	public ProjectGitHubScanService(
		ProjectRepository projectRepository,
		UserRepository userRepository,
		GitHubRepositoryUrlValidator urlValidator,
		GitHubRepositoryDownloadService downloadService,
		SafeZipExtractorService safeZipExtractorService,
		ProjectArchiveScanService projectArchiveScanService
	) {
		this.projectRepository = projectRepository;
		this.userRepository = userRepository;
		this.urlValidator = urlValidator;
		this.downloadService = downloadService;
		this.safeZipExtractorService = safeZipExtractorService;
		this.projectArchiveScanService = projectArchiveScanService;
	}

	@Transactional
	public GitHubRepositoryResponse connectRepository(UUID projectId, GitHubConnectRequest request, String userEmail) {
		Project project = getOwnedProject(projectId, userEmail);
		GitHubRepositoryReference reference = urlValidator.validate(request.repositoryUrl());

		project.setGithubUrl(reference.normalizedUrl());
		Project savedProject = projectRepository.save(project);

		return new GitHubRepositoryResponse(savedProject.getId(), savedProject.getGithubUrl(), reference.displayName());
	}

	@Transactional
	public ScanResult scanRepository(UUID projectId, GitHubScanRequest request, String userEmail) {
		Project project = getOwnedProject(projectId, userEmail);
		User user = userRepository.findByEmail(userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("User not found"));
		GitHubRepositoryReference reference = resolveRepository(project, request);
		Path extractionDirectory = createExtractionDirectory(projectId);

		try (InputStream zipStream = downloadService.downloadZip(reference)) {
			ZipExtractionResult extractionResult = safeZipExtractorService.extractSupportedFiles(zipStream, extractionDirectory);
			if (extractionResult.files().isEmpty()) {
				throw new BadRequestException("GitHub repository does not contain supported files to scan");
			}

			project.setGithubUrl(reference.normalizedUrl());
			projectRepository.save(project);

			return projectArchiveScanService.scanAndSave(
				project,
				user,
				"GitHub scan: " + reference.displayName(),
				ScanSourceType.GITHUB_REPOSITORY,
				extractionResult.files()
			);
		} catch (IOException exception) {
			throw new BadRequestException("Unable to close GitHub repository ZIP stream");
		} finally {
			deleteQuietly(extractionDirectory);
		}
	}

	private GitHubRepositoryReference resolveRepository(Project project, GitHubScanRequest request) {
		if (request != null && request.repositoryUrl() != null && !request.repositoryUrl().isBlank()) {
			return urlValidator.validate(request.repositoryUrl());
		}
		return urlValidator.validate(project.getGithubUrl());
	}

	private Project getOwnedProject(UUID projectId, String userEmail) {
		return projectRepository.findByIdAndOwnerEmail(projectId, userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Project not found"));
	}

	private Path createExtractionDirectory(UUID projectId) {
		try {
			return Files.createTempDirectory("securescope-github-" + projectId + "-");
		} catch (IOException exception) {
			throw new BadRequestException("Unable to prepare GitHub scan workspace");
		}
	}

	private void deleteQuietly(Path directory) {
		try (var paths = Files.walk(directory)) {
			paths.sorted(Comparator.reverseOrder())
				.forEach(path -> {
					try {
						Files.deleteIfExists(path);
					} catch (IOException ignored) {
						// Temporary GitHub scan cleanup should not hide the completed scan response.
					}
				});
		} catch (IOException ignored) {
			// Temporary GitHub scan cleanup should not hide the completed scan response.
		}
	}
}
