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
    String quoteEndpoint();

    /** @return config endpoint path. */
    @Key("config.endpoint")
    String configEndpoint();

    /** @return API key header value. */
    @Key("api.key")
    String apiKey();

    /** @return client id header value. */
    @Key("client.id")
    String clientId();

    /** @return connect timeout in milliseconds. */
    @Key("timeout.connect.ms")
    int connectTimeoutMs();

    /** @return read timeout in milliseconds. */
    @Key("timeout.read.ms")
    int readTimeoutMs();

    /** @return relative template path for HTML report. */
    @Key("report.template.path")
    String reportTemplatePath();

    /** @return output directory for generated reports. */
    @Key("report.output.dir")
    String reportOutputDir();
}
