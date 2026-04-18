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
        return RestAssured.given()
            .baseUri(config.baseUrl())
            .auth().oauth2(config.authToken())
            .header("custom-lang", config.defaultHeaderCustomLang())
            .header("clientplatform", "3")
            .contentType(ContentType.JSON)
            .accept(ContentType.JSON);
    }
}
