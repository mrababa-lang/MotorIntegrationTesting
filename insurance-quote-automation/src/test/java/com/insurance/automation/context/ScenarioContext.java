package com.insurance.automation.context;

import com.insurance.automation.helpers.ConfigurationTestHelper;
import com.insurance.automation.models.request.QuoteRequest;
import com.insurance.automation.models.response.ConfigStatusResponse;
import com.insurance.automation.models.response.QuoteResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Scenario-scoped context shared across step definitions.
 */
public class ScenarioContext {

    private QuoteRequest currentRequest;
    private QuoteResponse lastQuoteResponse;
    private ConfigStatusResponse lastConfigResponse;
    private long lastResponseTimeMs;
    private int lastHttpStatus;
    private String currentScenarioName;
    private String currentScenarioId;
    private String lastExpectedStatement;
    private String rawLastResponse;
    private ConfigurationTestHelper.ConfigResult lastConfigResult;
    private String lastConfigError;

    private Response lastApiResponse;
    private String vehicleId;
    private String customerId;
    private String customerLicenseId = null;
    private String quoteRequestId;
    private List<String> offerIds = new ArrayList<>();

    public QuoteRequest getCurrentRequest() {
        return currentRequest;
    }

    public void setCurrentRequest(final QuoteRequest currentRequest) {
        this.currentRequest = currentRequest;
    }

    public QuoteResponse getLastQuoteResponse() {
        return lastQuoteResponse;
    }

    public void setLastQuoteResponse(final QuoteResponse lastQuoteResponse) {
        this.lastQuoteResponse = lastQuoteResponse;
    }

    public ConfigStatusResponse getLastConfigResponse() {
        return lastConfigResponse;
    }

    public void setLastConfigResponse(final ConfigStatusResponse lastConfigResponse) {
        this.lastConfigResponse = lastConfigResponse;
    }

    public long getLastResponseTimeMs() {
        return lastResponseTimeMs;
    }

    public void setLastResponseTimeMs(final long lastResponseTimeMs) {
        this.lastResponseTimeMs = lastResponseTimeMs;
    }

    public int getLastHttpStatus() {
        return lastHttpStatus;
    }

    public void setLastHttpStatus(final int lastHttpStatus) {
        this.lastHttpStatus = lastHttpStatus;
    }

    public String getCurrentScenarioName() {
        return currentScenarioName;
    }

    public void setCurrentScenarioName(final String currentScenarioName) {
        this.currentScenarioName = currentScenarioName;
    }

    public String getCurrentScenarioId() {
        return currentScenarioId;
    }

    public void setCurrentScenarioId(final String currentScenarioId) {
        this.currentScenarioId = currentScenarioId;
    }

    public String getLastExpectedStatement() {
        return lastExpectedStatement;
    }

    public void setLastExpectedStatement(final String lastExpectedStatement) {
        this.lastExpectedStatement = lastExpectedStatement;
    }

    public String getRawLastResponse() {
        return rawLastResponse;
    }

    public void setRawLastResponse(final String rawLastResponse) {
        this.rawLastResponse = rawLastResponse;
    }

    public ConfigurationTestHelper.ConfigResult getLastConfigResult() {
        return lastConfigResult;
    }

    public void setLastConfigResult(final ConfigurationTestHelper.ConfigResult lastConfigResult) {
        this.lastConfigResult = lastConfigResult;
    }

    public String getLastConfigError() {
        return lastConfigError;
    }

    public void setLastConfigError(final String lastConfigError) {
        this.lastConfigError = lastConfigError;
    }

    public Response getLastApiResponse() {
        return lastApiResponse;
    }

    public void setLastApiResponse(final Response lastApiResponse) {
        this.lastApiResponse = lastApiResponse;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(final String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final String customerId) {
        this.customerId = customerId;
    }

    public Optional<String> getCustomerLicenseId() {
        return Optional.ofNullable(customerLicenseId);
    }

    public void setCustomerLicenseId(final String customerLicenseId) {
        this.customerLicenseId = customerLicenseId;
    }

    public String getQuoteRequestId() {
        return quoteRequestId;
    }

    public void setQuoteRequestId(final String quoteRequestId) {
        this.quoteRequestId = quoteRequestId;
    }

    public List<String> getOfferIds() {
        return offerIds;
    }

    public void setOfferIds(final List<String> offerIds) {
        this.offerIds = offerIds;
    }
}
