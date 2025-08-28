package com.securescope.persistence.entity;

import com.securescope.common.enums.FindingCategory;
import com.securescope.common.enums.FindingStatus;
import com.securescope.common.enums.Severity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "findings")
public class Finding extends AuditableEntity {

	@Column(nullable = false, length = 180)
	private String title;

	@Column(nullable = false, length = 1000)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private Severity severity;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 60)
	private FindingCategory category;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private FindingStatus status = FindingStatus.OPEN;

	@Column(length = 120)
	private String owaspCategory;

	@Column(length = 500)
	private String filePath;

	private Integer lineNumber;

	@Column(length = 2000)
	private String evidence;

	@Column(length = 1000)
	private String recommendation;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scan_id", nullable = false)
	private Scan scan;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public FindingCategory getCategory() {
		return category;
	}

	public void setCategory(FindingCategory category) {
		this.category = category;
	}

	public FindingStatus getStatus() {
		return status;
	}

	public void setStatus(FindingStatus status) {
		this.status = status;
	}

	public String getOwaspCategory() {
		return owaspCategory;
	}

	public void setOwaspCategory(String owaspCategory) {
		this.owaspCategory = owaspCategory;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Integer getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(Integer lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getEvidence() {
		return evidence;
	}

	public void setEvidence(String evidence) {
		this.evidence = evidence;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public Scan getScan() {
		return scan;
	}

	public void setScan(Scan scan) {
		this.scan = scan;
	}
}
