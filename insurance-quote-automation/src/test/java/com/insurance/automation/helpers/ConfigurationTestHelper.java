package com.insurance.automation.helpers;

import com.insurance.automation.api.QuoteApiClient;
import com.insurance.automation.context.ScenarioContext;
import io.restassured.response.Response;

/**
 * Executes the two-quote probe pattern for configuration eligibility validation.
 */
public class ConfigurationTestHelper {

    /**
     * Result states for a configuration eligibility evaluation.
     */
    public enum ConfigResult {
        ENABLED,
        DISABLED,
        DATA_ISSUE
    }

    private final QuoteApiClient quoteClient;
    private final ScenarioContext context;

    public ConfigurationTestHelper(final QuoteApiClient quoteClient, final ScenarioContext context) {
        this.quoteClient = quoteClient;
        this.context = context;
    }

    /**
     * Runs the two-quote probe pattern.
     *
     * @param probeRequest request expected to succeed when test data is valid.
     * @param constraintRequest request with constrained input that validates rule enforcement.
     * @return eligibility result.
     */
    public ConfigResult evaluate(final Object probeRequest, final Object constraintRequest) {
        final Response probeResponse = quoteClient.requestQuote(probeRequest);
        final boolean probeSuccess = Boolean.TRUE.equals(probeResponse.jsonPath().getBoolean("isSuccess"));

        if (!probeSuccess) {
            context.setLastConfigResult(ConfigResult.DATA_ISSUE);
            context.setLastConfigError("Probe quote failed: " + probeResponse.asString());
            return ConfigResult.DATA_ISSUE;
        }

        final Response constraintResponse = quoteClient.requestQuote(constraintRequest);
        final boolean constraintSuccess = Boolean.TRUE.equals(constraintResponse.jsonPath().getBoolean("isSuccess"));

        if (!constraintSuccess) {
            context.setLastConfigResult(ConfigResult.ENABLED);
            context.setLastConfigError(null);
            return ConfigResult.ENABLED;
        }

        context.setLastConfigResult(ConfigResult.DISABLED);
        context.setLastConfigError(null);
        return ConfigResult.DISABLED;
    }
}
