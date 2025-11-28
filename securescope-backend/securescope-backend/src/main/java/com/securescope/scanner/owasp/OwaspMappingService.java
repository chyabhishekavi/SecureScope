package com.securescope.scanner.owasp;

import org.springframework.stereotype.Service;

@Service
public class OwaspMappingService {

	public String secretsExposure() {
		return "A02:2021 - Cryptographic Failures";
	}

	public String weakCryptography() {
		return "A02:2021 - Cryptographic Failures";
	}

	public String injection() {
		return "A03:2021 - Injection";
	}

	public String securityMisconfiguration() {
		return "A05:2021 - Security Misconfiguration";
	}

	public String softwareSupplyChainFailures() {
		return "A06:2021 - Vulnerable and Outdated Components";
	}
}
