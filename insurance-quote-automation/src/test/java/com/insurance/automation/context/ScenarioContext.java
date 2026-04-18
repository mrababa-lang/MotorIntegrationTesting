package com.insurance.automation.context;

import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Scenario-scoped context shared across step definitions.
 */
public class ScenarioContext {

    // ── API / HTTP state ─────────────────────────────────────────────────────
    private Response lastApiResponse;
    private String rawLastResponse;
    private long lastResponseTimeMs;
    private int lastHttpStatus;

    // ── Request capture ──────────────────────────────────────────────────────
    /** Raw JSON body of the last outgoing POST request (for report "Input" column). */
    private String lastRequestBodyJson;

    // ── Quote flow IDs ───────────────────────────────────────────────────────
    private String vehicleId;
    private String customerId;
    private String customerLicenseId = null;
    private String quoteRequestId;
    private List<String> offerIds = new ArrayList<>();

    // ── Report helpers ───────────────────────────────────────────────────────
    private String currentScenarioName;
    private String currentScenarioId;

    /**
     * Human-readable assertion lines accumulated during a scenario.
     * Used as the "Expected" column in the test report.
     */
    private List<String> stepLog = new ArrayList<>();

    /**
     * Key-value pairs extracted from responses during a scenario.
     * Used as the "Config Values" column in the test report.
     */
    private Map<String, String> capturedConfigValues = new LinkedHashMap<>();

    // ── Legacy fields kept for back-compat with TestHooks ───────────────────
    private String lastExpectedStatement;
    private String lastConfigError;

    // ── Getters / setters ────────────────────────────────────────────────────

    public Response getLastApiResponse() { return lastApiResponse; }
    public void setLastApiResponse(final Response r) { this.lastApiResponse = r; }

    public String getRawLastResponse() { return rawLastResponse; }
    public void setRawLastResponse(final String s) { this.rawLastResponse = s; }

    public long getLastResponseTimeMs() { return lastResponseTimeMs; }
    public void setLastResponseTimeMs(final long ms) { this.lastResponseTimeMs = ms; }

    public int getLastHttpStatus() { return lastHttpStatus; }
    public void setLastHttpStatus(final int s) { this.lastHttpStatus = s; }

    public String getLastRequestBodyJson() { return lastRequestBodyJson; }
    public void setLastRequestBodyJson(final String json) { this.lastRequestBodyJson = json; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(final String vehicleId) { this.vehicleId = vehicleId; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(final String customerId) { this.customerId = customerId; }

    public Optional<String> getCustomerLicenseId() { return Optional.ofNullable(customerLicenseId); }
    public void setCustomerLicenseId(final String customerLicenseId) { this.customerLicenseId = customerLicenseId; }

    public String getQuoteRequestId() { return quoteRequestId; }
    public void setQuoteRequestId(final String quoteRequestId) { this.quoteRequestId = quoteRequestId; }

    public List<String> getOfferIds() { return offerIds; }
    public void setOfferIds(final List<String> offerIds) { this.offerIds = offerIds; }

    public String getCurrentScenarioName() { return currentScenarioName; }
    public void setCurrentScenarioName(final String n) { this.currentScenarioName = n; }

    public String getCurrentScenarioId() { return currentScenarioId; }
    public void setCurrentScenarioId(final String id) { this.currentScenarioId = id; }

    public String getLastExpectedStatement() { return lastExpectedStatement; }
    public void setLastExpectedStatement(final String s) { this.lastExpectedStatement = s; }

    public String getLastConfigError() { return lastConfigError; }
    public void setLastConfigError(final String e) { this.lastConfigError = e; }

    // ── Step log (Expected column) ───────────────────────────────────────────

    public List<String> getStepLog() { return stepLog; }

    /** Appends one human-readable assertion line to the step log. */
    public void appendStep(final String line) { this.stepLog.add(line); }

    public void clearStepLog() { this.stepLog = new ArrayList<>(); }

    // ── Captured config values (Config Values column) ────────────────────────

    public Map<String, String> getCapturedConfigValues() { return capturedConfigValues; }

    /** Records a key-value pair observed from an API response during this scenario. */
    public void addCapturedConfigValue(final String key, final String value) {
        this.capturedConfigValues.put(key, value);
    }

    public void clearCapturedConfigValues() { this.capturedConfigValues = new LinkedHashMap<>(); }

    // ── Kept for TestHooks back-compat (no longer used for serialization) ────
    /** @deprecated Use lastRequestBodyJson instead. */
    @Deprecated
    public com.insurance.automation.models.request.QuoteRequest getCurrentRequest() { return null; }
    /** @deprecated Use setLastRequestBodyJson instead. */
    @Deprecated
    public void setCurrentRequest(final com.insurance.automation.models.request.QuoteRequest r) { /* no-op */ }

    /** @deprecated Config response tracking replaced by capturedConfigValues. */
    @Deprecated
    public com.insurance.automation.models.response.ConfigStatusResponse getLastConfigResponse() { return null; }
    /** @deprecated */
    @Deprecated
    public void setLastConfigResponse(final com.insurance.automation.models.response.ConfigStatusResponse r) { /* no-op */ }

    /** @deprecated Config response tracking replaced by capturedConfigValues. */
    @Deprecated
    public com.insurance.automation.models.response.QuoteResponse getLastQuoteResponse() { return null; }
    /** @deprecated */
    @Deprecated
    public void setLastQuoteResponse(final com.insurance.automation.models.response.QuoteResponse r) { /* no-op */ }
}
