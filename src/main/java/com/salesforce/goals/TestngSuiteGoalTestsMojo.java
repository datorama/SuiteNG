/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.goals;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;
import org.testng.xml.XmlTest;

import com.salesforce.AbstractTestngSuiteMojo;
import com.salesforce.utils.StringsUtils;

/**
 * This goal will generate TestNG suite file with included methods.
 */
@Mojo(name = "testng-generate-with-tests", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGoalTestsMojo extends AbstractTestngSuiteMojo {

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
				xmlTest.setName(getTestName(cls, method));
				if(getTestLevelTimeoutInMilliseconds() > 0) {
					xmlTest.setTimeOut(getTestLevelTimeoutInMilliseconds());
				}
				xmlTest.setXmlClasses(classes);
				topLevelTestsList.add(xmlTest);
			});
		});
	}

	private String getTestName(Class<?> cls, Method method) {
		if (!getTestCaseId().isEmpty()) {
			String testCaseId = getTestCaseID(method);
			if (StringUtils.isNotEmpty(testCaseId)) {
				return String.format("%s - %s", testCaseId, method.getName());
			}
		}

		return String.format("%s # %s", cls.getCanonicalName(), method.getName());
	}

	private String getTestCaseID(Method method) {

		String testCaseID = null;

		for (int index = 0; index < getTestCaseId().size(); index++) {
			String testCaseIdAnnotation = StringsUtils.parseClassName(getTestCaseId().get(index).toString());
			String testCaseIdAttribute = StringsUtils.parseMethodName(getTestCaseId().get(index).toString());

			Class annotationClass;
			try {
				annotationClass = Class.forName(testCaseIdAnnotation);
				if (method.isAnnotationPresent(annotationClass)) {
					Annotation annotation = method.getAnnotation(annotationClass);
					testCaseID = annotation.getClass().getMethod(testCaseIdAttribute).invoke(annotation, new Object[0]).toString();
				}
			} catch (ClassNotFoundException | NullPointerException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				getLog().debug(e);
			}

			if (StringUtils.isNotEmpty(testCaseID)) {
				break;
			}
		}

		return testCaseID;
	}

}
