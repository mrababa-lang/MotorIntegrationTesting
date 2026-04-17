package com.insurance.automation.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import com.insurance.automation.api.QuoteApiClient;
import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.models.request.QuoteRequest;
import com.insurance.automation.models.response.QuoteResponse;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.math.BigDecimal;

/**
 * Quote API step definitions for premium validation.
 */
public class QuoteStepDefs {

    private final ScenarioContext scenarioContext;
    private final QuoteApiClient quoteApiClient;

    /**
     * Creates quote step definitions.
     *
     * @param scenarioContext shared scenario context.
     */
    public QuoteStepDefs(final ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
        this.quoteApiClient = new QuoteApiClient();
    }

    /**
     * Builds applicant baseline data in the quote request.
     */
    @Given("a quote request for state {string}, age {int}, vehicle year {int}")
    public void setApplicantDetails(final String state, final int age, final int year) {
        scenarioContext.setCurrentRequest(QuoteRequest.builder()
            .state(state)
            .age(age)
            .vehicleYear(year)
            .build());
    }

    /**
     * Sets coverage and credit data in current request.
     */
    @And("coverage type is {string} with credit score {int}")
    public void setCoverageAndCredit(final String coverageType, final int creditScore) {
        scenarioContext.setCurrentRequest(scenarioContext.getCurrentRequest().toBuilder()
            .coverageType(coverageType)
            .creditScore(creditScore)
            .build());
    }

    /**
     * Sets prior insurance boolean in current request.
     */
    @And("prior insurance is {word}")
    public void setPriorInsurance(final String priorInsurance) {
        scenarioContext.setCurrentRequest(scenarioContext.getCurrentRequest().toBuilder()
            .priorInsurance(Boolean.parseBoolean(priorInsurance))
            .build());
    }

    /**
     * Sets multi-car boolean in current request.
     */
    @And("multi car is {word}")
    public void setMultiCar(final String multiCar) {
        scenarioContext.setCurrentRequest(scenarioContext.getCurrentRequest().toBuilder()
            .multiCar(Boolean.parseBoolean(multiCar))
            .build());
    }

    /**
     * Calls the quote API and stores response metadata.
     */
    @When("the Get Quote API is called")
    public void callGetQuoteApi() {
        final QuoteResponse response = quoteApiClient.getQuote(scenarioContext.getCurrentRequest());
        scenarioContext.setLastQuoteResponse(response);
        scenarioContext.setLastHttpStatus(quoteApiClient.getLastHttpStatusCode());
        scenarioContext.setLastResponseTimeMs(quoteApiClient.getLastResponseTime());
        scenarioContext.setRawLastResponse(quoteApiClient.getLastRawResponse());
    }

    /**
     * Verifies the latest HTTP status code.
     */
    @Then("the response status is {int}")
    public void verifyHttpStatus(final int expectedStatus) {
        assertThat(scenarioContext.getLastHttpStatus()).isEqualTo(expectedStatus);
    }

    /**
     * Verifies quote business status.
     */
    @And("the quote status is {string}")
    public void verifyQuoteStatus(final String expectedStatus) {
        assertThat(scenarioContext.getLastQuoteResponse().getStatus()).isEqualTo(expectedStatus);
        scenarioContext.setLastExpectedStatement("quote status is " + expectedStatus);
    }

    /**
     * Verifies monthly premium range.
     */
    @And("the monthly premium is between {int} and {int}")
    public void verifyPremiumRange(final int minPremium, final int maxPremium) {
        final BigDecimal monthly = scenarioContext.getLastQuoteResponse().getPremium().getMonthly();
        assertThat(monthly).isBetween(BigDecimal.valueOf(minPremium), BigDecimal.valueOf(maxPremium));
        scenarioContext.setLastExpectedStatement("monthly premium between " + minPremium + " and " + maxPremium);
    }

    /**
     * Verifies prior-insurance discount percentage against baseline amount.
     */
    @Then("the prior insurance discount reduces monthly premium by 5 percent")
    public void verifyPriorInsuranceDiscount() {
        final QuoteResponse response = scenarioContext.getLastQuoteResponse();
        assertThat(response.getPremium()).isNotNull();
        assertThat(response.getDiscounts()).isNotNull();
        assertThat(response.getDiscounts()).anyMatch(value -> value.contains("priorInsurance:-5%"));
        scenarioContext.setLastExpectedStatement("prior insurance discount reduces monthly premium by 5 percent");
    }

    /**
     * Verifies stacked discounts are present.
     */
    @Then("stacked discounts are applied for prior insurance and multi car")
    public void verifyStackedDiscounts() {
        final QuoteResponse response = scenarioContext.getLastQuoteResponse();
        assertThat(response.getDiscounts()).isNotNull();
        assertThat(response.getDiscounts()).anyMatch(value -> value.contains("priorInsurance:-5%"));
        assertThat(response.getDiscounts()).anyMatch(value -> value.contains("multiCar:-8%"));
        scenarioContext.setLastExpectedStatement("stacked discounts applied");
    }
}
