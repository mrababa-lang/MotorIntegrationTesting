package com.insurance.automation.api;

import com.insurance.automation.config.ConfigManager;
import com.insurance.automation.config.EnvironmentConfig;
import com.insurance.automation.models.request.QuoteRequest;
import com.insurance.automation.models.response.QuoteResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API client for Get Quote endpoint interactions.
 */
public class QuoteApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteApiClient.class);

    private final RequestSpecification requestSpecification;
    private long lastResponseTime;
    private int lastHttpStatusCode;
    private String lastRawResponse;

    /**
     * Creates a quote API client and initializes the reusable REST Assured request specification.
     */
    public QuoteApiClient() {
        final EnvironmentConfig config = ConfigManager.getConfig();
        this.requestSpecification = buildRequestSpec(config);
    }

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
        final Response response = RestAssured.given(requestSpecification)
            .body(request)
            .post();

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

    private RequestSpecification buildRequestSpec(final EnvironmentConfig config) {
        final RestAssuredConfig restAssuredConfig = RestAssuredConfig.config()
            .httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMs())
                .setParam("http.socket.timeout", config.readTimeoutMs()))
            .logConfig(LogConfig.logConfig());

        final RequestSpecBuilder builder = new RequestSpecBuilder()
            .setConfig(restAssuredConfig)
            .setBaseUri(config.baseUrl())
            .setBasePath(config.quoteEndpoint())
            .setContentType("application/json")
            .addHeader("X-Api-Key", config.apiKey())
            .addHeader("X-Client-Id", config.clientId());

        if (LOG.isDebugEnabled()) {
            builder.addFilter(new RequestLoggingFilter(LogDetail.ALL));
            builder.addFilter(new ResponseLoggingFilter(LogDetail.ALL));
        }

        return builder.build();
    }
}
