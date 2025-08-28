package com.securescope.persistence.entity;

import com.securescope.common.enums.RiskLevel;
import com.securescope.common.enums.ScanSourceType;
import com.securescope.common.enums.ScanStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "scans")
public class Scan extends AuditableEntity {

	@Column(nullable = false, length = 160)
	private String scanName;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private ScanSourceType sourceType;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private ScanStatus status = ScanStatus.PENDING;

	@Column(nullable = false)
	private int securityScore;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private RiskLevel riskLevel = RiskLevel.SAFE;

	private Instant startedAt;

	private Instant completedAt;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "project_id")
	private Project project;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requested_by_id")
	private User requestedBy;

	@OneToMany(mappedBy = "scan", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Finding> findings = new ArrayList<>();

	public String getScanName() {
		return scanName;
	}

	public void setScanName(String scanName) {
		this.scanName = scanName;
	}

	public ScanSourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(ScanSourceType sourceType) {
		this.sourceType = sourceType;
	}

	public ScanStatus getStatus() {
		return status;
	}

	public void setStatus(ScanStatus status) {
		this.status = status;
	}

	public int getSecurityScore() {
		return securityScore;
	}

	public void setSecurityScore(int securityScore) {
		this.securityScore = securityScore;
	}

	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	public Instant getStartedAt() {
		return startedAt;
	}

	public void setStartedAt(Instant startedAt) {
		this.startedAt = startedAt;
	}

	public Instant getCompletedAt() {
		return completedAt;
	}

	public void setCompletedAt(Instant completedAt) {
		this.completedAt = completedAt;
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	public User getRequestedBy() {
		return requestedBy;
	}

	public void setRequestedBy(User requestedBy) {
		this.requestedBy = requestedBy;
	}

	public List<Finding> getFindings() {
		return findings;
	}
}
