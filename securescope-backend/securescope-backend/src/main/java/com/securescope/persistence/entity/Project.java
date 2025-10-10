package com.securescope.persistence.entity;

import com.securescope.common.enums.ScanSourceType;
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
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "projects")
public class Project extends AuditableEntity {

	@Column(nullable = false, length = 140)
	private String name;

	@Column(length = 800)
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 40)
	private ScanSourceType sourceType;

	@Column(length = 120)
	private String technology;

	@Column(length = 500)
	private String githubUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private User owner;

	@OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Scan> scans = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ScanSourceType getSourceType() {
		return sourceType;
	}

	public void setSourceType(ScanSourceType sourceType) {
		this.sourceType = sourceType;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getGithubUrl() {
		return githubUrl;
	}

	public void setGithubUrl(String githubUrl) {
		this.githubUrl = githubUrl;
	}

	public User getOwner() {
		return owner;
	}

	public void setOwner(User owner) {
		this.owner = owner;
	}

	public List<Scan> getScans() {
		return scans;
	}
}
