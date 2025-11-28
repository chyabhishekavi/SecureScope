package com.securescope.scanner.dependency;

import com.securescope.common.exception.BadRequestException;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Component
public class MavenPomDependencyParser {

	public List<DependencyDescriptor> parse(String content) {
		List<DependencyDescriptor> dependencies = new ArrayList<>();

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
			factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

			var document = factory.newDocumentBuilder()
				.parse(new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
			NodeList dependencyNodes = document.getElementsByTagName("dependency");

			for (int index = 0; index < dependencyNodes.getLength(); index++) {
				Element dependency = (Element) dependencyNodes.item(index);
				String groupId = textOf(dependency, "groupId");
				String artifactId = textOf(dependency, "artifactId");
				String version = textOf(dependency, "version");

				if (!artifactId.isBlank() && !version.isBlank() && !version.startsWith("${")) {
					dependencies.add(new DependencyDescriptor(groupId + ":" + artifactId, version, "Maven"));
				}
			}
		} catch (Exception exception) {
			throw new BadRequestException("Unable to parse Maven pom.xml");
		}

		return dependencies;
	}

	private String textOf(Element element, String tagName) {
		NodeList nodeList = element.getElementsByTagName(tagName);
		if (nodeList.getLength() == 0 || nodeList.item(0).getTextContent() == null) {
			return "";
		}

		return nodeList.item(0).getTextContent().trim();
	}
}
