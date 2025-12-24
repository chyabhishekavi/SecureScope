package com.securescope.scanner.github;

import com.securescope.common.exception.BadRequestException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.stereotype.Service;

@Service
public class GitHubRepositoryDownloadService {

	private static final long MAX_DOWNLOAD_BYTES = 25L * 1024L * 1024L;

	private final HttpClient httpClient = HttpClient.newBuilder()
		.connectTimeout(Duration.ofSeconds(10))
		.followRedirects(HttpClient.Redirect.NORMAL)
		.build();

	public InputStream downloadZip(GitHubRepositoryReference reference) {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create(reference.zipballApiUrl()))
			.timeout(Duration.ofSeconds(30))
			.header("Accept", "application/vnd.github+json")
			.header("User-Agent", "SecureScope")
			.GET()
			.build();

		try {
			HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
			if (response.statusCode() == 404) {
				throw new BadRequestException("GitHub repository was not found or is not public");
			}
			if (response.statusCode() >= 400) {
				throw new BadRequestException("Unable to download GitHub repository");
			}
			return new LimitedInputStream(response.body(), MAX_DOWNLOAD_BYTES);
		} catch (IOException exception) {
			throw new BadRequestException("Unable to download GitHub repository ZIP");
		} catch (InterruptedException exception) {
			Thread.currentThread().interrupt();
			throw new BadRequestException("GitHub repository download was interrupted");
		}
	}
}
