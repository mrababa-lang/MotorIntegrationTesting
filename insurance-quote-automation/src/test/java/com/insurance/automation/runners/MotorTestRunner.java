package com.insurance.automation.runners;

import io.cucumber.testng.CucumberOptions;

/**
 * Executes end-to-end motor regression scenarios.
 */
@CucumberOptions(
    features = "src/test/resources/features/motor",
    glue = {"com.insurance.automation.stepdefs", "com.insurance.automation.hooks"},
    tags = "@motor",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/report.html",
        "json:target/cucumber-reports/report.json"
    },
    monochrome = true
)
public class MotorTestRunner extends TestRunner {
}
