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
        addShoryMotorConfigurations();
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

    private void addShoryMotorConfigurations() {
        reporter.addConfiguration(configuration("vehicleIdentity_plate", "Plate-Based Lookup (vehicleIdentity=2)", "Vehicle Lookup", "vehicleIdentity=2"));
        reporter.addConfiguration(configuration("vehicleIdentity_vcc", "VCC-Based Lookup (vehicleIdentity=3)", "Vehicle Lookup", "vehicleIdentity=3"));
        reporter.addConfiguration(configuration("customLangHeader", "Custom Language Header (AR)", "Request Config", "custom-lang: AR"));
        reporter.addConfiguration(configuration("quoteOfferPolling", "Quote Offer Polling", "Quote Flow", "max=10, interval=3000ms"));
        reporter.addConfiguration(configuration("nullLicenseHandling", "Null CustomerLicenseId Handling", "Quote Flow", "omit field if null"));
        reporter.addConfiguration(configuration("offerFeatureCode_tpl", "Third Party Liability Feature (code=1)", "Offer Features", "code=1"));
        reporter.addConfiguration(configuration("offerFeatureCode_carRental", "Car Rental Feature (code=3)", "Offer Features", "code=3"));
        reporter.addConfiguration(configuration("offerFeatureCode_roadside", "Roadside Assistance Feature (code=4)", "Offer Features", "code=4"));
    }

    private InsuranceQuoteReportGenerator.ConfigurationResult configuration(
        final String key,
        final String label,
        final String category,
        final String value) {
        return InsuranceQuoteReportGenerator.ConfigurationResult.builder()
            .key(key)
            .label(label)
            .category(category)
            .enabled(Boolean.TRUE)
            .value(value)
            .build();
    }
}
