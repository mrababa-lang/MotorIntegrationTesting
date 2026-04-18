package com.insurance.automation.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import com.insurance.automation.api.BaseApiClient;
import com.insurance.automation.api.QuoteApiClient;
import com.insurance.automation.api.ShoryEndpoints;
import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.helpers.ConfigurationTestHelper;
import com.insurance.automation.report.InsuranceQuoteReportGenerator;
import com.insurance.automation.runners.TestRunner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Step definitions implementing the two-quote configuration eligibility test pattern.
 */
public class ConfigurationEligibilityStepDefs extends BaseApiClient {

    private final ScenarioContext context;
    private final ConfigurationTestHelper configHelper;

    public ConfigurationEligibilityStepDefs(final ScenarioContext context) {
        this.context = context;
        this.configHelper = new ConfigurationTestHelper(new QuoteApiClient(), context);
    }

    @Given("the UAT environment is configured")
    public void uatEnvironmentConfigured() {
        assertThat(config.baseUrl()).contains("motor-uat.shory.com");
    }

    @And("I have retrieved vehicle details for personalId {long} and plateNumber {string}")
    public void retrieveVehicleDetails(final long personalId, final String plateNumber) {
        final Map<String, Object> plateInfo = new LinkedHashMap<>();
        plateInfo.put("plateNumber", plateNumber);
        plateInfo.put("plateKindId", 1);
        plateInfo.put("plateSourceId", 1);
        plateInfo.put("plateColorTypeId", 52);

        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("vehicleIdentity", 2);
        payload.put("personalId", personalId);
        payload.put("plateInfo", plateInfo);

        final io.restassured.response.Response response = buildSpec().body(payload).post(ShoryEndpoints.VEHICLE_RETRIEVE);
        context.setLastApiResponse(response);
        context.setLastHttpStatus(response.statusCode());
        context.setLastResponseTimeMs(response.time());
        context.setRawLastResponse(response.asString());

        if (Boolean.TRUE.equals(response.jsonPath().getBoolean("isSuccess"))) {
            context.setVehicleId(response.jsonPath().getString("details.vehicle[0].id"));
            context.setCustomerId(response.jsonPath().getString("details.customerId"));
            context.setCustomerLicenseId(response.jsonPath().getString("details.customerLicenseId"));
        }
    }

    @And("vehicle retrieval was successful")
    public void vehicleRetrievalWasSuccessful() {
        assertThat(context.getLastApiResponse().jsonPath().getBoolean("isSuccess")).isTrue();
        assertThat(context.getVehicleId()).isNotBlank();
        assertThat(context.getCustomerId()).isNotBlank();
    }

    @When("I run the configuration probe with insuranceTypeId {int} and constraint insuranceTypeId {int}")
    public void runConfigurationProbe(final int probeInsuranceTypeId, final int constraintInsuranceTypeId) {
        final ConfigurationTestHelper.ConfigResult result = configHelper.evaluate(
            quoteRequest(probeInsuranceTypeId),
            quoteRequest(constraintInsuranceTypeId));
        context.setLastConfigResult(result);
        recordResult(result, probeInsuranceTypeId, constraintInsuranceTypeId);
    }

    @Then("the configuration {string} should be evaluated")
    public void configurationShouldBeEvaluated(final String configKey) {
        assertThat(context.getLastConfigResult()).isNotNull();
        final InsuranceQuoteReportGenerator reporter = TestRunner.getReporter();
        final ConfigurationTestHelper.ConfigResult result = context.getLastConfigResult();
        final Boolean enabled = result == ConfigurationTestHelper.ConfigResult.ENABLED
            ? Boolean.TRUE
            : result == ConfigurationTestHelper.ConfigResult.DISABLED ? Boolean.FALSE : null;

        reporter.addConfiguration(InsuranceQuoteReportGenerator.ConfigurationResult.builder()
            .category("Quote Eligibility Rules")
            .key(configKey)
            .label("Restrict TPL for Existing TPL Policy Holders")
            .enabled(enabled)
            .value("insuranceTypeId=1 blocked when currentPolicy.typeId=1")
            .lastUpdated(null)
            .build());
    }

    private Map<String, Object> quoteRequest(final int insuranceTypeId) {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("vehicleId", context.getVehicleId());
        body.put("customerId", context.getCustomerId());
        context.getCustomerLicenseId().ifPresent(value -> body.put("customerLicenseId", value));
        body.put("insuranceTypeId", insuranceTypeId);
        return body;
    }

    private void recordResult(final ConfigurationTestHelper.ConfigResult result,
                              final int probeInsuranceTypeId,
                              final int constraintInsuranceTypeId) {
        final InsuranceQuoteReportGenerator.TestResult.TestResultBuilder testResult =
            InsuranceQuoteReportGenerator.TestResult.builder()
                .id("CFG-001")
                .name("Restrict TPL for Existing TPL Policy")
                .description("Verifies that TPL quotes are rejected for vehicles with existing TPL policy.")
                .category("configuration")
                .input("insuranceTypeId_probe=" + probeInsuranceTypeId + ", insuranceTypeId_constraint=" + constraintInsuranceTypeId);

        switch (result) {
            case ENABLED -> testResult.status("PASS")
                .expected("TPL quote rejected (config enforced)")
                .actual("TPL quote was rejected — configuration is active")
                .match(Boolean.TRUE);
            case DISABLED -> testResult.status("FAIL")
                .expected("TPL quote rejected (config enforced)")
                .actual("TPL quote was generated — configuration is NOT active")
                .match(Boolean.FALSE);
            case DATA_ISSUE -> testResult.status("SKIP")
                .expected("Probe quote succeeds with valid data")
                .actual("Probe quote failed — data issue, cannot test configuration")
                .match(Boolean.FALSE)
                .error(context.getLastConfigError());
            default -> throw new IllegalStateException("Unexpected value: " + result);
        }
        TestRunner.getReporter().addResult(testResult.build());
    }
}
