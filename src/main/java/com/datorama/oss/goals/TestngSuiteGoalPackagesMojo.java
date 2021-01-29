/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama.oss.goals;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.xml.XmlPackage;
import org.testng.xml.XmlTest;

import com.datorama.oss.AbstractTestngSuiteMojo;

/**
 * This goal will generate TestNG suite file with packages.
 */
@Mojo(name = "testng-generate-with-packages", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGoalPackagesMojo extends AbstractTestngSuiteMojo {

	@Override
	public void generate() {

		setTestsPackage();
	}

	private void setTestsPackage() {

		XmlTest xmlTest = new XmlTest(topLevelSuite);
		xmlTest.setName(getTestName());
		if(getTestLevelTimeoutInMilliseconds() > 0) {
			xmlTest.setTimeOut(getTestLevelTimeoutInMilliseconds());
		}

		List<XmlPackage> packagesList = new ArrayList<>();
		packagesList.add(new XmlPackage(getTestsPackageName()));
		xmlTest.setXmlPackages(packagesList);

		topLevelTestsList.add(xmlTest);
	}
}
