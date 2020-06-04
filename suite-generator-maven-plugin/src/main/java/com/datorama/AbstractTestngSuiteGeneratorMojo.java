/*
 * Copyright (c) 2018, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

public abstract class AbstractTestngSuiteGeneratorMojo extends AbstractSuiteGeneratorMojo {

	protected URLClassLoader urlClassLoader;
	protected XmlSuite topLevelSuite;
	protected XmlTest topLevelTest;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		generate();
	}

	protected void setSuiteTopLevelPreConfiguration() {
		//Create an instance of XML Suite and assign a name
		topLevelSuite = new XmlSuite();
		topLevelSuite.setName(suiteName);

		//Create an instance of XmlTest and assign a name
		topLevelTest = new XmlTest(topLevelSuite);
		topLevelTest.setName(testName);

		setSuiteGlobalConfiguration();

		urlClassLoader = setPluginClasspath(getProjectAdditionalClasspathElements());
	}

	protected void setSuiteGlobalConfiguration() {

		XmlSuite.ParallelMode enumParallelMode = XmlSuite.ParallelMode.getValidParallel(parallelMode);
		topLevelSuite.setParallel(enumParallelMode);
		topLevelSuite.setThreadCount(threadCount);
		topLevelSuite.setListeners(listeners);
		topLevelSuite.setExcludedGroups(excludedGroups);
		topLevelSuite.setIncludedGroups(includedGroups);
		topLevelSuite.setTimeOut(timeout);
		topLevelSuite.setPreserveOrder(isPreserveOrder);
	}

	protected void setSuiteTopLevelPostConfiguration() {

		//Create a list of XmlTests
		List<XmlTest> testsList = new ArrayList<>();
		testsList.add(topLevelTest);

		//Assign tests list to suite
		topLevelSuite.setTests(testsList);

		//Add the suite to the list of suites.
		List<XmlSuite> suitesList = new ArrayList<>();
		suitesList.add(topLevelSuite);

		//Create XML file based on the virtual XML content
		suitesList.forEach(xmlSuite -> {
			FileUtils.writeFile(basedir + suiteRelativePath, xmlSuite.toXml(), getLog());
		});
	}
}
