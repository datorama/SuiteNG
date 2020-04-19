package com.datorama;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.testng.annotations.Test;
import org.testng.xml.XmlClass;
import org.testng.xml.XmlInclude;

/**
 * This goal will generate TestNG suite file with included methods.
 */
@Mojo(name = "testng-generate-with-methods", defaultPhase = LifecyclePhase.GENERATE_TEST_RESOURCES)
public class TestngSuiteGeneratorGoalMethodsMojo extends AbstractTestngSuiteGeneratorMojo {

	@Override
	public void generate() {
		setSuiteTopLevelPreConfiguration();
		setTestIncludeMethods(getTestMethodsPerClass());
		setSuiteTopLevelPostConfiguration();
	}

	private Map<String, List<String>> getTestMethodsPerClass() {

		FilesScanner scanner = new FilesScanner(urlClassLoader, getLog());

		return scanner.scanFilesMethodsWithAnnotations(basedir + testClassesDirectory, Test.class, ".class");
	}

	private void setTestIncludeMethods(Map<String, List<String>> methodNamesByClassNameMap) {

		List<XmlClass> classes = new ArrayList<>();

		methodNamesByClassNameMap.forEach((className, methods) -> {
			XmlClass xmlClass = new XmlClass(className);
			List<XmlInclude> xmlIncludeMethods = new ArrayList<>();
			methods.forEach(method -> {
				XmlInclude include = new XmlInclude(method);
				xmlIncludeMethods.add(include);
			});
			xmlClass.setIncludedMethods(xmlIncludeMethods);
			classes.add(xmlClass);
		});

		topLevelTest.setXmlClasses(classes);
	}

}
