package com.insurance.automation.api;

import com.insurance.automation.config.ConfigManager;
import com.insurance.automation.config.EnvironmentConfig;
import com.insurance.automation.models.response.ConfigStatusResponse;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * API client for configuration status endpoint interactions.
 */
public class ConfigApiClient {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigApiClient.class);

    private final RequestSpecification requestSpecification;

    /**
     * Creates a configuration API client.
     */
    public ConfigApiClient() {
        final EnvironmentConfig config = ConfigManager.getConfig();
        this.requestSpecification = buildRequestSpec(config);
    }

    /**
     * Queries a configuration key.
     *
     * @param configKey key identifier.
     * @return mapped config status response.
     */
    public ConfigStatusResponse getConfigStatus(final String configKey) {
        final Response response = RestAssured.given(requestSpecification)
            .queryParam("key", configKey)
            .get();
        LOG.info("Config API call completed for key={} with status={}", configKey, response.statusCode());
        return response.as(ConfigStatusResponse.class);
    }

    /**
     * Queries all configuration values.
     *
     * @return list of configuration responses.
     */
    public List<ConfigStatusResponse> getAllConfigs() {
        final Response response = RestAssured.given(requestSpecification).get();
        LOG.info("Config API all-configs call completed with status={}", response.statusCode());
        return Arrays.asList(response.as(ConfigStatusResponse[].class));
    }

    private RequestSpecification buildRequestSpec(final EnvironmentConfig config) {
        return new RequestSpecBuilder()
            .setConfig(RestAssuredConfig.config().httpClient(HttpClientConfig.httpClientConfig()
                .setParam("http.connection.timeout", config.connectTimeoutMs())
                .setParam("http.socket.timeout", config.readTimeoutMs())))
            .setBaseUri(config.baseUrl())
            .setBasePath(config.configEndpoint())
            .setContentType("application/json")
            .addHeader("X-Api-Key", config.apiKey())
            .addHeader("X-Client-Id", config.clientId())
            .build();
    }
}
