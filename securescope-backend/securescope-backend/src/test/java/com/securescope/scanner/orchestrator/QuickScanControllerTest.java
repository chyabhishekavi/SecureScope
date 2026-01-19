package com.securescope.scanner.orchestrator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.ScanStatus;
import com.securescope.common.response.ApiResponse;
import com.securescope.scanner.dto.QuickScanRequest;
import com.securescope.scanner.dto.ScanResult;
import java.security.Principal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class QuickScanControllerTest {

	private final QuickCodeScannerService quickCodeScannerService = mock(QuickCodeScannerService.class);
	private final QuickScanController quickScanController = new QuickScanController(quickCodeScannerService);

	@Test
	void quickCodeScanReturnsCreatedResponse() {
		QuickScanRequest request = new QuickScanRequest("Snippet", "JavaScript", "app.js", "const token = \"secret\";", null);
		Principal principal = () -> "developer@example.com";
		ScanResult scanResult = new ScanResult("scan-id", ScanStatus.COMPLETED, 80, RiskLevel.LOW, 0, List.of());
		when(quickCodeScannerService.scan(request, principal.getName())).thenReturn(scanResult);

		ResponseEntity<ApiResponse<ScanResult>> response = quickScanController.quickCodeScan(request, principal);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isNotNull();
		assertThat(response.getBody().data()).isEqualTo(scanResult);
		assertThat(response.getBody().message()).isEqualTo("Quick code scan completed");
		verify(quickCodeScannerService).scan(request, "developer@example.com");
	}
}
