package com.securescope.finding;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.FindingStatus;
import com.securescope.common.enums.Severity;
import com.securescope.common.response.ApiResponse;
import com.securescope.finding.dto.FindingResponse;
import com.securescope.finding.dto.FindingStatusUpdateRequest;
import jakarta.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/findings")
public class FindingController {

	private final FindingService findingService;

	public FindingController(FindingService findingService) {
		this.findingService = findingService;
	}

	@GetMapping
	public ResponseEntity<ApiResponse<List<FindingResponse>>> getFindings(
		@RequestParam(required = false) Severity severity,
		@RequestParam(required = false) FindingCategory category,
		@RequestParam(required = false) String owaspCategory,
		@RequestParam(required = false) FindingStatus status,
		Principal principal
	) {
		List<FindingResponse> findings = findingService.getFindings(
			principal.getName(),
			severity,
			category,
			owaspCategory,
			status
		);

		return ResponseEntity.ok(ApiResponse.success("Findings loaded", findings));
	}

	@GetMapping("/{findingId}")
	public ResponseEntity<ApiResponse<FindingResponse>> getFinding(
		@PathVariable UUID findingId,
		Principal principal
	) {
		FindingResponse finding = findingService.getFinding(findingId, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Finding loaded", finding));
	}

	@PatchMapping("/{findingId}/status")
	public ResponseEntity<ApiResponse<FindingResponse>> updateFindingStatus(
		@PathVariable UUID findingId,
		@Valid @RequestBody FindingStatusUpdateRequest request,
		Principal principal
	) {
		FindingResponse finding = findingService.updateStatus(findingId, request, principal.getName());

		return ResponseEntity.ok(ApiResponse.success("Finding status updated", finding));
	}
}
