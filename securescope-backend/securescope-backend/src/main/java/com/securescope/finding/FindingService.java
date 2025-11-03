package com.securescope.finding;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.FindingStatus;
import com.securescope.common.enums.Severity;
import com.securescope.common.exception.ResourceNotFoundException;
import com.securescope.finding.dto.FindingResponse;
import com.securescope.finding.dto.FindingStatusUpdateRequest;
import com.securescope.persistence.entity.Finding;
import com.securescope.persistence.repository.FindingRepository;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FindingService {

	private final FindingRepository findingRepository;

	public FindingService(FindingRepository findingRepository) {
		this.findingRepository = findingRepository;
	}

	@Transactional(readOnly = true)
	public List<FindingResponse> getFindings(
		String userEmail,
		Severity severity,
		FindingCategory category,
		String owaspCategory,
		FindingStatus status
	) {
		return findingRepository.findByScanRequestedByEmail(userEmail)
			.stream()
			.filter(finding -> severity == null || finding.getSeverity() == severity)
			.filter(finding -> category == null || finding.getCategory() == category)
			.filter(finding -> status == null || finding.getStatus() == status)
			.filter(finding -> matchesOwaspCategory(finding, owaspCategory))
			.sorted(Comparator.comparing(Finding::getCreatedAt).reversed())
			.map(FindingResponse::from)
			.toList();
	}

	@Transactional(readOnly = true)
	public FindingResponse getFinding(UUID findingId, String userEmail) {
		return FindingResponse.from(getOwnedFinding(findingId, userEmail));
	}

	@Transactional
	public FindingResponse updateStatus(
		UUID findingId,
		FindingStatusUpdateRequest request,
		String userEmail
	) {
		Finding finding = getOwnedFinding(findingId, userEmail);
		finding.setStatus(request.status());

		return FindingResponse.from(findingRepository.save(finding));
	}

	private Finding getOwnedFinding(UUID findingId, String userEmail) {
		return findingRepository.findByIdAndScanRequestedByEmail(findingId, userEmail)
			.orElseThrow(() -> new ResourceNotFoundException("Finding not found"));
	}

	private boolean matchesOwaspCategory(Finding finding, String owaspCategory) {
		if (owaspCategory == null || owaspCategory.isBlank()) {
			return true;
		}

		String findingOwaspCategory = finding.getOwaspCategory();
		return findingOwaspCategory != null
			&& findingOwaspCategory.toLowerCase().contains(owaspCategory.trim().toLowerCase());
	}
}
