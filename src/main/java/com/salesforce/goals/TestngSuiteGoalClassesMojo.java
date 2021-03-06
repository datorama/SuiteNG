/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.salesforce.goals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlTest;

import com.salesforce.AbstractTestngSuiteMojo;

/**
 * This goal will generate TestNG suite file with classes.
 */
@Mojo(name = "testng-generate-with-classes", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGoalClassesMojo extends AbstractTestngSuiteMojo {

	@Override
	public void generate() {

		scanProjectFiles();
		setTestClasses();
	}

	private void setTestClasses() {

		XmlTest xmlTest = new XmlTest(topLevelSuite);
		xmlTest.setName(getTestName());
		if(getTestLevelTimeoutInMilliseconds() > 0) {
			xmlTest.setTimeOut(getTestLevelTimeoutInMilliseconds());
		}

		List<XmlClass> xmlClassesList = new ArrayList<>();
		Set<Class<?>> classes = getTestsClasses();
		classes.forEach(clazz -> {
			xmlClassesList.add(new XmlClass(clazz, false));
		});

		xmlTest.setXmlClasses(xmlClassesList);
		topLevelTestsList.add(xmlTest);
	}

}
