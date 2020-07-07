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
@Mojo(name = "testng-generate-with-tests", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGeneratorGoalTestsMojo extends AbstractTestngSuiteGeneratorMojo {

	@Override
	public void generate() {

		scanProjectFiles();
		setTestIncludeMethods(getTestMethodsPerClass());
	}

	private void setTestIncludeMethods(Map<Class<?>, List<Method>> methodsPerClassMap) {

		methodsPerClassMap.forEach((clazz, methods) -> {
			methods.forEach(method -> {
				XmlInclude include = new XmlInclude(method.getName());
				List<XmlInclude> xmlIncludeMethods = new ArrayList<>();
				xmlIncludeMethods.add(include);

				XmlClass xmlClass = new XmlClass(clazz);
				xmlClass.setIncludedMethods(xmlIncludeMethods);
				List<XmlClass> classes = new ArrayList<>();
				classes.add(xmlClass);

				XmlTest xmlTest = new XmlTest(topLevelSuite);
				xmlTest.setName(clazz.getCanonicalName() + "." + method.getName());
				xmlTest.setXmlClasses(classes);
				xmlTest.setExcludedGroups(getExcludedGroups());
				xmlTest.setIncludedGroups(getIncludedGroups());
				topLevelTestsList.add(xmlTest);
			});
		});
	}

	private Map<Class<?>, List<Method>> getTestMethodsPerClass() {

		List<Filter> filters = (getIncludedGroups().isEmpty()) ? buildFiltersByTestAnnotation() : buildFiltersByIncludedGroups();

		return getFilesScanner().getFilteredResults(filters);
	}

}
