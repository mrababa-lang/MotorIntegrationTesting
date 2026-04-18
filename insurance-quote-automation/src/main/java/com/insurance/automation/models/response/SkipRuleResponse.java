package com.insurance.automation.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Model representing a skip rule in API/report responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SkipRuleResponse {

    @JsonProperty("code")
    private String code;

    @JsonProperty("message")
    private String message;

    public SkipRuleResponse() {}

    public SkipRuleResponse(final String code, final String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode()    { return code; }
    public String getMessage() { return message; }

    public void setCode(final String code)       { this.code = code; }
    public void setMessage(final String message) { this.message = message; }
}
