# Afterpay Coding Assignment

![Build Status](https://github.com/sreeram-boyapati/credit-fraud-detector/workflows/Java%20CI%20with%20Gradle/badge.svg)
![Coverage](https://github.com/sreeram-boyapati/credit-fraud-detector/blob/master/.github/badges/jacoco.svg)

<br/>
A credit fraud detector that parses a file and gives out fraudalent IDs as per the threshold amount provided as an option.

Recommended IDE to run the code: IntelliJ Idea 2020.3 <br/>
Build System: Gradle 6.8.1 <br/>
Java Target: Java 8 <br/>
Testing Engine: JUnit 5 <br/>

Third Party Libraries Used:
1. Picocli - https://picocli.info/ - a mighty tiny command line interface.
2. OpenCSV - For parsing CSV files.
3. Hamcrest - for more powerful matchers to assert in tests.

## Installation

The project uses Gradle 6.8.1 as the build tool.

Please run `./gradlew build` to build the project for generating the CLI tool.
The CLI tool is path dependent on libraries and will be in the folder `build/distributions/cfd-0.1-DEMO/bin`

For distribution, Please create a symbolic link to cli tool using the following command for general usage
`sudo ln -s build/distributions/cfd-0.1-DEMO/bin/cfd ~/.local/bin/cfd`.


## Run Configurations
Please use the CLI_tool run configuration in the `.idea` directory to test run
the application with test files from the `src/test/resources` java.

For standalone testing use `./gradlew run`. <br\>
Here is an example command using test resources:
`./gradlew run --args="\$150 src/test/resources/testcase_valid_overlapping.csv -v"`

## Testing
Please run the following command to run the tests
`./gradlew test`

Tests are located in the `src/test/java/` folder and follow the layout guidelines for gradle/maven projects.

Please use the testing options specified in https://docs.gradle.org/current/userguide/java_testing.html#full_qualified_name_pattern
to run individual tests


## Project Layout
1. com.interview.afterpay.entities - Serializable classes for objects
   like CreditRecord and FraudResult.
2. com.interview.afterpay.cfd - The CLI wrapper to access the cfd
   functionality.
3. com.interview.afterpay.frauddetector.builder - Creational pattern to build
   detector with flexible number of constraints
4. com.interview.afterpay.frauddetector.rules - Constraints against which the
   dataset is evaluated. Only Batch is supported at the present. Ex:
   CannotExceedWithdrawal evaluates against a dataset and prints fraudulent IDs
   in O(N) time complexity.
5. com.interview.afterpay.frauddetector.BatchCreditFraudDetector - The View
   that is constructed using a builder which takes a dataset, passes it to
   constraints and returns the FraudResult.
6. com.interview.afterpay.frauddetector.processor - The processors to
   deserialize a credit card statement to a list of CreditRecord entities. Only
   CSV file is supported at present.
