package com.insurance.automation.builders;

import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.runners.TestRunner;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder utility for /quote/request payload generation.
 * Automatically includes the active insuranceCompanyProfileId from the suite runner
 * so every quote request is scoped to the company under test.
 */
public final class QuoteRequestBuilder {

    private QuoteRequestBuilder() {}

    /**
     * Builds a quote request body from the current scenario context.
     * Always includes {@code insuranceCompanyProfileId} from the active suite run.
     *
     * @param context current scenario context holding vehicle/customer IDs.
     * @return mutable map ready to be serialised as a JSON request body.
     */
    public static Map<String, Object> fromContext(final ScenarioContext context) {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("vehicleId",  context.getVehicleId());
        body.put("customerId", context.getCustomerId());
        if (context.getCustomerLicenseId().isPresent()) {
            body.put("customerLicenseId", context.getCustomerLicenseId().get());
        }
        // Scope the quote to the insurance company under test
        final int companyId = TestRunner.getActiveCompanyProfileId();
        if (companyId > 0) {
            body.put("insuranceCompanyProfileId", companyId);
        }
        return body;
    }
}
