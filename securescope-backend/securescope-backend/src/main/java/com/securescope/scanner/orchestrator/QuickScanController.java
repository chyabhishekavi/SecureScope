package com.securescope.scanner.orchestrator;

import com.securescope.common.response.ApiResponse;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.dto.QuickScanRequest;
import com.securescope.scanner.dto.ScanResult;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/scans")
public class QuickScanController {

	private final QuickCodeScannerService quickCodeScannerService;

	public QuickScanController(QuickCodeScannerService quickCodeScannerService) {
		this.quickCodeScannerService = quickCodeScannerService;
	}

	@PostMapping("/quick-code")
	public ResponseEntity<ApiResponse<ScanResult>> quickCodeScan(
		@Valid @RequestBody QuickScanRequest request,
		Principal principal
	) {
		ScanResult scanResult = quickCodeScannerService.scan(request, principal.getName());
		ApiResponse<ScanResult> response = ApiResponse.success("Quick code scan completed", scanResult);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@GetMapping("/{scanId}")
	public ResponseEntity<ApiResponse<ScanResult>> getScan(
		@PathVariable UUID scanId,
		Principal principal
	) {
		ScanResult scanResult = quickCodeScannerService.getScan(scanId, principal.getName());
		ApiResponse<ScanResult> response = ApiResponse.success("Scan loaded", scanResult);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/{scanId}/findings")
	public ResponseEntity<ApiResponse<List<FindingResult>>> getScanFindings(
		@PathVariable UUID scanId,
		Principal principal
	) {
		List<FindingResult> findings = quickCodeScannerService.getFindings(scanId, principal.getName());
		ApiResponse<List<FindingResult>> response = ApiResponse.success("Scan findings loaded", findings);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/my-scans")
	public ResponseEntity<ApiResponse<List<ScanResult>>> getMyScans(Principal principal) {
		List<ScanResult> scans = quickCodeScannerService.getMyScans(principal.getName());
		ApiResponse<List<ScanResult>> response = ApiResponse.success("Scans loaded", scans);

		return ResponseEntity.ok(response);
	}
}
