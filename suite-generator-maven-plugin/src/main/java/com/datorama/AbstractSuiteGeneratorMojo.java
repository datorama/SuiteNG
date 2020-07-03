/*
 * Copyright (c) 2020, salesforce.com, inc.
 * All rights reserved.
 * SPDX-License-Identifier: BSD-3-Clause
 * For full license text, see the LICENSE file in the repo root or https://opensource.org/licenses/BSD-3-Clause
 */
package com.datorama;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import com.google.common.io.Files;

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
	private String basedir;

	/**
	 * The path to tests classes directory in project.
	 */
	@Parameter(property = "classes.directory", defaultValue = "target/classes/")
	private String classesDirectory;

	/**
	 * The path to tests classes directory in project.
	 */
	@Parameter(property = "test.classes.directory", defaultValue = "target/test-classes/")
	private String testClassesDirectory;

	/**
	 * The tests package name.
	 */
	@Parameter(property = "tests.package.name", defaultValue = "com.**")
	private String testsPackageName;

	/**
	 * The destination path (including filename) of the xml file.
	 */
	@Parameter(property = "suite.relative.path", defaultValue = "src/test/resources/suite.xml")
	private String suiteRelativePath;

	/**
	 * The suite name to configure in xml file.
	 */
	@Parameter(property = "suite.name", defaultValue = "default")
	private String suiteName;

	/**
	 * The test name parameter to configure in xml file.
	 */
	@Parameter(property = "test.name", defaultValue = "default")
	private String testName;

	/**
	 * The parallel mode to configure in xml file.
	 */
	@Parameter(property = "parallel.mode", defaultValue = "none")
	private String parallelMode;

	/**
	 * The thread count to configure in xml file (NOTE: ignored if parallel mode = "none").
	 */
	@Parameter(property = "thread.count", defaultValue = "1")
	private int threadCount;

	/**
	 * The timeout to configure in xml file.
	 */
	@Parameter(property = "timeout")
	private String timeout;

	/**
	 * The verbose level to configure in xml file.
	 */
	@Parameter(property = "verbose", defaultValue = "1")
	private int verbose;

	/**
	 * The preserve order to configure in xml file.
	 */
	@Parameter(property = "preserve.order")
	private boolean isPreserveOrder;

	/**
	 * The listeners to configure in xml file.
	 */
	@Parameter(property = "listeners")
	private List listeners;

	/**
	 * The excluded groups to configure in xml file.
	 */
	@Parameter(property = "excluded.groups")
	private List<String> excludedGroups;

	/**
	 * The included groups to configure in xml file.
	 */
	@Parameter(property = "included.groups")
	private List<String> includedGroups;

	protected FilesScanner scanner;


	public String getBasedir() {
		setBasedir(validatePathEndsWithFileSeparator(basedir));
		return basedir;
	}

	public void setBasedir(String basedir) {
		this.basedir = basedir;
	}

	public String getClassesDirectory() {
		setClassesDirectory(validatePathEndsWithFileSeparator(classesDirectory));
		return classesDirectory;
	}

	public void setClassesDirectory(String classesDirectory) {
		this.classesDirectory = classesDirectory;
	}

	public String getTestClassesDirectory() {
		setTestClassesDirectory(validatePathEndsWithFileSeparator(testClassesDirectory));
		return testClassesDirectory;
	}

	public void setTestClassesDirectory(String testClassesDirectory) {
		this.testClassesDirectory = testClassesDirectory;
	}

	public String getTestsPackageName() {
		return testsPackageName;
	}

	public void setTestsPackageName(String testsPackageName) {
		this.testsPackageName = testsPackageName;
	}

	public String getSuiteRelativePath() {
		return suiteRelativePath;
	}

	public void setSuiteRelativePath(String suiteRelativePath) {
		this.suiteRelativePath = suiteRelativePath;
	}

	public String getSuiteName() {
		return suiteName;
	}

	public void setSuiteName(String suiteName) {
		this.suiteName = suiteName;
	}

	public String getTestName() {
		return testName;
	}

	public void setTestName(String testName) {
		this.testName = testName;
	}

	public String getParallelMode() {
		return parallelMode;
	}

	public void setParallelMode(String parallelMode) {
		this.parallelMode = parallelMode;
	}

	public int getThreadCount() {
		return threadCount;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}

	public String getTimeout() {
		return timeout;
	}

	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}

	public int getVerbose() {
		return verbose;
	}

	public void setVerbose(int verbose) {
		this.verbose = verbose;
	}

	public boolean isPreserveOrder() {
		return isPreserveOrder;
	}

	public void setPreserveOrder(boolean preserveOrder) {
		isPreserveOrder = preserveOrder;
	}

	public List getListeners() {
		return listeners;
	}

	public void setListeners(List listeners) {
		this.listeners = listeners;
	}

	public List<String> getExcludedGroups() {
		return excludedGroups;
	}

	public void setExcludedGroups(List<String> excludedGroups) {
		this.excludedGroups = excludedGroups;
	}

	public List<String> getIncludedGroups() {
		return includedGroups;
	}

	public void setIncludedGroups(List<String> includedGroups) {
		this.includedGroups = includedGroups;
	}

	protected void setPluginClasspath(List<String> additionalClasspathElements) {

		final ClassRealm classRealm = pluginDescriptor.getClassRealm();

		// Add all classpath elements to plugin
		for (String classpathElement : additionalClasspathElements) {
			try {
				URL url = new File(classpathElement).toURI().toURL();
				classRealm.addURL(url);
				getLog().debug("Add Classpath Element URL: " + url.toString() + " to Plugin ClassRealm");
			} catch (MalformedURLException e) {
				getLog().error(e);
			}
		}
	}

	protected List<String> getProjectAdditionalClasspathElements() {

		DependencyScanner dependencyScanner = DependencyScanner.getInstance();
		dependencyScanner.init(new File(getBasedir()), getLog());
		dependencyScanner.scan();
		List<String> additionalClasspathElements = new ArrayList<>(dependencyScanner.getBuildClasspathElements());
		additionalClasspathElements.add(getBasedir() + getTestClassesDirectory());
		additionalClasspathElements.add(getBasedir() + getClassesDirectory());

		return additionalClasspathElements;
	}

	private String validatePathEndsWithFileSeparator(String path) {
		return (path.endsWith(File.separator)) ? path : (path + File.separator);
	}

	protected void writeFile(final String filename, final String content, Log log) {
		try {
			File file = new File(getBasedir() + filename);
			Files.write(content.getBytes(), file);
			log.info(String.format("File %s generated successfully", file.getAbsolutePath()));
		} catch (IOException | NullPointerException e) {
			log.error(String.format("Failed to generate file %s", filename) + e);
		}
	}

	protected void scanProjectFiles() {
		ClassRealm classRealm = pluginDescriptor.getClassRealm();
		scanner = FilesScanner.getInstance();
		scanner.init(new URLClassLoader(classRealm.getURLs(), classRealm), getLog());
		scanner.scan(getBasedir() + getTestClassesDirectory());
	}
}
