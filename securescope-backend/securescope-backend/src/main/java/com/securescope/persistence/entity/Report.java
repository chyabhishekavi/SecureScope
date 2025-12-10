package com.securescope.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "reports")
public class Report extends AuditableEntity {

	@Column(nullable = false, length = 180)
	private String title;

	@Column(nullable = false, length = 20)
	private String format;

	@Column(nullable = false)
	private Instant generatedAt;

	@Column(nullable = false)
	private int findingCount;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String htmlContent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scan_id", nullable = false)
	private Scan scan;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public Instant getGeneratedAt() {
		return generatedAt;
	}

	public void setGeneratedAt(Instant generatedAt) {
		this.generatedAt = generatedAt;
	}

	public int getFindingCount() {
		return findingCount;
	}

	public void setFindingCount(int findingCount) {
		this.findingCount = findingCount;
	}

	public String getHtmlContent() {
		return htmlContent;
	}

	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}

	public Scan getScan() {
		return scan;
	}

	public void setScan(Scan scan) {
		this.scan = scan;
	}
}
