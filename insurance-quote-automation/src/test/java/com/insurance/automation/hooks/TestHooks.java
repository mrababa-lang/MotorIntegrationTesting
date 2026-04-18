package com.insurance.automation.hooks;

import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.report.InsuranceQuoteReportGenerator;
import com.insurance.automation.runners.TestRunner;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.Status;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cucumber lifecycle hooks for scenario setup and report aggregation.
 */
public class TestHooks {

    private static final Logger LOG = LoggerFactory.getLogger(TestHooks.class);
    private final ScenarioContext scenarioContext;

    /**
     * Creates hook class with scenario context injection.
     *
     * @param scenarioContext shared scenario context.
     */
    public TestHooks(final ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    /**
     * Runs before each scenario — resets all per-scenario state.
     *
     * @param scenario current cucumber scenario.
     */
    @Before
    public void beforeScenario(final Scenario scenario) {
        scenarioContext.setCurrentScenarioName(scenario.getName());
        scenarioContext.setCurrentScenarioId(extractScenarioId(scenario));

        // Reset HTTP state
        scenarioContext.setLastResponseTimeMs(0L);
        scenarioContext.setLastHttpStatus(0);
        scenarioContext.setRawLastResponse(null);
        scenarioContext.setLastApiResponse(null);

        // Reset request capture
        scenarioContext.setLastRequestBodyJson(null);

        // Reset step / config capture
        scenarioContext.clearStepLog();
        scenarioContext.clearCapturedConfigValues();

        // Reset flow IDs
        scenarioContext.setVehicleId(null);
        scenarioContext.setCustomerId(null);
        scenarioContext.setCustomerLicenseId(null);
        scenarioContext.setQuoteRequestId(null);
        scenarioContext.setOfferIds(new ArrayList<>());

        LOG.info("Starting scenario '{}' on env '{}'", scenario.getName(), System.getProperty("env", "uat"));
    }

    /**
     * Runs after each scenario and appends a result to the HTML report.
     *
     * @param scenario completed cucumber scenario.
     */
    @After
    public void afterScenario(final Scenario scenario) {
        final InsuranceQuoteReportGenerator reporter = TestRunner.getReporter();

        // ── Map Cucumber status to report status ──────────────────────────
        final String mappedStatus;
        final Boolean match;
        String error = null;

        if (scenario.getStatus() == Status.PASSED) {
            mappedStatus = "PASS";
            match = Boolean.TRUE;
        } else if (scenario.getStatus() == Status.FAILED) {
            mappedStatus = "FAIL";
            match = Boolean.FALSE;
            error = "Scenario failed — see actual response for details";
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
            .input(scenarioContext.getLastRequestBodyJson())
            .expected(resolveExpected())
            .actual(scenarioContext.getRawLastResponse())
            .match(match)
            .skipRules(new ArrayList<>())
            .configValues(resolveConfigValues())
            .error(error)
            .build());
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String extractScenarioId(final Scenario scenario) {
        return scenario.getSourceTagNames().stream()
            .filter(tag -> tag.startsWith("@TC-"))
            .map(tag -> tag.substring(1))
            .findFirst()
            .orElse("TC-NA");
    }

    private String resolveCategory(final Scenario scenario) {
        if (scenario.getSourceTagNames().contains("@config-test")) {
            return "configuration";
        }
        final String name = scenario.getName().toLowerCase();
        if (name.contains("skip")) return "skip-rule";
        if (name.contains("config")) return "configuration";
        return "quote";
    }

    /**
     * Builds the "Expected" text from the step log accumulated during the scenario.
     * If no steps were logged the scenario name is used as a fallback.
     */
    private String resolveExpected() {
        final List<String> steps = scenarioContext.getStepLog();
        if (steps == null || steps.isEmpty()) {
            return scenarioContext.getCurrentScenarioName() != null
                ? scenarioContext.getCurrentScenarioName()
                : "See scenario steps";
        }
        return String.join("\n", steps);
    }

    /**
     * Converts the key-value pairs captured during the scenario into the report model.
     */
    private List<InsuranceQuoteReportGenerator.ConfigValue> resolveConfigValues() {
        final Map<String, String> map = scenarioContext.getCapturedConfigValues();
        if (map == null || map.isEmpty()) {
            return new ArrayList<>();
        }
        final List<InsuranceQuoteReportGenerator.ConfigValue> list = new ArrayList<>();
        for (final Map.Entry<String, String> entry : map.entrySet()) {
            list.add(InsuranceQuoteReportGenerator.ConfigValue.builder()
                .key(entry.getKey())
                .value(entry.getValue())
                .build());
        }
        return list;
    }
}
