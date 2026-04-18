package com.insurance.automation.models.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request payload model for the Get Quote API.
 */
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

    public QuoteRequest() {}

    public QuoteRequest(final String state, final Integer age, final Integer vehicleYear,
            final String coverageType, final Integer creditScore,
            final Boolean priorInsurance, final Boolean multiCar) {
        this.state = state;
        this.age = age;
        this.vehicleYear = vehicleYear;
        this.coverageType = coverageType;
        this.creditScore = creditScore;
        this.priorInsurance = priorInsurance;
        this.multiCar = multiCar;
    }

    public String getState()           { return state; }
    public Integer getAge()            { return age; }
    public Integer getVehicleYear()    { return vehicleYear; }
    public String getCoverageType()    { return coverageType; }
    public Integer getCreditScore()    { return creditScore; }
    public Boolean getPriorInsurance() { return priorInsurance; }
    public Boolean getMultiCar()       { return multiCar; }

    public void setState(final String state)               { this.state = state; }
    public void setAge(final Integer age)                  { this.age = age; }
    public void setVehicleYear(final Integer vehicleYear)  { this.vehicleYear = vehicleYear; }
    public void setCoverageType(final String coverageType) { this.coverageType = coverageType; }
    public void setCreditScore(final Integer creditScore)  { this.creditScore = creditScore; }
    public void setPriorInsurance(final Boolean priorInsurance) { this.priorInsurance = priorInsurance; }
    public void setMultiCar(final Boolean multiCar)        { this.multiCar = multiCar; }

    public static QuoteRequestBuilder builder() { return new QuoteRequestBuilder(); }

    public QuoteRequestBuilder toBuilder() {
        return new QuoteRequestBuilder()
            .state(state).age(age).vehicleYear(vehicleYear).coverageType(coverageType)
            .creditScore(creditScore).priorInsurance(priorInsurance).multiCar(multiCar);
    }

    public static class QuoteRequestBuilder {
        private String state;
        private Integer age;
        private Integer vehicleYear;
        private String coverageType;
        private Integer creditScore;
        private Boolean priorInsurance;
        private Boolean multiCar;

        public QuoteRequestBuilder state(final String state)               { this.state = state;             return this; }
        public QuoteRequestBuilder age(final Integer age)                  { this.age = age;                 return this; }
        public QuoteRequestBuilder vehicleYear(final Integer vehicleYear)  { this.vehicleYear = vehicleYear; return this; }
        public QuoteRequestBuilder coverageType(final String coverageType) { this.coverageType = coverageType; return this; }
        public QuoteRequestBuilder creditScore(final Integer creditScore)  { this.creditScore = creditScore; return this; }
        public QuoteRequestBuilder priorInsurance(final Boolean priorInsurance) { this.priorInsurance = priorInsurance; return this; }
        public QuoteRequestBuilder multiCar(final Boolean multiCar)        { this.multiCar = multiCar;       return this; }

        public QuoteRequest build() {
            return new QuoteRequest(state, age, vehicleYear, coverageType, creditScore, priorInsurance, multiCar);
        }
    }
}
