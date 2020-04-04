package com.datorama;

import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;

/**
 * This goal will generate TestNG suite file with classes.
 */
@Mojo(name = "testng-generate-with-classes", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
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
