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

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.xml.XmlPackage;

/**
 * This goal will generate TestNG suite file with packages.
 */
@Mojo(name = "testng-generate-with-packages", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGeneratorGoalPackagesMojo extends AbstractTestngSuiteGeneratorMojo {

	@Override
	public void generate() {
		setSuiteTopLevelPreConfiguration();
		setTestsPackage();
		setSuiteTopLevelPostConfiguration();
	}

	private void setTestsPackage() {

		List<XmlPackage> packagesList = new ArrayList<>();
		packagesList.add(new XmlPackage(testsPackageName));
		topLevelTest.setXmlPackages(packagesList);
	}

}
