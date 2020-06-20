/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlTest;

/**
 * This goal will generate TestNG suite file with included methods.
 */
@Mojo(name = "testng-generate-with-tests", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGeneratorGoalTestsMojo extends AbstractTestngSuiteGeneratorMojo {

	@Override
	public void generate() {
		setSuiteTopLevelPreConfiguration();
		setTestIncludeMethods(getTestMethodsPerClass());
		setSuiteTopLevelPostConfiguration();
	}

	private Map<String, List<String>> getTestMethodsPerClass() {

		FilesScanner scanner = new FilesScanner(urlClassLoader, getLog());

		return scanner.scanFilesMethodsWithAnnotations(basedir + testClassesDirectory, Test.class, ".class");
	}

	private void setTestIncludeMethods(Map<String, List<String>> methodNamesByClassNameMap) {

		methodNamesByClassNameMap.forEach((className, methods) -> {
			methods.forEach(method -> {


				XmlInclude include = new XmlInclude(method);
				List<XmlInclude> xmlIncludeMethods = new ArrayList<>();
				xmlIncludeMethods.add(include);

				XmlClass xmlClass = new XmlClass(className);
				xmlClass.setIncludedMethods(xmlIncludeMethods);
				List<XmlClass> classes = new ArrayList<>();
				classes.add(xmlClass);

				XmlTest xmlTest = new XmlTest(topLevelSuite);
				xmlTest.setName(method);
				xmlTest.setXmlClasses(classes);

				topLevelTestsList.add(xmlTest);
			});
		});

	}
}
