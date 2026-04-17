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
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.PrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API client for Get Quote endpoint interactions.
 */
public class QuoteApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(QuoteApiClient.class);

    private final EnvironmentConfig config;
    private long lastResponseTime;
    private int lastHttpStatusCode;
    private String lastRawResponse;

    /**
     * Creates a quote API client.
     */
    public QuoteApiClient() {
        this.config = ConfigManager.getConfig();
    }

    /**
     * Posts a quote request and maps the response body to {@link QuoteResponse}.
     *
     * @param request quote request payload.
     * @return mapped quote response.
     */
    public QuoteResponse getQuote(final QuoteRequest request) {
        final RequestSpecification specification = buildRequestSpec();
        final Response response = RestAssured.given(specification)
            .body(request)
            .post();
        lastResponseTime = response.time();
        lastHttpStatusCode = response.statusCode();
        lastRawResponse = response.asString();
        LOG.info("Quote API call completed with status={} and duration={}ms", lastHttpStatusCode, lastResponseTime);
        return response.as(QuoteResponse.class);
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

    private RequestSpecification buildRequestSpec() {
        final boolean debugEnabled = LOG.isDebugEnabled();
        final RestAssuredConfig raConfig = RestAssuredConfig.config()
            .httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMs())
                .setParam("http.socket.timeout", config.readTimeoutMs()))
            .logConfig(LogConfig.logConfig().enableLoggingOfRequestAndResponseIfValidationFails(LogDetail.ALL));

        final RequestSpecBuilder builder = new RequestSpecBuilder()
            .setConfig(raConfig)
            .setBaseUri(config.baseUrl())
            .setBasePath(config.quoteEndpoint())
            .setContentType("application/json")
            .addHeader("X-Api-Key", config.apiKey())
            .addHeader("X-Client-Id", config.clientId());

        if (debugEnabled) {
            builder.log(LogDetail.ALL);
            builder.setConfig(raConfig.logConfig(LogConfig.logConfig().defaultStream(new PrintStream(System.err))));
        }

        return builder.build();
    }
}
