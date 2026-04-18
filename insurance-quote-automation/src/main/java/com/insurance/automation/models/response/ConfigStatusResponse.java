package com.insurance.automation.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model for a configuration status response.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConfigStatusResponse {

    @JsonProperty("key")
    private String key;

    @JsonProperty("label")
    private String label;

    @JsonProperty("enabled")
    private Boolean enabled;

    @JsonProperty("value")
    private String value;

    @JsonProperty("category")
    private String category;

    @JsonProperty("lastUpdated")
    private String lastUpdated;

    public ConfigStatusResponse() {}

    public ConfigStatusResponse(final String key, final String label, final Boolean enabled,
            final String value, final String category, final String lastUpdated) {
        this.key = key;
        this.label = label;
        this.enabled = enabled;
        this.value = value;
        this.category = category;
        this.lastUpdated = lastUpdated;
    }

    public String getKey()         { return key; }
    public String getLabel()       { return label; }
    public Boolean getEnabled()    { return enabled; }
    public String getValue()       { return value; }
    public String getCategory()    { return category; }
    public String getLastUpdated() { return lastUpdated; }

    public void setKey(final String key)               { this.key = key; }
    public void setLabel(final String label)           { this.label = label; }
    public void setEnabled(final Boolean enabled)      { this.enabled = enabled; }
    public void setValue(final String value)           { this.value = value; }
    public void setCategory(final String category)     { this.category = category; }
    public void setLastUpdated(final String lastUpdated) { this.lastUpdated = lastUpdated; }
}
