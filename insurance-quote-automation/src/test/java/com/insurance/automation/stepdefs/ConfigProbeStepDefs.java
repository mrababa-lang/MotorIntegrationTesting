package com.insurance.automation.stepdefs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.automation.api.BaseApiClient;
import com.insurance.automation.api.ShoryEndpoints;
import com.insurance.automation.builders.QuoteRequestBuilder;
import com.insurance.automation.config.ConfigManager;
import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.report.InsuranceQuoteReportGenerator;
import com.insurance.automation.runners.TestRunner;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.response.Response;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Step definitions for configuration validation using the two-probe pattern.
 *
 * <p>Each configuration scenario runs two API probes:
 * <ol>
 *   <li>PROBE — a valid, standard request that should succeed.</li>
 *   <li>CONSTRAINT — the same request with one deliberate variation that exercises the rule.</li>
 * </ol>
 *
 * <p>Outcome mapping:
 * <ul>
 *   <li>Probe FAILS  → {@code DATA_ISSUE} — can't evaluate, record {@code enabled = null}.</li>
 *   <li>Probe PASSES + Constraint FAILS  → {@code ENABLED}  — restriction is active.</li>
 *   <li>Probe PASSES + Constraint PASSES → {@code DISABLED} — restriction is not enforced.</li>
 * </ul>
 *
 * <p>In-scope rules (all others are explicitly out of scope):
 * <ul>
 *   <li>SR-001 — TPL to Comprehensive upgrade restriction</li>
 *   <li>SR-002 — Expired prior insurance to Comprehensive upgrade restriction</li>
 *   <li>SR-003 — Non-GCC vehicle allowance</li>
 *   <li>CFG-001 — Maximum driver age limit</li>
 *   <li>CFG-002 — Minimum driver age limit</li>
 *   <li>CFG-003 — Maximum vehicle value limit</li>
 *   <li>CFG-004 — Minimum vehicle value limit</li>
 * </ul>
 */
public class ConfigProbeStepDefs extends BaseApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigProbeStepDefs.class);

    private final ScenarioContext context;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Result of the probe (valid baseline) request. */
    private boolean probeSuccess = false;
    /** Result of the constraint (modified) request. */
    private boolean constraintSuccess = false;
    /** Raw JSON of the probe response — stored for the report "Actual" column. */
    private String probeResponseJson;
    /** Raw JSON of the constraint response. */
    private String constraintResponseJson;

    public ConfigProbeStepDefs(final ScenarioContext context) {
        this.context = context;
    }

    // ── Probe step ────────────────────────────────────────────────────────────

    @When("I submit a probe quote using standard contextual IDs")
    public void submitProbeQuote() {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        captureRequestBody(body);
        context.appendStep("PROBE: POST /quote/request with vehicleId=" + context.getVehicleId()
            + ", customerId=" + context.getCustomerId());

        final Response quoteResp = buildSpec().body(body).post(ShoryEndpoints.QUOTE_REQUEST);
        final boolean quoteOk = quoteResp.statusCode() == 200
            && Boolean.TRUE.equals(quoteResp.jsonPath().getBoolean("isSuccess"));

        if (!quoteOk) {
            probeSuccess = false;
            probeResponseJson = quoteResp.asString();
            LOG.warn("Probe quote request failed (status={}, isSuccess=false)", quoteResp.statusCode());
            setLastResponse(quoteResp);
            return;
        }

        final String qrId = quoteResp.jsonPath().getString("details.quoteRequestId");
        context.setQuoteRequestId(qrId);

        final Response offersResp = pollForOffers(qrId);
        probeResponseJson = offersResp.asString();
        probeSuccess = offersResp.statusCode() == 200
            && Boolean.TRUE.equals(offersResp.jsonPath().getBoolean("isSuccess"));

        context.appendStep("PROBE result: " + (probeSuccess ? "SUCCESS (offers returned)" : "FAILED (no offers)"));
        setLastResponse(offersResp);
    }

    // ── Skip rule constraint steps ─────────────────────────────────────────────

    /**
     * SR-001 — TPL to Comprehensive upgrade restriction.
     * Sends a quote request with insuranceTypeId=1 (TPL).
     * If the system rejects it, the upgrade restriction rule is ENABLED.
     */
    @And("I submit a constraint quote requesting TPL insurance type")
    public void submitConstraintRequestingTplInsuranceType() {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        body.put("insuranceTypeId", 1);
        captureRequestBody(body);
        context.appendStep("CONSTRAINT: POST /quote/request with insuranceTypeId=1 (TPL)");

        executeConstraintQuote(body);
    }

    /**
     * SR-002 — Expired prior insurance to Comprehensive upgrade restriction.
     * Flags the request as originating from an expired prior policy.
     * If the system rejects it, the restriction is ENABLED.
     */
    @And("I submit a constraint quote with expired prior insurance")
    public void submitConstraintWithExpiredPriorInsurance() {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        body.put("priorInsuranceExpired", true);
        captureRequestBody(body);
        context.appendStep("CONSTRAINT: POST /quote/request with priorInsuranceExpired=true");

        executeConstraintQuote(body);
    }

    /**
     * SR-003 — Non-GCC vehicle allowance.
     * Flags the vehicle origin as non-GCC.
     * If the system rejects it, non-GCC vehicles are NOT allowed (rule is ENABLED as a restriction).
     */
    @And("I submit a constraint quote for a non-GCC vehicle")
    public void submitConstraintForNonGccVehicle() {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        body.put("vehicleOrigin", "NON_GCC");
        captureRequestBody(body);
        context.appendStep("CONSTRAINT: POST /quote/request with vehicleOrigin=NON_GCC");

        executeConstraintQuote(body);
    }

    // ── Configuration parameter constraint steps ───────────────────────────────

    /**
     * CFG-001 / CFG-002 — Driver age limits.
     * Sends a quote with the specified driver age.
     * Age 85 tests the maximum cap; age 17 tests the minimum floor.
     */
    @And("I submit a constraint quote with driver age {int}")
    public void submitConstraintWithDriverAge(final int age) {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        body.put("driverAge", age);
        captureRequestBody(body);
        context.appendStep("CONSTRAINT: POST /quote/request with driverAge=" + age);

        executeConstraintQuote(body);
    }

    /**
     * CFG-003 / CFG-004 — Vehicle value limits.
     * Sends a quote with the specified vehicle value (AED).
     * Value 5,000,000 tests the maximum cap; value 1,000 tests the minimum floor.
     */
    @And("I submit a constraint quote with vehicle value {long}")
    public void submitConstraintWithVehicleValue(final long value) {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        body.put("vehicleValue", value);
        captureRequestBody(body);
        context.appendStep("CONSTRAINT: POST /quote/request with vehicleValue=" + value);

        executeConstraintQuote(body);
    }

    // ── Evaluation step ───────────────────────────────────────────────────────

    @Then("the rule {string} in category {string} is evaluated and recorded")
    public void evaluateAndRecord(final String label, final String category) {
        final String outcome;
        final Boolean enabled;

        if (!probeSuccess) {
            outcome = "DATA_ISSUE";
            enabled = null;
            context.appendStep("OUTCOME: DATA_ISSUE — probe failed, cannot evaluate rule");
        } else if (!constraintSuccess) {
            outcome = "ENABLED";
            enabled = Boolean.TRUE;
            context.appendStep("OUTCOME: ENABLED — probe passed, constraint was rejected (rule is active)");
        } else {
            outcome = "DISABLED";
            enabled = Boolean.FALSE;
            context.appendStep("OUTCOME: DISABLED — both probe and constraint passed (rule is not enforced)");
        }

        LOG.info("Rule evaluation: label='{}' category='{}' outcome={}", label, category, outcome);

        // Record config values for the test report row detail panel
        context.addCapturedConfigValue("probeResult",      probeSuccess      ? "PASS" : "FAIL");
        context.addCapturedConfigValue("constraintResult", constraintSuccess ? "PASS" : "FAIL");
        context.addCapturedConfigValue("ruleOutcome",      outcome);

        // Add to the Configurations section of the report
        TestRunner.getReporter().addConfiguration(
            InsuranceQuoteReportGenerator.ConfigurationResult.builder()
                .category(category)
                .key(label.toLowerCase().replace(" ", "_"))
                .label(label)
                .enabled(enabled)
                .value(outcome)
                .lastUpdated(java.time.LocalDateTime.now().toString())
                .build());
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /**
     * Sends the constraint body to /quote/request and then polls /quote/offers.
     * Sets {@link #constraintSuccess} and {@link #constraintResponseJson} as a side-effect.
     */
    private void executeConstraintQuote(final Map<String, Object> body) {
        final Response quoteResp = buildSpec().body(body).post(ShoryEndpoints.QUOTE_REQUEST);
        if (quoteResp.statusCode() != 200
                || !Boolean.TRUE.equals(quoteResp.jsonPath().getBoolean("isSuccess"))) {
            constraintSuccess = false;
            constraintResponseJson = quoteResp.asString();
            context.appendStep("CONSTRAINT result: FAILED (quote request rejected)");
            return;
        }

        final String qrId = quoteResp.jsonPath().getString("details.quoteRequestId");
        final Response offersResp = pollForOffers(qrId);
        constraintResponseJson = offersResp.asString();
        constraintSuccess = offersResp.statusCode() == 200
            && Boolean.TRUE.equals(offersResp.jsonPath().getBoolean("isSuccess"));

        context.appendStep("CONSTRAINT result: " + (constraintSuccess ? "SUCCESS (offers returned)" : "FAILED (no offers)"));
    }

    /**
     * Polls {@code /quote/offers} until {@code isReady=true} or the attempt limit is reached.
     */
    private Response pollForOffers(final String quoteRequestId) {
        final int maxAttempts = ConfigManager.getConfig().pollingMaxAttempts();
        final long intervalMs  = ConfigManager.getConfig().pollingIntervalMs();
        Response latest = buildSpec()
            .queryParam("quoteRequestId", quoteRequestId)
            .get(ShoryEndpoints.QUOTE_OFFERS);

        for (int i = 1; i < maxAttempts; i++) {
            if (Boolean.TRUE.equals(latest.jsonPath().getBoolean("isReady"))) {
                break;
            }
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            latest = buildSpec()
                .queryParam("quoteRequestId", quoteRequestId)
                .get(ShoryEndpoints.QUOTE_OFFERS);
        }
        return latest;
    }

    private void captureRequestBody(final Object body) {
        try {
            context.setLastRequestBodyJson(
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
        } catch (JsonProcessingException e) {
            context.setLastRequestBodyJson("{}");
        }
    }

    private void setLastResponse(final Response response) {
        context.setLastApiResponse(response);
        context.setLastHttpStatus(response.statusCode());
        context.setLastResponseTimeMs(response.time());
        context.setRawLastResponse(response.asString());
    }
}
