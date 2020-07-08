/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama.goals;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlTest;

import com.datorama.AbstractTestngSuiteGeneratorMojo;
import com.datorama.filters.Filter;
import com.datorama.filters.FiltersBuilder;

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

		methodsPerClassMap.forEach((cls, methods) -> {
			methods.forEach(method -> {
				XmlInclude include = new XmlInclude(method.getName());
				List<XmlInclude> xmlIncludeMethods = new ArrayList<>();
				xmlIncludeMethods.add(include);

				XmlClass xmlClass = new XmlClass(cls);
				xmlClass.setIncludedMethods(xmlIncludeMethods);
				List<XmlClass> classes = new ArrayList<>();
				classes.add(xmlClass);

				XmlTest xmlTest = new XmlTest(topLevelSuite);
				int testCaseID = getTestCaseID(method);
				xmlTest.setName(((testCaseID != -1)? (testCaseID + " - ") : (cls.getCanonicalName()+ "#"))  + method.getName());
				xmlTest.setXmlClasses(classes);
				xmlTest.setExcludedGroups(getExcludedGroups());
				xmlTest.setIncludedGroups(getIncludedGroups());
				topLevelTestsList.add(xmlTest);
			});
		});
	}

	private Map<Class<?>, List<Method>> getTestMethodsPerClass() {

		List<Filter> includedAnnotationFilters = FiltersBuilder.buildAnnotationFilters(getIncludedAnnotationFilters());
		List<Filter> excludedAnnotationFilters = FiltersBuilder.buildAnnotationFilters(getExcludedAnnotationFilters());

		return getFilesScanner().getFilteredResults(includedAnnotationFilters, excludedAnnotationFilters);
	}

	private int getTestCaseID(Method method) {

		final String TEST_CASE_ID_ANNOTATION = "com.datorama.listeners.TestCaseId";
		int testCaseID = -1;

		Class annotationClass;
		try {
			annotationClass = Class.forName(TEST_CASE_ID_ANNOTATION);
			if (method.isAnnotationPresent(annotationClass)) {
				Annotation annotation = method.getAnnotation(annotationClass);
				testCaseID = (int)annotation.getClass().getMethod("id").invoke(annotation, new Object[0]);
			}
		} catch (ClassNotFoundException | NullPointerException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			getLog().debug(e);
		}

		return testCaseID;
	}

}
