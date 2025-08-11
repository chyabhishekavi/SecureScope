package com.securescope.scanner.scoring;

import static org.assertj.core.api.Assertions.assertThat;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.Severity;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.dto.ScoreResult;
import java.util.List;
import org.junit.jupiter.api.Test;

class SecurityScoringServiceTest {

	private final SecurityScoringService securityScoringService = new SecurityScoringService();

	@Test
	void calculateScoreSubtractsSeverityPenalties() {
		List<FindingResult> findings = List.of(
			finding(Severity.CRITICAL),
			finding(Severity.HIGH),
			finding(Severity.MEDIUM)
		);

		ScoreResult scoreResult = securityScoringService.calculateScore(findings);

		assertThat(scoreResult.securityScore()).isEqualTo(40);
		assertThat(scoreResult.riskLevel()).isEqualTo(RiskLevel.HIGH);
	}

	private FindingResult finding(Severity severity) {
		return new FindingResult(
			"Test finding",
			"Test description",
			severity,
			FindingCategory.RISKY_CODE_PATTERN,
			"A01:2021 - Test",
			"test.java",
			1,
			"evidence",
			"recommendation"
		);
	}
}
