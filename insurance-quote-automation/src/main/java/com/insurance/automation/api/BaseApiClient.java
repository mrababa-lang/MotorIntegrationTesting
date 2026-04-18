package com.insurance.automation.api;

import com.insurance.automation.config.ConfigManager;
import com.insurance.automation.config.EnvironmentConfig;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Shared REST Assured base client for Shory motor flows.
 */
public class BaseApiClient {

    protected final EnvironmentConfig config;

    public BaseApiClient() {
        this.config = ConfigManager.getConfig();
    }

    protected RequestSpecification buildSpec() {
        RequestSpecification specification = RestAssured.given()
            .baseUri(config.baseUrl());

        final String apiBasePath = trimToNull(config.apiBasePath());
        if (apiBasePath != null) {
            specification = specification.basePath(apiBasePath);
        }

        final String authToken = trimToNull(config.authToken());
        if (authToken != null && !looksLikeUnresolvedPlaceholder(authToken)) {
            specification = specification.auth().oauth2(authToken);
        }

        return specification
            .header("custom-lang", config.defaultHeaderCustomLang())
            .header("clientplatform", "3")
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);
    }

    private static String trimToNull(final String value) {
        if (value == null) {
            return null;
        }
        final String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private static boolean looksLikeUnresolvedPlaceholder(final String value) {
        return value.startsWith("${") && value.endsWith("}");
    }
}
