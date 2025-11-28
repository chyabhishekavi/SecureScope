package com.securescope.scanner.dependency;

public record DependencyDescriptor(
	String name,
	String version,
	String ecosystem
) {
}
