package com.securescope.scanner.zip;

import com.securescope.common.response.ApiResponse;
import com.securescope.scanner.dto.ScanResult;
import com.securescope.scanner.zip.dto.ZipScanRequest;
import com.securescope.scanner.zip.dto.ZipUploadResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects/{projectId}")
public class ProjectZipScanController {

	private final ProjectZipScanService projectZipScanService;

	public ProjectZipScanController(ProjectZipScanService projectZipScanService) {
		this.projectZipScanService = projectZipScanService;
	}

	@PostMapping(path = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ApiResponse<ZipUploadResponse>> uploadZip(
		@PathVariable UUID projectId,
		@RequestParam("file") MultipartFile file,
		Principal principal
	) {
		ZipUploadResponse uploadResponse = projectZipScanService.uploadZip(projectId, file, principal.getName());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("ZIP uploaded and validated", uploadResponse));
	}

	@PostMapping("/scans")
	public ResponseEntity<ApiResponse<ScanResult>> scanUploadedZip(
		@PathVariable UUID projectId,
		@Valid @RequestBody ZipScanRequest request,
		Principal principal
	) {
		ScanResult scanResult = projectZipScanService.scanUploadedZip(projectId, request, principal.getName());

		return ResponseEntity
			.status(HttpStatus.CREATED)
			.body(ApiResponse.success("ZIP scan completed", scanResult));
	}
}
