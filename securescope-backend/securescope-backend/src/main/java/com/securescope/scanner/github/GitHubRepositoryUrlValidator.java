package com.securescope.scanner.github;

import com.securescope.common.exception.BadRequestException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import org.springframework.stereotype.Component;

@Component
public class GitHubRepositoryUrlValidator {

	public GitHubRepositoryReference validate(String repositoryUrl) {
		if (repositoryUrl == null || repositoryUrl.isBlank()) {
			throw new BadRequestException("GitHub repository URL is required");
		}

		URI uri = parse(repositoryUrl.trim());
		String host = uri.getHost() == null ? "" : uri.getHost().toLowerCase(Locale.ROOT);

		if (!host.equals("github.com") && !host.equals("www.github.com")) {
			throw new BadRequestException("Only github.com repository URLs are supported");
		}

		String[] pathSegments = cleanPath(uri.getPath()).split("/");
		if (pathSegments.length < 2 || pathSegments[0].isBlank() || pathSegments[1].isBlank()) {
			throw new BadRequestException("GitHub repository URL must include owner and repository name");
		}

		String owner = pathSegments[0];
		String repository = removeGitSuffix(pathSegments[1]);
		if (repository.isBlank()) {
			throw new BadRequestException("GitHub repository URL must include a repository name");
		}

		String normalizedUrl = "https://github.com/" + owner + "/" + repository;
		String apiUrl = "https://api.github.com/repos/" + owner + "/" + repository + "/zipball";
		return new GitHubRepositoryReference(owner, repository, normalizedUrl, apiUrl);
	}

	private URI parse(String repositoryUrl) {
		try {
			URI uri = new URI(repositoryUrl);
			if (uri.getScheme() == null) {
				uri = new URI("https://" + repositoryUrl);
			}
			if (!"https".equalsIgnoreCase(uri.getScheme())) {
				throw new BadRequestException("GitHub repository URL must use HTTPS");
			}
			return uri;
		} catch (URISyntaxException exception) {
			throw new BadRequestException("GitHub repository URL is not valid");
		}
	}

	private String cleanPath(String path) {
		if (path == null) {
			return "";
		}
		String cleaned = path.strip();
		while (cleaned.startsWith("/")) {
			cleaned = cleaned.substring(1);
		}
		while (cleaned.endsWith("/")) {
			cleaned = cleaned.substring(0, cleaned.length() - 1);
		}
		return cleaned;
	}

	private String removeGitSuffix(String repository) {
		if (repository.toLowerCase(Locale.ROOT).endsWith(".git")) {
			return repository.substring(0, repository.length() - 4);
		}
		return repository;
	}
}
