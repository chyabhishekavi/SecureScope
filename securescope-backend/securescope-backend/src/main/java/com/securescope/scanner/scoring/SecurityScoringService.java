package com.securescope.scanner.scoring;

import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.Severity;
import com.securescope.scanner.dto.FindingResult;
import com.securescope.scanner.dto.ScoreResult;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SecurityScoringService {

	public ScoreResult calculateScore(List<FindingResult> findings) {
		int penalty = findings.stream()
			.map(FindingResult::severity)
			.mapToInt(this::penaltyFor)
			.sum();

		int score = Math.max(0, 100 - penalty);

		return new ScoreResult(score, riskLevelFor(score));
	}

	private int penaltyFor(Severity severity) {
		return switch (severity) {
			case CRITICAL -> 30;
			case HIGH -> 20;
			case MEDIUM -> 10;
			case LOW -> 5;
			case INFO -> 1;
		};
	}

	private RiskLevel riskLevelFor(int score) {
		if (score >= 90) {
			return RiskLevel.SAFE;
		}
		if (score >= 75) {
			return RiskLevel.LOW;
		}
		if (score >= 50) {
			return RiskLevel.MODERATE;
		}
		if (score >= 25) {
			return RiskLevel.HIGH;
		}
		return RiskLevel.CRITICAL;
	}
}
