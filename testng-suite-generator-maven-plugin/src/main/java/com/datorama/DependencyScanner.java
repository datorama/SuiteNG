package com.datorama;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.maven.plugin.logging.Log;

public class DependencyScanner {

	private Log log;
	private File dir;
	private String buffer = "";

	public DependencyScanner(File basedir, Log log) {
		this.log = log;
		this.dir = basedir;
	}

	public void scan() {

		final String mavenDependencyPluginCommand = "mvn dependency:build-classpath";
		buffer = Utils.execCommand(mavenDependencyPluginCommand, new String[] {}, dir, log);
		log.debug("Dependency build-classpath command: " + buffer);
	}

	public List<String> getBuildClasspathElements() {

		final String CLASSPATH_ELEMENTS_SEPERATOR = ":";

		String buildClasspathLine = parseBuildClasspathLine(buffer);
		List<String> buildClasspathElements = Arrays.stream(buildClasspathLine.split(CLASSPATH_ELEMENTS_SEPERATOR)).collect(Collectors.toList());

		buildClasspathElements.forEach(element -> log.debug("Dependencies classpath: " + element));

		return buildClasspathElements;
	}

	private String parseBuildClasspathLine(String text) {

		final String DEPENDENCIES_CLASSPATH_PREFIX = "Dependencies classpath:";

		String[] textLines = text.split(System.lineSeparator());

		String buildClasspathLine = IntStream.range(1, textLines.length)
				.filter(i -> textLines[i - 1].contains(DEPENDENCIES_CLASSPATH_PREFIX))
				.mapToObj(i -> textLines[i]).findFirst().orElse("");

		return buildClasspathLine;
	}
}
