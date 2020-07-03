/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.annotations.Test;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.google.common.collect.ImmutableMap;

public abstract class AbstractTestngSuiteGeneratorMojo extends AbstractSuiteGeneratorMojo {

	protected XmlSuite topLevelSuite;
	protected List<XmlTest> topLevelTestsList;

	public abstract void generate();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		setPluginClasspath(getProjectAdditionalClasspathElements());
		setSuiteTopLevelPreConfiguration();
		generate();
		setSuiteTopLevelPostConfiguration();
	}

	protected void setSuiteTopLevelPreConfiguration() {

		//Create an instance of XML Suite and assign a name
		topLevelSuite = new XmlSuite();
		topLevelSuite.setName(getSuiteName());

		topLevelTestsList = new ArrayList<>();
		setSuiteGlobalConfiguration();
	}

	protected void setSuiteGlobalConfiguration() {

		XmlSuite.ParallelMode enumParallelMode = XmlSuite.ParallelMode.getValidParallel(getParallelMode());
		topLevelSuite.setParallel(enumParallelMode);
		topLevelSuite.setThreadCount(getThreadCount());
		topLevelSuite.setListeners(getListeners());
		topLevelSuite.setExcludedGroups(getExcludedGroups());
		topLevelSuite.setIncludedGroups(getIncludedGroups());
		topLevelSuite.setTimeOut(getTimeout());
		topLevelSuite.setPreserveOrder(isPreserveOrder());
		topLevelSuite.setVerbose(getVerbose());
	}

	protected void setSuiteTopLevelPostConfiguration() {

		//Assign tests list to suite
		topLevelSuite.setTests(topLevelTestsList);

		//Add the suite to the list of suites.
		List<XmlSuite> suitesList = new ArrayList<>();
		suitesList.add(topLevelSuite);

		//Create XML file based on the virtual XML content
		suitesList.forEach(xmlSuite -> {
			writeFile(getSuiteRelativePath(), xmlSuite.toXml(), getLog());
		});
	}

	protected List<AnnotationsFilter> buildFiltersByIncludedGroups() {

		final String ATTRIBUTE_GROUPS = "groups";

		List<AnnotationsFilter> filters = new ArrayList<>();

		getIncludedGroups().forEach(includedGroup -> {
			Map<String, String> attributes = ImmutableMap.of(ATTRIBUTE_GROUPS, includedGroup);
			Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap = ImmutableMap.of(Test.class, attributes);
			AnnotationsFilter filter = new AnnotationsFilter(annotationsFilterMap);
			filters.add(filter);
		});

		return filters;
	}

	protected List<AnnotationsFilter> buildFiltersByTestAnnotation() {

		List<AnnotationsFilter> filters = new ArrayList<>();

		Map<String, String> attributes = ImmutableMap.of();
		Map<Class<? extends Annotation>, Map<String, String>> annotationsFilterMap = ImmutableMap.of(Test.class, attributes);
		AnnotationsFilter filter = new AnnotationsFilter(annotationsFilterMap);
		filters.add(filter);

		return filters;
	}
}
