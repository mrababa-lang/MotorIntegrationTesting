package com.insurance.automation.runners;

import com.insurance.automation.config.ConfigManager;
import com.insurance.automation.config.EnvironmentConfig;
import com.insurance.automation.config.InsuranceCompanyRegistry;
import com.insurance.automation.config.InsuranceCompanyRegistry.CompanyProfile;
import com.insurance.automation.report.InsuranceQuoteReportGenerator;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

/**
 * Shared Cucumber + TestNG suite lifecycle host.
 *
 * <p>The {@code insuranceCompanyProfileId} parameter controls which company is under test.
 * Set it in {@code testng.xml} or override on the command line:
 * <pre>mvn test -DinsuranceCompanyProfileId=3</pre>
 * The Configuration section of the report is populated by config feature scenarios,
 * not by hardcoded entries here.
 */
public abstract class TestRunner extends AbstractTestNGCucumberTests {

    private static final Logger LOG = LoggerFactory.getLogger(TestRunner.class);
    private static InsuranceQuoteReportGenerator reporter;
    private static int activeCompanyProfileId;

    /**
     * Initialises the report generator before the suite runs.
     *
     * @param companyIdParam insuranceCompanyProfileId from testng.xml (default "6").
     *                       A command-line -DinsuranceCompanyProfileId takes precedence.
     */
    @BeforeSuite(alwaysRun = true)
    @Parameters("insuranceCompanyProfileId")
    public void beforeSuite(@Optional("6") final String companyIdParam) {
        // Command-line property wins over testng.xml parameter
        final String resolved = System.getProperty("insuranceCompanyProfileId", companyIdParam);
        activeCompanyProfileId = parseCompanyId(resolved, 6);

        final CompanyProfile company = InsuranceCompanyRegistry.get(activeCompanyProfileId);
        final EnvironmentConfig config = ConfigManager.getConfig();
        final String buildId = System.getProperty("buildId", "local-build");

        reporter = new InsuranceQuoteReportGenerator(
            InsuranceQuoteReportGenerator.RunInfo.builder()
                .environment(System.getProperty("env", "uat"))
                .buildId(buildId)
                .startedAt(LocalDateTime.now().toString())
                .baseUrl(config.baseUrl())
                .insuranceCompanyProfileId(company.getId())
                .insuranceCompanyName(company.getName())
                .insuranceCompanyLogo(company.getLogoUrl())
                .build(),
            config.reportTemplatePath(),
            config.reportOutputDir());

        LOG.info("Suite started — company={} (id={}) build={} env={}",
            company.getName(), company.getId(), buildId, System.getProperty("env", "uat"));
    }

    /**
     * Generates the HTML report after all scenarios have run.
     */
    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        if (reporter == null) {
            LOG.error("Reporter was not initialised; skipping generation.");
            return;
        }
        final String output = reporter.generate();
        if (output == null) {
            LOG.error("Report generation failed — check template path and write permissions.");
            return;
        }
        LOG.info("Report written to: {}", output);
    }

    /**
     * Returns the singleton suite reporter.
     */
    public static InsuranceQuoteReportGenerator getReporter() {
        return reporter;
    }

    /**
     * Returns the insurance company profile ID active for this suite run.
     */
    public static int getActiveCompanyProfileId() {
        return activeCompanyProfileId;
    }

    private static int parseCompanyId(final String value, final int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            LOG.warn("Invalid insuranceCompanyProfileId '{}'; defaulting to {}", value, fallback);
            return fallback;
        }
    }
}
