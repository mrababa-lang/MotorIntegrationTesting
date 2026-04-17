package com.insurance.automation.runners;

import com.insurance.automation.config.ConfigManager;
import com.insurance.automation.config.EnvironmentConfig;
import com.insurance.automation.report.InsuranceQuoteReportGenerator;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Cucumber + TestNG runner and suite lifecycle host.
 */
@CucumberOptions(
    features = "src/test/resources/features",
    glue = {"com.insurance.automation.stepdefs", "com.insurance.automation.hooks"},
    tags = "@Regression",
    plugin = {
        "pretty",
        "html:target/cucumber-reports/report.html",
        "json:target/cucumber-reports/report.json"
    },
    monochrome = true
)
public class TestRunner extends AbstractTestNGCucumberTests {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);
    private static InsuranceQuoteReportGenerator reporter;

    /**
     * Initializes report generator before suite execution.
     */
    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        final EnvironmentConfig config = ConfigManager.getConfig();
        final String buildId = System.getProperty("buildId", "local-build");
        reporter = new InsuranceQuoteReportGenerator(
            InsuranceQuoteReportGenerator.RunInfo.builder()
                .environment(System.getProperty("env", "uat"))
                .buildId(buildId)
                .startedAt(LocalDateTime.now().toString())
                .baseUrl(config.baseUrl())
                .build(),
            config.reportTemplatePath(),
            config.reportOutputDir());
        LOG.info("Initialized report generator for build={} env={}", buildId, System.getProperty("env", "uat"));
    }

    /**
     * Finalizes report after suite execution.
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (reporter == null) {
            LOG.error("Reporter was not initialized; skipping generation.");
            return;
        }
        final String output = reporter.generate();
        if (output == null) {
            LOG.error("Report generation skipped due to missing template or write failure.");
            return;
        }
        LOG.info("Report output path: {}", output);
    }

    /**
     * Returns singleton suite reporter.
     *
     * @return report generator instance.
     */
    public static InsuranceQuoteReportGenerator getReporter() {
        return reporter;
    }
}
