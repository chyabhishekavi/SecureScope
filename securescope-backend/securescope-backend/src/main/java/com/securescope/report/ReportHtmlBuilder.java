package com.securescope.report;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.Severity;
import com.securescope.persistence.entity.Finding;
import com.securescope.persistence.entity.Project;
import com.securescope.persistence.entity.Scan;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.stereotype.Component;

@Component
public class ReportHtmlBuilder {

	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter
		.ofPattern("yyyy-MM-dd HH:mm")
		.withZone(ZoneId.systemDefault());

	public String build(Scan scan) {
		List<Finding> findings = scan.getFindings();
		Map<Severity, Long> severitySummary = severitySummary(findings);
		Map<String, Long> owaspSummary = owaspSummary(findings);
		List<Finding> dependencyFindings = findings.stream()
			.filter(finding -> finding.getCategory() == FindingCategory.VULNERABLE_DEPENDENCY)
			.toList();
		List<Finding> secretFindings = findings.stream()
			.filter(finding -> finding.getCategory() == FindingCategory.HARDCODED_SECRET)
			.toList();

		StringBuilder html = new StringBuilder();
		html.append("""
			<!doctype html>
			<html lang="en">
			<head>
			  <meta charset="utf-8">
			  <title>SecureScope Security Report</title>
			  <style>
			    body { font-family: Arial, sans-serif; margin: 32px; color: #0f172a; line-height: 1.55; }
			    h1, h2 { color: #0f172a; }
			    .muted { color: #64748b; }
			    .summary { display: grid; grid-template-columns: repeat(4, 1fr); gap: 12px; margin: 24px 0; }
			    .card { border: 1px solid #e2e8f0; border-radius: 8px; padding: 14px; }
			    .card span { display: block; color: #64748b; font-size: 12px; font-weight: 700; text-transform: uppercase; }
			    .card strong { display: block; margin-top: 8px; font-size: 22px; }
			    table { width: 100%; border-collapse: collapse; margin: 14px 0 24px; }
			    th, td { border: 1px solid #e2e8f0; padding: 9px; text-align: left; vertical-align: top; }
			    th { background: #f8fafc; }
			    code { background: #f8fafc; border-radius: 6px; padding: 3px 5px; word-break: break-word; }
			    .disclaimer { border-left: 4px solid #0f766e; background: #f0fdfa; padding: 12px 14px; margin-top: 24px; }
			  </style>
			</head>
			<body>
			""");

		html.append("<h1>SecureScope Security Report</h1>");
		html.append("<p class=\"muted\">Generated static security report with masked evidence.</p>");
		html.append("<div class=\"summary\">");
		summaryCard(html, "Project", projectName(scan));
		summaryCard(html, "Scan Date", formatScanDate(scan));
		summaryCard(html, "Scan Source", scan.getSourceType().name());
		summaryCard(html, "Risk Level", scan.getRiskLevel().name());
		summaryCard(html, "Security Score", String.valueOf(scan.getSecurityScore()));
		summaryCard(html, "Findings", String.valueOf(findings.size()));
		summaryCard(html, "Dependency Findings", String.valueOf(dependencyFindings.size()));
		summaryCard(html, "Secret Findings", String.valueOf(secretFindings.size()));
		html.append("</div>");

		appendSummaryTable(html, "Severity Summary", severitySummary);
		appendSummaryTable(html, "OWASP Summary", owaspSummary);
		appendFindingsTable(html, "Findings Table", findings);
		appendFindingsTable(html, "Dependency Findings", dependencyFindings);
		appendFindingsTable(html, "Secret Findings", secretFindings);
		appendRecommendations(html, findings);

		html.append("""
			<div class="disclaimer">
			  SecureScope performs static checks and does not replace manual security review, dependency auditing, threat modeling, or penetration testing.
			</div>
			</body>
			</html>
			""");

		return html.toString();
	}

	private void summaryCard(StringBuilder html, String label, String value) {
		html.append("<div class=\"card\"><span>")
			.append(escape(label))
			.append("</span><strong>")
			.append(escape(value))
			.append("</strong></div>");
	}

	private void appendSummaryTable(StringBuilder html, String title, Map<?, Long> summary) {
		html.append("<h2>").append(escape(title)).append("</h2>");
		html.append("<table><thead><tr><th>Category</th><th>Count</th></tr></thead><tbody>");
		if (summary.isEmpty()) {
			html.append("<tr><td colspan=\"2\">No data</td></tr>");
		} else {
			summary.forEach((key, value) -> html.append("<tr><td>")
				.append(escape(String.valueOf(key)))
				.append("</td><td>")
				.append(value)
				.append("</td></tr>"));
		}
		html.append("</tbody></table>");
	}

	private void appendFindingsTable(StringBuilder html, String title, List<Finding> findings) {
		html.append("<h2>").append(escape(title)).append("</h2>");
		html.append("""
			<table>
			  <thead>
			    <tr>
			      <th>Severity</th><th>Status</th><th>Title</th><th>OWASP</th><th>File</th><th>Evidence</th><th>Recommendation</th>
			    </tr>
			  </thead>
			  <tbody>
			""");
		if (findings.isEmpty()) {
			html.append("<tr><td colspan=\"7\">No findings in this section.</td></tr>");
		} else {
			for (Finding finding : findings) {
				html.append("<tr><td>")
					.append(escape(finding.getSeverity().name()))
					.append("</td><td>")
					.append(escape(finding.getStatus().name()))
					.append("</td><td>")
					.append(escape(finding.getTitle()))
					.append("</td><td>")
					.append(escape(finding.getOwaspCategory()))
					.append("</td><td>")
					.append(escape(location(finding)))
					.append("</td><td><code>")
					.append(escape(finding.getEvidence()))
					.append("</code></td><td>")
					.append(escape(finding.getRecommendation()))
					.append("</td></tr>");
			}
		}
		html.append("</tbody></table>");
	}

	private void appendRecommendations(StringBuilder html, List<Finding> findings) {
		html.append("<h2>Recommendations</h2><ul>");
		if (findings.isEmpty()) {
			html.append("<li>No scanner recommendations were generated for this scan.</li>");
		} else {
			findings.stream()
				.map(Finding::getRecommendation)
				.filter(recommendation -> recommendation != null && !recommendation.isBlank())
				.distinct()
				.forEach(recommendation -> html.append("<li>").append(escape(recommendation)).append("</li>"));
		}
		html.append("</ul>");
	}

	private Map<Severity, Long> severitySummary(List<Finding> findings) {
		Map<Severity, Long> summary = new EnumMap<>(Severity.class);
		for (Severity severity : Severity.values()) {
			summary.put(severity, 0L);
		}
		for (Finding finding : findings) {
			summary.computeIfPresent(finding.getSeverity(), (key, count) -> count + 1);
		}
		return summary;
	}

	private Map<String, Long> owaspSummary(List<Finding> findings) {
		Map<String, Long> summary = new TreeMap<>();
		for (Finding finding : findings) {
			String owaspCategory = finding.getOwaspCategory() == null || finding.getOwaspCategory().isBlank()
				? "Unmapped"
				: finding.getOwaspCategory();
			summary.merge(owaspCategory, 1L, Long::sum);
		}
		return summary;
	}

	private String projectName(Scan scan) {
		Project project = scan.getProject();
		return project == null ? "Standalone Quick Scan" : project.getName();
	}

	private String formatScanDate(Scan scan) {
		if (scan.getCompletedAt() != null) {
			return DATE_FORMATTER.format(scan.getCompletedAt());
		}
		if (scan.getStartedAt() != null) {
			return DATE_FORMATTER.format(scan.getStartedAt());
		}
		return "Not available";
	}

	private String location(Finding finding) {
		String filePath = finding.getFilePath() == null ? "No file path" : finding.getFilePath();
		return finding.getLineNumber() == null ? filePath : filePath + ":" + finding.getLineNumber();
	}

	private String escape(String value) {
		if (value == null) {
			return "";
		}
		return value
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&#39;");
	}
}
