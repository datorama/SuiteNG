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
import java.util.Set;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;

/**
 * This goal will generate TestNG suite file with classes.
 */
@Mojo(name = "testng-generate-with-classes", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGeneratorGoalClassesMojo extends AbstractTestngSuiteGeneratorMojo {

	@Override
	public void generate() {
		setSuiteTopLevelPreConfiguration();
		setTestClasses();
		setSuiteTopLevelPostConfiguration();
	}

	private void setTestClasses() {

		List<XmlClass> classesList = new ArrayList<>();
		Set<String> classNames = getTestsClassNames();
		classNames.forEach(className -> {
			classesList.add(new XmlClass(className, false));
		});
		topLevelTest.setXmlClasses(classesList);
	}

	private Set<String> getTestsClassNames() {

		FilesScanner scanner = new FilesScanner(urlClassLoader, getLog());
		Set<String> classNames = scanner.scanFilesMethodsWithAnnotations(basedir + testClassesDirectory, Test.class, ".class").keySet();

		return classNames;
	}

}
