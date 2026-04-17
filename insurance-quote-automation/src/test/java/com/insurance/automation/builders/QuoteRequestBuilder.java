package com.insurance.automation.builders;

import com.insurance.automation.context.ScenarioContext;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builder utility for /quote/request payload generation.
 */
public final class QuoteRequestBuilder {

    private QuoteRequestBuilder() {
    }

    public static Map<String, Object> fromContext(final ScenarioContext context) {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("vehicleId", context.getVehicleId());
        body.put("customerId", context.getCustomerId());
        if (context.getCustomerLicenseId().isPresent()) {
            body.put("customerLicenseId", context.getCustomerLicenseId().get());
        }
        return body;
    }
}
