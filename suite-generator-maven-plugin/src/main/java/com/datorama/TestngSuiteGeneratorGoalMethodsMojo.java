/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlTest;

/**
 * This goal will generate TestNG suite file with included methods.
 */
@Mojo(name = "testng-generate-with-methods", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGeneratorGoalMethodsMojo extends AbstractTestngSuiteGeneratorMojo {

	@Override
	public void generate() {
		setTestIncludeMethods(getTestMethodsPerClass());
	}

	private void setTestIncludeMethods(Map<Class<?>, List<Method>> methodsPerClassMap) {

		XmlTest xmlTest = new XmlTest(topLevelSuite);
		xmlTest.setName(getTestName());

		List<XmlClass> classes = new ArrayList<>();

		methodsPerClassMap.forEach((clazz, methods) -> {
			XmlClass xmlClass = new XmlClass(clazz);
			List<XmlInclude> xmlIncludeMethods = new ArrayList<>();
			methods.forEach(method -> {
				XmlInclude include = new XmlInclude(method.getName());
				xmlIncludeMethods.add(include);
			});
			xmlClass.setIncludedMethods(xmlIncludeMethods);
			classes.add(xmlClass);
		});

		xmlTest.setXmlClasses(classes);

		topLevelTestsList.add(xmlTest);
	}

	private Map<Class<?>, List<Method>> getTestMethodsPerClass() {

		FilesScanner scanner = new FilesScanner(urlClassLoader, getLog());
		scanner.scan(getBasedir() + getTestClassesDirectory());

		return scanner.getFilteredResults(buildFiltersByIncludedGroups());
	}
}
