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
