package com.insurance.automation.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import com.insurance.automation.api.ConfigApiClient;
import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.models.response.ConfigStatusResponse;
import com.insurance.automation.report.InsuranceQuoteReportGenerator;
import com.insurance.automation.runners.TestRunner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

/**
 * Step definitions for configuration status validation.
 */
public class ConfigurationStepDefs {

    private final ScenarioContext scenarioContext;
    private final ConfigApiClient configApiClient;

    /**
     * Creates configuration step definitions.
     *
     * @param scenarioContext shared scenario context.
     */
    public ConfigurationStepDefs(final ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.configApiClient = new ConfigApiClient();
    }

    /**
     * Calls config API by key.
     *
     * @param configKey key to query.
     */
    @When("the configuration status API is called for key {string}")
    public void callConfigStatusApi(final String configKey) {
        final ConfigStatusResponse response = configApiClient.getConfigStatus(configKey);
        scenarioContext.setLastConfigResponse(response);
    }

    /**
     * Asserts enabled flag.
     *
     * @param expected expected enabled state.
     */
    @Then("the configuration enabled state is {word}")
    public void verifyEnabledState(final String expected) {
        assertThat(scenarioContext.getLastConfigResponse().getEnabled()).isEqualTo(Boolean.parseBoolean(expected));
        scenarioContext.setLastExpectedStatement("configuration enabled state is " + expected);
    }

    /**
     * Asserts configuration value.
     *
     * @param expected expected value.
     */
    @And("the configuration value is {string}")
    public void verifyConfigValue(final String expected) {
        assertThat(scenarioContext.getLastConfigResponse().getValue()).isEqualTo(expected);
        scenarioContext.setLastExpectedStatement("configuration value is " + expected);
    }

    /**
     * Appends config entry to final report.
     *
     * @param category report category.
     */
    @And("the result is recorded in the report as category {string}")
    public void recordConfigInReport(final String category) {
        final ConfigStatusResponse config = scenarioContext.getLastConfigResponse();
        final InsuranceQuoteReportGenerator reporter = TestRunner.getReporter();
        reporter.addConfiguration(InsuranceQuoteReportGenerator.ConfigurationResult.builder()
            .category(category)
            .key(config.getKey())
            .label(config.getLabel())
            .enabled(config.getEnabled())
            .value(config.getValue())
            .lastUpdated(config.getLastUpdated())
            .build());
    }
}
