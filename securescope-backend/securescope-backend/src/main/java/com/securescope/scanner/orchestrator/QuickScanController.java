package com.securescope.scanner.orchestrator;

import com.securescope.common.response.ApiResponse;
import com.securescope.scanner.dto.QuickScanRequest;
import com.securescope.scanner.dto.ScanResult;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
		@Valid @RequestBody QuickScanRequest request
	) {
		ScanResult scanResult = quickCodeScannerService.scan(request);
		ApiResponse<ScanResult> response = ApiResponse.success("Quick code scan completed", scanResult);

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}
}
