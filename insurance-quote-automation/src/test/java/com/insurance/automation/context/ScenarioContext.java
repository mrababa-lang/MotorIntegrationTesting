package com.insurance.automation.context;

import com.insurance.automation.models.request.QuoteRequest;
import com.insurance.automation.models.response.ConfigStatusResponse;
import com.insurance.automation.models.response.QuoteResponse;
import lombok.Data;

/**
 * Scenario-scoped context shared across step definitions.
 */
@Data
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
}
