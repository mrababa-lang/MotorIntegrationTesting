package com.insurance.automation.runners;

import io.cucumber.testng.CucumberOptions;

/**
 * Executes skip-rule-focused regression scenarios.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.insurance.automation.stepdefs", "com.insurance.automation.hooks"},
    tags = "@Regression and @SkipRule",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/report.html",
        "json:target/cucumber-reports/report.json"
    },
    monochrome = true
)
public class SkipRuleTestRunner extends TestRunner {
}
