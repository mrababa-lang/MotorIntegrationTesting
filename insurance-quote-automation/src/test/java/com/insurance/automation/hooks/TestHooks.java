package com.insurance.automation.hooks;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.models.request.QuoteRequest;
import com.insurance.automation.models.response.QuoteResponse;
import com.insurance.automation.report.InsuranceQuoteReportGenerator;
import com.insurance.automation.runners.TestRunner;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks for scenario setup and report aggregation.
 */
public class TestHooks {

    private static final Logger LOG = LoggerFactory.getLogger(TestHooks.class);
    private final ScenarioContext scenarioContext;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates hook class with scenario context injection.
     *
     * @param scenarioContext shared scenario context.
     */
    public TestHooks(final ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    /**
     * Runs before each scenario.
     *
     * @param scenario current cucumber scenario.
     */
    @Before
    public void beforeScenario(final Scenario scenario) {
        scenarioContext.setCurrentScenarioName(scenario.getName());
        scenarioContext.setCurrentScenarioId(extractScenarioId(scenario));
        scenarioContext.setCurrentRequest(null);
        scenarioContext.setLastQuoteResponse(null);
        scenarioContext.setLastConfigResponse(null);
        scenarioContext.setLastResponseTimeMs(0L);
        scenarioContext.setLastHttpStatus(0);
        scenarioContext.setLastExpectedStatement(null);
        scenarioContext.setRawLastResponse(null);
        LOG.info("Starting scenario '{}' on env '{}'", scenario.getName(), System.getProperty("env", "uat"));
    }

    /**
     * Runs after each scenario and appends a report result.
     *
     * @param scenario completed cucumber scenario.
     */
    @After
    public void afterScenario(final Scenario scenario) {
        final InsuranceQuoteReportGenerator reporter = TestRunner.getReporter();
        final QuoteResponse quoteResponse = scenarioContext.getLastQuoteResponse();

        String mappedStatus;
        Boolean match;
        String error = null;

        if (scenario.getStatus() == Status.PASSED && quoteResponse != null && "skipped".equalsIgnoreCase(quoteResponse.getStatus())) {
            mappedStatus = "SKIP";
            match = Boolean.TRUE;
        } else if (scenario.getStatus() == Status.PASSED) {
            mappedStatus = "PASS";
            match = Boolean.TRUE;
        } else if (scenario.getStatus() == Status.FAILED) {
            mappedStatus = "FAIL";
            match = Boolean.FALSE;
            error = scenario.isFailed() && scenario.getError() != null ? toStackTrace(scenario.getError()) : "Scenario failed";
        } else {
            mappedStatus = "PENDING";
            match = null;
        }

        reporter.addResult(InsuranceQuoteReportGenerator.TestResult.builder()
            .id(Optional.ofNullable(scenarioContext.getCurrentScenarioId()).orElse("TC-NA"))
            .name(scenario.getName())
            .description("Feature scenario: " + scenario.getName())
            .category(resolveCategory(scenario))
            .status(mappedStatus)
            .duration(scenarioContext.getLastResponseTimeMs())
            .input(serializeInput(scenarioContext.getCurrentRequest()))
            .expected(Optional.ofNullable(scenarioContext.getLastExpectedStatement()).orElse("See scenario steps"))
            .actual(resolveActual())
            .match(match)
            .skipRules(resolveSkipRules(quoteResponse))
            .configValues(resolveConfigValues(quoteResponse))
            .error(error)
            .build());
    }

    private String extractScenarioId(final Scenario scenario) {
        return scenario.getSourceTagNames().stream()
            .filter(tag -> tag.startsWith("@TC-"))
            .map(tag -> tag.substring(1))
            .findFirst()
            .orElse("TC-NA");
    }

    private String resolveCategory(final Scenario scenario) {
        final String lowered = scenario.getName().toLowerCase();
        if (lowered.contains("skip")) {
            return "skip-rule";
        }
        if (lowered.contains("configuration") || lowered.contains("config")) {
            return "configuration";
        }
        return "quote";
    }

    private String serializeInput(final QuoteRequest request) {
        if (request == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException exception) {
            return "{}";
        }
    }

    private String resolveActual() {
        if (scenarioContext.getLastQuoteResponse() != null) {
            final QuoteResponse response = scenarioContext.getLastQuoteResponse();
            if (response.getPremium() != null && response.getPremium().getMonthly() != null) {
                return "monthly=" + response.getPremium().getMonthly();
            }
            return Optional.ofNullable(response.getMessage()).orElse(scenarioContext.getRawLastResponse());
        }
        if (scenarioContext.getLastConfigResponse() != null) {
            return "configValue=" + scenarioContext.getLastConfigResponse().getValue();
        }
        return scenarioContext.getRawLastResponse();
    }

    private List<InsuranceQuoteReportGenerator.SkipRule> resolveSkipRules(final QuoteResponse response) {
        if (response == null) {
            return Collections.emptyList();
        }
        final List<String> codes = new ArrayList<>();
        if (response.getSkipReason() != null) {
            codes.add(response.getSkipReason());
        }
        if (response.getSkipReasons() != null) {
            codes.addAll(response.getSkipReasons());
        }
        final List<InsuranceQuoteReportGenerator.SkipRule> skipRules = new ArrayList<>();
        for (final String code : codes) {
            skipRules.add(InsuranceQuoteReportGenerator.SkipRule.builder()
                .code(code)
                .message("Rule triggered: " + code)
                .build());
        }
        return skipRules;
    }

    private List<InsuranceQuoteReportGenerator.ConfigValue> resolveConfigValues(final QuoteResponse response) {
        if (response == null) {
            return Collections.emptyList();
        }
        final List<InsuranceQuoteReportGenerator.ConfigValue> configValues = new ArrayList<>();
        if (response.getRatingFactor() != null) {
            configValues.add(InsuranceQuoteReportGenerator.ConfigValue.builder().key("ratingFactor").value(response.getRatingFactor()).build());
        }
        if (response.getBaseRate() != null) {
            configValues.add(InsuranceQuoteReportGenerator.ConfigValue.builder().key("baseRate").value(response.getBaseRate().toPlainString()).build());
        }
        if (response.getDiscounts() != null) {
            configValues.add(InsuranceQuoteReportGenerator.ConfigValue.builder().key("discounts").value(String.join(",", response.getDiscounts())).build());
        }
        return configValues;
    }

    private String toStackTrace(final Throwable throwable) {
        final StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
