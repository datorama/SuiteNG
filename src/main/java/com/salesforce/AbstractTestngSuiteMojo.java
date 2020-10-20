/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import com.salesforce.filters.Filter;
import com.salesforce.filters.FiltersBuilder;

public abstract class AbstractTestngSuiteMojo extends AbstractSuiteMojo {

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
		topLevelSuite.setPreserveOrder(isPreserveOrder());
		topLevelSuite.setVerbose(getVerbose());
		if(getSuiteLevelTimeoutInMilliseconds() > 0) {
			topLevelSuite.setTimeOut(Long.toString(getSuiteLevelTimeoutInMilliseconds()));
		}
	}

	protected void setSuiteTopLevelPostConfiguration() {

		//Assign tests list to suite
		topLevelSuite.setTests(topLevelTestsList);

		//Add the suite to the list of suites.
		List<XmlSuite> suitesList = new ArrayList<>();
		suitesList.add(topLevelSuite);

		//Create XML file based on the virtual XML content
		suitesList.forEach(xmlSuite -> {
			writeFile(getSuiteRelativePath(), xmlSuite.toXml());
		});
	}

	protected Set<Class<?>> getTestsClasses() {

		return getFilesScanner().getFilteredResults(getIncludedFilters(), getExcludedFilters()).keySet();
	}

	protected Map<Class<?>, List<Method>> getTestMethodsPerClass() {

		return getFilesScanner().getFilteredResults(getIncludedFilters(), getExcludedFilters());
	}

	protected List<Filter> getIncludedFilters() {

		return Stream.concat(FiltersBuilder.buildAnnotationFilters(getIncludedAnnotationFilters()).stream(), FiltersBuilder.buildTestngGroupsFilters(getGroups()).stream())
				.collect(Collectors.toList());
	}

	protected List<Filter> getExcludedFilters() {

		return Stream.concat(FiltersBuilder.buildAnnotationFilters(getExcludedAnnotationFilters()).stream(), FiltersBuilder.buildTestngGroupsFilters(getExcludedGroups()).stream())
				.collect(Collectors.toList());
	}

}
