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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

public abstract class AbstractSuiteGeneratorMojo extends AbstractMojo {

	/**
	 * Information about this plugin, mainly used to lookup this plugin's configuration from the currently executing
	 * project.
	 *
	 * @since 2.12
	 */
	@Parameter(defaultValue = "${plugin}", readonly = true, required = true)
	protected PluginDescriptor pluginDescriptor;

	/**
	 * The Maven project.
	 */
	@Parameter(property = "maven.project", defaultValue = "${project}", readonly = true)
	protected MavenProject project;

	/**
	 * The project base directory path.
	 */
	@Parameter(property = "project.basedir", defaultValue = "${project.basedir}/")
	protected String basedir;

	/**
	 * The path to tests classes directory in project.
	 */
	@Parameter(property = "classes.directory", defaultValue = "target/classes/")
	protected String classesDirectory;

	/**
	 * The path to tests classes directory in project.
	 */
	@Parameter(property = "test.classes.directory", defaultValue = "target/test-classes/")
	protected String testClassesDirectory;

	/**
	 * The tests package name.
	 */
	@Parameter(property = "tests.package.name", defaultValue = "com.**")
	protected String testsPackageName;

	/**
	 * The destination path (including filename) of the xml file.
	 */
	@Parameter(property = "suite.relative.path", defaultValue = "src/test/resources/suite.xml")
	protected String suiteRelativePath;

	/**
	 * The suite name to configure in xml file.
	 */
	@Parameter(property = "suite.name", defaultValue = "default")
	protected String suiteName;

	/**
	 * The test name parameter to configure in xml file.
	 */
	@Parameter(property = "test.name", defaultValue = "default")
	protected String testName;

	/**
	 * The parallel mode to configure in xml file.
	 */
	@Parameter(property = "parallel.mode", defaultValue = "none")
	protected String parallelMode;

	/**
	 * The thread count to configure in xml file (NOTE: ignored if parallel mode = "none").
	 */
	@Parameter(property = "thread.count", defaultValue = "1")
	protected int threadCount;

	/**
	 * The timeout to configure in xml file.
	 */
	@Parameter(property = "timeout")
	protected String timeout;

	/**
	 * The verbose level to configure in xml file.
	 */
	@Parameter(property = "verbose")
	protected int verbose;

	/**
	 * The preserve order to configure in xml file.
	 */
	@Parameter(property = "preserve.order")
	protected boolean isPreserveOrder;

	/**
	 * The listeners to configure in xml file.
	 */
	@Parameter(property = "listeners")
	protected List listeners;

	/**
	 * The excluded groups to configure in xml file.
	 */
	@Parameter(property = "excluded.groups")
	protected List excludedGroups;

	/**
	 * The included groups to configure in xml file.
	 */
	@Parameter(property = "included.groups")
	protected List includedGroups;

	public abstract void generate();

	protected URLClassLoader setPluginClasspath(List<String> additionalClasspathElements) {

		pluginDescriptor = (PluginDescriptor) getPluginContext().get("pluginDescriptor");
		final ClassRealm classRealm = pluginDescriptor.getClassRealm();

		// Add all classpath elements to plugin
		for (String classpathElement : additionalClasspathElements) {
			final File classes = new File(classpathElement);

			try {
				classRealm.addURL(classes.toURI().toURL());
			} catch (MalformedURLException e) {
				getLog().error(e);
			}
		}

		// Add all classpath elements to project
		for (URL url : classRealm.getURLs()) {
			try {
				this.project.getCompileClasspathElements().add(url.getPath());
				this.project.getRuntimeClasspathElements().add(url.getPath());
				getLog().debug("Suite Generator Plugin ClassRealm URLs >>> " + url.toString());
			} catch (DependencyResolutionRequiredException e) {
				getLog().error(e);
			}
		}

		// Print all compile / runtime project classpath elements
		try {
			getLog().debug("compile cp: " +
					this.project.getCompileClasspathElements());
			getLog().debug("runtime cp: " +
					this.project.getRuntimeClasspathElements());
		} catch (DependencyResolutionRequiredException e) {
			getLog().error(e);
		}

		URLClassLoader urlClassLoader = new URLClassLoader(classRealm.getURLs(), classRealm);

		return urlClassLoader;
	}

	protected List<String> getProjectAdditionalClasspathElements() {

		DependencyScanner dependencyScanner = new DependencyScanner(new File(basedir), getLog());
		List<String> additionalClasspathElements = new ArrayList<>(dependencyScanner.scan());
		additionalClasspathElements.add(basedir + testClassesDirectory);
		additionalClasspathElements.add(basedir + classesDirectory);

		return additionalClasspathElements;
	}
}
