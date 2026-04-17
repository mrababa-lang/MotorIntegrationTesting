package com.insurance.automation.config;

import org.aeonbits.owner.Config;

/**
 * Typed contract for environment-specific framework properties.
 */
@Config.Sources({"classpath:environments/${env}.properties"})
public interface EnvironmentConfig extends Config {

    /** @return base URL for API calls. */
    @Key("base.url")
    String baseUrl();

    /** @return quote endpoint path. */
    @Key("quote.endpoint")
    @DefaultValue("/api/v2/quotes/get")
    String quoteEndpoint();

    /** @return config endpoint path. */
    @Key("config.endpoint")
    @DefaultValue("/api/v2/config/status")
    String configEndpoint();

    /** @return API key header value. */
    @Key("api.key")
    @DefaultValue("")
    String apiKey();

    /** @return client id header value. */
    @Key("client.id")
    @DefaultValue("")
    String clientId();

    /** @return bearer token for auth. */
    @Key("auth.token")
    @DefaultValue("")
    String authToken();

    /** @return custom language header value. */
    @Key("default.header.custom-lang")
    @DefaultValue("AR")
    String defaultHeaderCustomLang();

    /** @return plate identity code. */
    @Key("vehicle.identity.plate")
    @DefaultValue("2")
    int vehicleIdentityPlate();

    /** @return vcc identity code. */
    @Key("vehicle.identity.vcc")
    @DefaultValue("3")
    int vehicleIdentityVcc();

    /** @return max poll attempts. */
    @Key("polling.max.attempts")
    @DefaultValue("10")
    int pollingMaxAttempts();

    /** @return poll interval in ms. */
    @Key("polling.interval.ms")
    @DefaultValue("3000")
    int pollingIntervalMs();

    /** @return connect timeout in milliseconds. */
    @Key("timeout.connect.ms")
    @DefaultValue("5000")
    int connectTimeoutMs();

    /** @return read timeout in milliseconds. */
    @Key("timeout.read.ms")
    @DefaultValue("15000")
    int readTimeoutMs();

    /** @return relative template path for HTML report. */
    @Key("report.template.path")
    String reportTemplatePath();

    /** @return output directory for generated reports. */
    @Key("report.output.dir")
    @DefaultValue("target/reports")
    String reportOutputDir();
}
