package com.insurance.automation.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload model for the Get Quote API.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuoteRequest {

    @JsonProperty("state")
    private String state;

    @JsonProperty("age")
    private Integer age;

    @JsonProperty("vehicleYear")
    private Integer vehicleYear;

    @JsonProperty("coverageType")
    private String coverageType;

    @JsonProperty("creditScore")
    private Integer creditScore;

    @JsonProperty("priorInsurance")
    private Boolean priorInsurance;

    @JsonProperty("multiCar")
    private Boolean multiCar;
}
