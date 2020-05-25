package com.datorama;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.util.List;
import java.util.stream.IntStream;

import org.apache.maven.plugin.logging.Log;

import com.google.common.base.Splitter;

public class DependencyScanner {

	final String ELEMENTS_SEPERATOR = ":";

	private Log log;
	private File dir;
	private String buffer = "";

	public DependencyScanner(File basedir, Log log) {
		this.log = log;
		this.dir = basedir;
	}

	public List<String> scan() {

		final String mavenCommand = "mvn ";
		final String mavenPlugin = "dependency";
		final String mavenGoal = "build-classpath";

		buffer = Commander.executeCommand(mavenCommand + mavenPlugin + ELEMENTS_SEPERATOR + mavenGoal, new String[] {}, dir, log);
		log.debug("Dependency build-classpath command: " + buffer);

		return getBuildClasspathElements();
	}

	private List<String> getBuildClasspathElements() {

		String buildClasspathLine = parseBuildClasspathLine(buffer);
		List<String> buildClasspathElements = Splitter.on(ELEMENTS_SEPERATOR).omitEmptyStrings().trimResults().splitToList(buildClasspathLine);
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
