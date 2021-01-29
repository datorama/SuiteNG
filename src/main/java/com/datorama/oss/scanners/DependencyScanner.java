/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama.oss.scanners;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.maven.plugin.logging.Log;

import com.google.common.base.Splitter;

public class DependencyScanner {

	private Log log;
	private File dir;
	private String buffer = "";
	private static DependencyScanner instance;

	private DependencyScanner() {
	}

	public void init(File basedir, Log log) {
		this.dir = basedir;
		this.log = log;
	}

	public static DependencyScanner getInstance() {
		if (instance == null) {
			synchronized (FilesScanner.class) {
				if (instance == null) {
					instance = new DependencyScanner();
				}
			}
		}
		return instance;
	}

	public void scan() {
		execMavenDependencyPlugin();
	}

	public List<String> getBuildClasspathElements() {

		final String ELEMENTS_SEPARATOR = ":";

		String buildClasspathLine = parseBuildClasspathLine(buffer);
		List<String> buildClasspathElements = Splitter.on(ELEMENTS_SEPARATOR).omitEmptyStrings().trimResults().splitToList(buildClasspathLine);
		buildClasspathElements.forEach(element -> log.debug("Dependencies classpath element: " + element));

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

	private void execMavenDependencyPlugin() {

		Runtime runtime = Runtime.getRuntime();

		try {
			Process process = runtime.exec("mvn dependency:build-classpath", new String[0], dir);
			buffer = new BufferedReader(new InputStreamReader(process.getInputStream())).lines()
					.parallel().collect(Collectors.joining(System.lineSeparator()));
		} catch (Exception e) {
			log.error(e);
		}
	}
}
