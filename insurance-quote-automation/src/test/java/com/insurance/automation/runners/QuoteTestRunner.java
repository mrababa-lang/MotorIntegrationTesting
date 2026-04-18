package com.insurance.automation.runners;

import io.cucumber.testng.CucumberOptions;

/**
 * Executes quote-focused regression scenarios.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.insurance.automation.stepdefs", "com.insurance.automation.hooks"},
    tags = "@Regression and @Quote",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/report.html",
        "json:target/cucumber-reports/report.json"
    },
    monochrome = true
)
public class QuoteTestRunner extends TestRunner {
}
