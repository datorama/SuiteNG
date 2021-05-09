# SuiteNG

The maven plugin provides the ability to dynamically auto-generate suite files for automation frameworks.


## How to Install Plugin 

The plugin is published in GitHub Packages.
To [install](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#installing-a-package) the plugin follow those steps:

1. Authenticate to GitHub Packages. For more information, see "[Authenticating to GitHub Packages](https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-apache-maven-registry#authenticating-to-github-packages)"

2. Add dependency in pom file:

```
<project>
  ...
  <build>
    <plugins>
      <plugin>
        <groupId>com.datorama</groupId>
        <artifactId>suiteng-maven-plugin</artifactId>
        <version>X.X.X</version>
        <configuration>
          ...
        </configuration>
      </plugin>
    </plugins>
  </build>
  ...
</project>
```

3. Install the package.
```
$ mvn install
```


## Plugin Goals
```
suiteng:help
  Display help information on suiteng-maven-plugin.
  Call mvn suiteng:help -Ddetail=true -Dgoal=<goal-name> to display
  parameter details.

suiteng:testng-generate-with-classes
  This goal will generate TestNG suite file with classes.

suiteng:testng-generate-with-methods
  This goal will generate TestNG suite file with included methods.

suiteng:testng-generate-with-packages
  This goal will generate TestNG suite file with packages.

suiteng:testng-generate-with-tests
  This goal will generate TestNG suite file with included methods.
```


##  Plugin Arguments

```
    basedir (Default: ${project.basedir}/)
      The project base directory path.
      User property: project.basedir

    classesDirectory (Default: target/classes/)
      The path to tests classes directory in project.
      User property: classes.directory

    excludedAnnotationFilters
      The list of configurations for excluded annotation filter of the scanned
      classes\methods. 
      Examples: 
      1. org.testng.annotations.Test 
      2. org.testng.annotations.Test#groups=sanity
      User property: excluded.annotation.filters

    excludedGroups
      The excluded groups to configure in xml file.
      User property: excluded.groups

    excludedTests
      The list of tests classes\methods to exclude. 
      Examples: 
      1. org.example.Test (class name only) 
      2. org.example.Test#test1 
      3. #test1 (method name only)
      User property: excludes

    groups
      The included groups to configure in xml file.
      User property: groups

    includedAnnotationFilters
      The list of configurations for included annotation filter of the scanned
      classes\methods. 
      Examples: 
      1. org.testng.annotations.Test 
      2. org.testng.annotations.Test#groups=sanity
      User property: included.annotation.filters

    includedTests
      The list of tests classes\methods to include. 
      Examples: 
      1. org.example.Test (class name only) 
      2. org.example.Test#test1 
      3. #test1 (method name only)
      User property: includes

    isPreserveOrder
      The preserve order to configure in xml file.
      User property: preserve.order

    listeners
      The listeners to configure in xml file.
      User property: listeners

    parallelMode (Default: none)
      The parallel mode to configure in xml file.
      User property: parallel.mode

    suiteLevelTimeoutInMilliseconds (Default: 0)
      The suite level timeout in milliseconds to configure in xml file.
      User property: suite.timeout

    suiteName (Default: default)
      The suite name to configure in xml file.
      User property: suite.name

    suiteRelativePath (Default: src/test/resources/suite.xml)
      The destination path (including filename) of the xml file.
      User property: suite.relative.path

    testCaseId
      The list of custom 3rd party annotations and attributes for test case id
      used for suite test name. Examples: com.examples.TestCaseId#jiraId
      User property: test.case.id

    testClassesDirectory (Default: target/test-classes/)
      The path to tests classes directory in project.
      User property: test.classes.directory

    testLevelTimeoutInMilliseconds (Default: 0)
      The test level timeout in milliseconds to configure in xml file.
      User property: test.timeout

    testName (Default: default)
      The test name parameter to configure in xml file.
      User property: test.name

    testsPackageName (Default: com.**)
      The tests package name.
      User property: tests.package.name

    threadCount (Default: 1)
      The thread count to configure in xml file (NOTE: ignored if parallel mode
      = 'none').
      User property: thread.count

    verbose (Default: 1)
      The verbose level to configure in xml file.
      User property: verbose

```


## Notes

* The plugin currently only supports TestNG framework


## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.


## License

[New BSD License](https://opensource.org/licenses/BSD-3-Clause)
