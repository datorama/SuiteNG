package com.datorama;

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
