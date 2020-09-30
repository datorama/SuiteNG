# SuiteNG

This maven plugin provide the ability to dynamically create suite files for automation frameworks.

This plugin has 5 goals:
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

## Usage

Add dependency in pom file with the configuration:
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
          <isPreserveOrder>...</isPreserveOrder>
          <parallelMode>...</parallelMode>
          <verbose>...</verbose>
          ...
        </configuration>
      </plugin>
    </plugins>
  </build>
  ...
</project>
```

##  Available parameters

```
    basedir (Default: ${project.basedir}/)
      The project base directory path.
      User property: project.basedir

    classesDirectory (Default: target/classes/)
      The path to tests classes directory in project.
      User property: classes.directory

    excludedGroups
      The excluded groups to configure in xml file.
      User property: excluded.groups

    includedGroups
      The included groups to configure in xml file.
      User property: included.groups

    isPreserveOrder
      The preserve order to configure in xml file.
      User property: preserve.order

    listeners
      The listeners to configure in xml file.
      User property: listeners

    parallelMode (Default: none)
      The parallel mode to configure in xml file.
      User property: parallel.mode

    suiteName (Default: default)
      The suite name to configure in xml file.
      User property: suite.name

    suiteRelativePath (Default: src/test/resources/suite.xml)
      The destination path (including filename) of the xml file.
      User property: suite.relative.path

    testClassesDirectory (Default: target/test-classes/)
      The path to tests classes directory in project.
      User property: test.classes.directory

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

    timeout
      The timeout to configure in xml file.
      User property: timeout

    verbose
      The verbose level to configure in xml file.
      User property: verbose
```

## Notes

* Plugin only supports TestNG framework at this point
* Project should include dependency of maven-surefire-plugin

## Contributing

Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

## License

[New BSD License](https://opensource.org/licenses/BSD-3-Clause)
