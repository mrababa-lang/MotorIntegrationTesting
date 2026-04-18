package com.insurance.automation.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.insurance.automation.api.BaseApiClient;
import com.insurance.automation.api.ShoryEndpoints;
import com.insurance.automation.builders.QuoteRequestBuilder;
import com.insurance.automation.config.ConfigManager;
import com.insurance.automation.config.EnvironmentConfig;
import com.insurance.automation.context.ScenarioContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Step definitions for Shory motor full flow endpoints.
 */
public class MotorFlowStepDefs extends BaseApiClient {

    private final ScenarioContext context;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MotorFlowStepDefs(final ScenarioContext context) {
        this.context = context;
    }

    // ── POST steps ───────────────────────────────────────────────────────────

    @When("I POST to {string} with body:")
    public void postWithBodyTable(final String path, final DataTable dataTable) {
        final Map<String, String> values = dataTable.asMap(String.class, String.class);
        final Map<String, Object> payload = toNestedPayload(values);
        captureBody(payload);
        setLastResponse(buildSpec().body(payload).post(path));
    }

    @Given("I POST to {string} with payload:")
    public void givenPostWithPayload(final String path, final String payload) {
        context.setLastRequestBodyJson(payload.trim());
        setLastResponse(buildSpec().body(payload).post(path));
    }

    @When("I POST to {string} with contextual IDs:")
    public void postWithContextualIds(final String path, final DataTable table) {
        final Map<String, Object> body = new LinkedHashMap<>();
        for (final List<String> row : table.cells()) {
            final String key = row.get(0);
            final String value = row.get(1);
            if (value.startsWith("{context:") && value.endsWith("}")) {
                final String contextKey = value.substring(9, value.length() - 1);
                final Object resolved = resolveContextValue(contextKey);
                if (resolved != null) {
                    body.put(key, resolved);
                }
            }
        }
        captureBody(body);
        setLastResponse(buildSpec().body(body).post(path));
    }

    @When("I POST to {string} omitting the customerLicenseId field")
    public void postQuoteRequestWithoutLicense(final String path) {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        captureBody(body);
        setLastResponse(buildSpec().body(body).post(path));
    }

    // ── GET / poll steps ─────────────────────────────────────────────────────

    @When("I GET {string} with param {string} from context")
    public void getWithContextParam(final String path, final String param) {
        setLastResponse(buildSpec().queryParam(param, context.getQuoteRequestId()).get(path));
    }

    @And("I poll with interval {int}ms up to {int} attempts until {string} is true")
    public void pollUntilReady(final int intervalMs, final int attempts, final String field) throws InterruptedException {
        Response latest = context.getLastApiResponse();
        for (int i = 0; i < attempts; i++) {
            final Boolean ready = latest.jsonPath().getBoolean(field);
            if (Boolean.TRUE.equals(ready)) {
                break;
            }
            Thread.sleep(intervalMs);
            latest = buildSpec().queryParam("quoteRequestId", context.getQuoteRequestId()).get(ShoryEndpoints.QUOTE_OFFERS);
        }
        setLastResponse(latest);
    }

    // ── Assertion steps (each logs itself to the step log) ───────────────────

    @Then("the response status code should be {int}")
    public void verifyStatusCode(final int expected) {
        context.appendStep("HTTP status = " + expected);
        assertThat(context.getLastApiResponse().statusCode()).isEqualTo(expected);
    }

    @And("the response field {string} should be true")
    public void responseFieldTrue(final String jsonPath) {
        context.appendStep(jsonPath + " = true");
        assertThat(context.getLastApiResponse().jsonPath().getBoolean(jsonPath)).isTrue();
    }

    @And("the response field {string} should be null")
    public void responseFieldNull(final String jsonPath) {
        context.appendStep(jsonPath + " = null");
        assertThat((Object) context.getLastApiResponse().jsonPath().get(jsonPath)).isNull();
    }

    @And("the response field {string} should be {int}")
    public void responseFieldInt(final String jsonPath, final int value) {
        context.appendStep(jsonPath + " = " + value);
        assertThat(context.getLastApiResponse().jsonPath().getInt(jsonPath)).isEqualTo(value);
    }

    @And("the response field {string} should be {string}")
    public void responseFieldString(final String jsonPath, final String value) {
        context.appendStep(jsonPath + " = \"" + value + "\"");
        assertThat(context.getLastApiResponse().jsonPath().getString(jsonPath)).isEqualTo(value);
    }

    @And("the response field {string} should start with {string}")
    public void responseFieldStartsWith(final String jsonPath, final String prefix) {
        context.appendStep(jsonPath + " starts with \"" + prefix + "\"");
        assertThat(context.getLastApiResponse().jsonPath().getString(jsonPath)).startsWith(prefix);
    }

    // ── Context capture steps ─────────────────────────────────────────────────

    @Given("the UAT environment is configured with base URL {string}")
    public void verifyUatBaseUrl(final String expectedBaseUrl) {
        context.appendStep("Base URL = " + expectedBaseUrl);
        assertThat(config.baseUrl()).isEqualTo(expectedBaseUrl);
    }

    @And("the request header {string} is set to {string}")
    public void verifyHeader(final String header, final String expectedValue) {
        context.appendStep("Header " + header + " = " + expectedValue);
        if ("custom-lang".equalsIgnoreCase(header)) {
            assertThat(config.defaultHeaderCustomLang()).isEqualTo(expectedValue);
        }
    }

    @And("the VehicleId, CustomerId, and CustomerLicenseId are stored in context")
    public void storeAllVehicleContextValues() {
        final JsonPath jp = context.getLastApiResponse().jsonPath();
        context.setVehicleId(jp.getString("details.vehicle[0].id"));
        context.setCustomerId(jp.getString("details.customerId"));
        context.setCustomerLicenseId(jp.getString("details.customerLicenseId"));
        // Capture key response fields for the Config Values column
        captureResponseField(jp, "customerId",         "details.customerId");
        captureResponseField(jp, "customerLicenseId",  "details.customerLicenseId");
        captureResponseField(jp, "vehicleId",          "details.vehicle[0].id");
        captureResponseField(jp, "vehicleMake",        "details.vehicle[0].makeAlias");
        captureResponseField(jp, "vehicleModel",       "details.vehicle[0].model");
        captureResponseField(jp, "vehicleYear",        "details.vehicle[0].modelYear");
    }

    @And("the VehicleId and CustomerId are stored in context")
    public void storeVehicleAndCustomerId() {
        final JsonPath jp = context.getLastApiResponse().jsonPath();
        context.setVehicleId(jp.getString("details.vehicle[0].id"));
        context.setCustomerId(jp.getString("details.customerId"));
        captureResponseField(jp, "customerId",  "details.customerId");
        captureResponseField(jp, "vehicleId",   "details.vehicle[0].id");
        captureResponseField(jp, "vehicleMake", "details.vehicle[0].makeAlias");
    }

    @And("CustomerLicenseId is stored as null in context")
    public void storeNullLicenseId() {
        context.setCustomerLicenseId(null);
        context.addCapturedConfigValue("customerLicenseId", "null (omitted)");
    }

    @And("the quoteRequestId is stored in context")
    @And("QuoteRequestId is captured")
    public void storeQuoteRequestId() {
        final String qrId = context.getLastApiResponse().jsonPath().getString("details.quoteRequestId");
        context.setQuoteRequestId(qrId);
        context.addCapturedConfigValue("quoteRequestId", qrId != null ? qrId : "null");
    }

    @Given("a vehicle has been successfully retrieved and IDs are in context")
    public void ensureVehicleIdsReady() {
        if (context.getVehicleId() == null || context.getCustomerId() == null) {
            final Map<String, Object> plate = defaultPlatePayload();
            captureBody(plate);
            setLastResponse(buildSpec().body(plate).post(ShoryEndpoints.VEHICLE_RETRIEVE));
            storeAllVehicleContextValues();
        }
    }

    @Given("CustomerLicenseId in context is null")
    public void ensureCustomerLicenseIsNull() {
        context.setCustomerLicenseId(null);
    }

    @Given("a quoteRequestId is available in context")
    public void ensureQuoteRequestReady() {
        ensureVehicleIdsReady();
        if (context.getQuoteRequestId() == null) {
            final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
            captureBody(body);
            setLastResponse(buildSpec().body(body).post(ShoryEndpoints.QUOTE_REQUEST));
            storeQuoteRequestId();
        }
    }

    @Given("one or more offer IDs are available in context")
    public void ensureOffersReady() {
        ensureQuoteRequestReady();
        if (context.getOfferIds() == null || context.getOfferIds().isEmpty()) {
            setLastResponse(buildSpec().queryParam("quoteRequestId", context.getQuoteRequestId()).get(ShoryEndpoints.QUOTE_OFFERS));
            storeAllOfferIds();
        }
    }

    @And("the offers list is non-empty")
    public void offersListNonEmpty() {
        final List<Map<String, Object>> offers = context.getLastApiResponse().jsonPath().getList("details.offers");
        context.appendStep("offers list is non-empty");
        assertThat(offers).isNotNull().isNotEmpty();
    }

    @And("all offer IDs are stored in context")
    @And("OfferIds are captured")
    public void storeAllOfferIds() {
        final List<String> ids = context.getLastApiResponse().jsonPath().getList("details.offers.id");
        context.setOfferIds(ids == null ? new ArrayList<>() : ids);
        context.addCapturedConfigValue("offerCount", String.valueOf(context.getOfferIds().size()));
        assertThat(context.getOfferIds()).isNotEmpty();
    }

    @When("I GET {string} with param {string} from context \\(first offer\\)")
    public void getOfferDetailsFirstOffer(final String path, final String param) {
        ensureOffersReady();
        setLastResponse(buildSpec().queryParam(param, context.getOfferIds().get(0)).get(path));
    }

    @And("the offer features list is non-empty")
    public void verifyOfferFeaturesNotEmpty() {
        final List<Map<String, Object>> features = context.getLastApiResponse().jsonPath().getList("details.features");
        context.appendStep("offer features list is non-empty");
        assertThat(features).isNotNull().isNotEmpty();
    }

    @And("feature code {int} corresponds to {string}")
    public void verifyFeatureCodeMapping(final int code, final String label) {
        context.appendStep("feature code " + code + " = \"" + label + "\"");
        final List<Map<String, Object>> features = context.getLastApiResponse().jsonPath().getList("details.features");
        assertThat(features).anySatisfy(feature -> {
            assertThat(((Number) feature.get("code")).intValue()).isEqualTo(code);
            assertThat(String.valueOf(feature.get("name"))).containsIgnoringCase(label);
        });
    }

    @And("the vehicle make is {string} and model is {string}")
    public void verifyVehicleMakeModel(final String make, final String model) {
        context.appendStep("vehicle make = \"" + make + "\", model = \"" + model + "\"");
        responseFieldString("details.vehicle[0].make", make);
        responseFieldString("details.vehicle[0].model", model);
    }

    @And("VehicleId, CustomerId, CustomerLicenseId are captured")
    public void captureIds() {
        storeAllVehicleContextValues();
    }

    @When("I POST to {string} using captured VehicleId and CustomerId")
    public void postUsingCapturedVehicleAndCustomer(final String path) {
        final Map<String, Object> body = QuoteRequestBuilder.fromContext(context);
        captureBody(body);
        setLastResponse(buildSpec().body(body).post(path));
    }

    @When("I poll GET {string}")
    public void pollGetByTemplate(final String templatePath) {
        final String path = templatePath.split("\\?")[0];
        setLastResponse(buildSpec().queryParam("quoteRequestId", context.getQuoteRequestId()).get(path));
    }

    @And("at least one offer is returned")
    public void atLeastOneOfferReturned() {
        offersListNonEmpty();
    }

    @When("I GET {string}")
    public void getByTemplate(final String templatePath) {
        final String path = templatePath.split("\\?")[0];
        setLastResponse(buildSpec().queryParam("offerId", context.getOfferIds().get(0)).get(path));
    }

    @And("the offer features include Third Party Liability \\(code 1\\)")
    public void tplFeature() {
        verifyFeatureCodeMapping(1, "Third Party Liability");
    }

    @And("the offer features include Car Rental \\(code 3\\)")
    public void carRentalFeature() {
        verifyFeatureCodeMapping(3, "Car Rental");
    }

    @And("the offer features include Roadside Assistance \\(code 4\\)")
    public void roadsideFeature() {
        verifyFeatureCodeMapping(4, "Roadside Assistance");
    }

    @And("Until the response indicates offers are ready \\(max {int} attempts, {int}ms interval\\)")
    public void untilReady(final int attempts, final int interval) throws InterruptedException {
        pollUntilReady(interval, attempts, "isReady");
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    /** Serializes a request body map to JSON and stores it in the context for the report. */
    private void captureBody(final Object body) {
        try {
            context.setLastRequestBodyJson(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(body));
        } catch (JsonProcessingException e) {
            context.setLastRequestBodyJson("{}");
        }
    }

    /** Extracts a single JSON path value from a response and adds it to captured config values. */
    private void captureResponseField(final JsonPath jp, final String label, final String path) {
        final Object val = jp.get(path);
        if (val != null) {
            context.addCapturedConfigValue(label, String.valueOf(val));
        }
    }

    private Object resolveContextValue(final String key) {
        return switch (key) {
            case "VehicleId" -> context.getVehicleId();
            case "CustomerId" -> context.getCustomerId();
            case "CustomerLicenseId" -> context.getCustomerLicenseId().orElse(null);
            case "QuoteRequestId" -> context.getQuoteRequestId();
            default -> null;
        };
    }

    private Map<String, Object> toNestedPayload(final Map<String, String> flatValues) {
        final Map<String, Object> payload = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : flatValues.entrySet()) {
            putNested(payload, entry.getKey(), parseValue(entry.getValue()));
        }
        return payload;
    }

    @SuppressWarnings("unchecked")
    private void putNested(final Map<String, Object> root, final String dottedKey, final Object value) {
        final String[] parts = dottedKey.split("\\.");
        Map<String, Object> cursor = root;
        for (int i = 0; i < parts.length - 1; i++) {
            cursor = (Map<String, Object>) cursor.computeIfAbsent(parts[i], ignored -> new LinkedHashMap<String, Object>());
        }
        cursor.put(parts[parts.length - 1], value);
    }

    private Object parseValue(final String rawValue) {
        if (rawValue == null) return null;
        final String value = rawValue.trim();
        if (value.matches("^-?\\d+$")) {
            try { return Long.parseLong(value); } catch (NumberFormatException e) { return value; }
        }
        return value;
    }

    private Map<String, Object> defaultPlatePayload() {
        final EnvironmentConfig cfg = ConfigManager.getConfig();
        final Map<String, Object> plateInfo = new LinkedHashMap<>();
        plateInfo.put("plateNumber", "78881");
        plateInfo.put("plateKindId", 1);
        plateInfo.put("plateSourceId", 1);
        plateInfo.put("plateColorTypeId", 52);

        final Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("vehicleIdentity", cfg.vehicleIdentityPlate());
        payload.put("personalId", 784197239274828L);
        payload.put("plateInfo", plateInfo);
        return payload;
    }

    private void setLastResponse(final Response response) {
        context.setLastApiResponse(response);
        context.setLastHttpStatus(response.statusCode());
        context.setLastResponseTimeMs(response.time());
        context.setRawLastResponse(response.asString());
    }
}
