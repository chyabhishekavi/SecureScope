package com.securescope.scanner.dependency;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class PackageJsonDependencyParser {

	private static final Pattern DEPENDENCY_BLOCK_PATTERN = Pattern.compile(
		"\"(dependencies|devDependencies)\"\\s*:\\s*\\{(?<body>[^}]*)}",
		Pattern.DOTALL
	);
	private static final Pattern DEPENDENCY_ENTRY_PATTERN = Pattern.compile(
		"\"(?<name>[^\"]+)\"\\s*:\\s*\"(?<version>[^\"]+)\""
	);

	public List<DependencyDescriptor> parse(String content) {
		List<DependencyDescriptor> dependencies = new ArrayList<>();
		Matcher blockMatcher = DEPENDENCY_BLOCK_PATTERN.matcher(content);

		while (blockMatcher.find()) {
			Matcher entryMatcher = DEPENDENCY_ENTRY_PATTERN.matcher(blockMatcher.group("body"));
			while (entryMatcher.find()) {
				dependencies.add(new DependencyDescriptor(
					entryMatcher.group("name"),
					entryMatcher.group("version"),
					"npm"
				));
			}
		}

		return dependencies;
	}
}
