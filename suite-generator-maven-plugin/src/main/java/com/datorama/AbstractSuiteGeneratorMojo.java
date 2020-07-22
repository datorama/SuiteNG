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
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import com.datorama.scanners.DependencyScanner;
import com.datorama.scanners.FilesScanner;
import com.google.common.io.Files;

public abstract class AbstractSuiteGeneratorMojo extends AbstractMojo {

	private FilesScanner filesScanner;

	/**
	 * Information about this plugin, mainly used to lookup this plugin's configuration from the currently executing
	 * project.
	 *
	 * @since 2.12
	 */
	@Parameter(defaultValue = "${plugin}", readonly = true, required = true)
	private PluginDescriptor pluginDescriptor;

	/**
	 * The Maven project.
	 */
	@Parameter(property = "maven.project", defaultValue = "${project}", readonly = true)
	private MavenProject project;

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
	private List<String> listeners;

	/**
	 * The excluded groups to configure in xml file.
	 */
	@Parameter(property = "excluded.groups")
	private List<String> excludedGroups;

	/**
	 * The included groups to configure in xml file.
	 */
	@Parameter(property = "groups")
	private List<String> groups;

	/**
	 * The list of configurations for included annotation filter of the scanned classes / methods.
	 * Examples:
	 * 1. org.testng.annotations.Test
	 * 2. org.testng.annotations.Test#groups=sanity
	 */
	@Parameter(property = "included.annotation.filters")
	private List<String> includedAnnotationFilters;

	/**
	 * The list of configurations for excluded annotation filter of the scanned classes / methods.
	 * Examples:
	 * 1. org.testng.annotations.Test
	 * 2. org.testng.annotations.Test#groups=sanity
	 */
	@Parameter(property = "excluded.annotation.filters")
	private List<String> excludedAnnotationFilters;

	/**
	 * The list of custom 3rd party annotations and attributes for test case id used for suite test name.
	 * Examples:
	 * com.examples.TestCaseId#jiraId
	 */
	@Parameter(property = "test.case.id")
	private List<String> testCaseId;


	public PluginDescriptor getPluginDescriptor() {
		return pluginDescriptor;
	}

	public FilesScanner getFilesScanner() {
		return filesScanner;
	}

	public void setFilesScanner(FilesScanner filesScanner) {
		this.filesScanner = filesScanner;
	}

	public MavenProject getProject() {
		return project;
	}

	public void setProject(MavenProject project) {
		this.project = project;
	}

	public List<String> getIncludedAnnotationFilters() {
		return includedAnnotationFilters;
	}

	public void setIncludedAnnotationFilters(List<String> includedAnnotationFilters) {
		this.includedAnnotationFilters = includedAnnotationFilters;
	}

	public List<String> getExcludedAnnotationFilters() {
		return excludedAnnotationFilters;
	}

	public void setExcludedAnnotationFilters(List<String> excludedAnnotationFilters) {
		this.excludedAnnotationFilters = excludedAnnotationFilters;
	}

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

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public List getTestCaseId() {
		return testCaseId;
	}

	public void setTestCaseId(List testCaseId) {
		this.testCaseId = testCaseId;
	}

	protected void setPluginClasspath(List<String> additionalClasspathElements) {

		final ClassRealm classRealm = getPluginDescriptor().getClassRealm();

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

	protected void writeFile(final String filename, final String content) {
		try {
			File file = new File(getBasedir() + filename);
			Files.write(content.getBytes(), file);
			getLog().info(String.format("File %s generated successfully", file.getAbsolutePath()));
		} catch (IOException | NullPointerException e) {
			getLog().error(String.format("Failed to generate file %s", filename) + e);
		}
	}

	protected void scanProjectFiles() {
		ClassRealm classRealm = getPluginDescriptor().getClassRealm();
		filesScanner = FilesScanner.getInstance();
		getFilesScanner().init(new URLClassLoader(classRealm.getURLs(), classRealm), getLog());
		getFilesScanner().scan(getBasedir() + getTestClassesDirectory());
	}

	private String validatePathEndsWithFileSeparator(String path) {
		return (path.endsWith(File.separator)) ? path : (path + File.separator);
	}

}
