package com.insurance.automation.api;

import com.insurance.automation.models.request.QuoteRequest;
import com.insurance.automation.models.response.QuoteResponse;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API client for Shory quote request endpoint interactions.
 */
public class QuoteApiClient extends BaseApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteApiClient.class);

    private long lastResponseTime;
    private int lastHttpStatusCode;
    private String lastRawResponse;

    /**
     * Posts a quote request and maps the response body to {@link QuoteResponse}.
     *
     * @param request quote request payload.
     * @return mapped quote response.
     */
    public QuoteResponse getQuote(final QuoteRequest request) {
        return requestQuote(request).as(QuoteResponse.class);
    }

    /**
     * Posts a quote request and returns the raw REST Assured response.
     *
     * @param request quote request payload.
     * @return raw response.
     */
    public Response requestQuote(final Object request) {
        final Response response = buildSpec()
            .body(request)
            .post(ShoryEndpoints.QUOTE_REQUEST);

        lastResponseTime = response.time();
        lastHttpStatusCode = response.statusCode();
        lastRawResponse = response.asString();

        LOG.info("Quote API call completed with status={} and duration={}ms", lastHttpStatusCode, lastResponseTime);
        return response;
    }

    /**
     * Returns the duration of the last API call.
     *
     * @return last response duration in milliseconds.
     */
    public long getLastResponseTime() {
        return lastResponseTime;
    }

    /**
     * Returns HTTP status from the last API call.
     *
     * @return status code.
     */
    public int getLastHttpStatusCode() {
        return lastHttpStatusCode;
    }

    /**
     * Returns the full raw response body from the last API call.
     *
     * @return raw response as JSON string.
     */
    public String getLastRawResponse() {
        return lastRawResponse;
    }
}
