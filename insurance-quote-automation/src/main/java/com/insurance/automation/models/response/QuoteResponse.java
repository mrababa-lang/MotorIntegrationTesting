package com.insurance.automation.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

/**
 * Response payload model for the Get Quote API.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QuoteResponse {

    @JsonProperty("quoteId")
    private String quoteId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("premium")
    private PremiumDetail premium;

    @JsonProperty("skipReason")
    private String skipReason;

    @JsonProperty("skipReasons")
    private List<String> skipReasons;

    @JsonProperty("message")
    private String message;

    @JsonProperty("reviewRequired")
    private Boolean reviewRequired;

    @JsonProperty("ratingFactor")
    private String ratingFactor;

    @JsonProperty("baseRate")
    private BigDecimal baseRate;

    @JsonProperty("discounts")
    private List<String> discounts;

    public QuoteResponse() {}

    public QuoteResponse(final String quoteId, final String status, final PremiumDetail premium,
            final String skipReason, final List<String> skipReasons, final String message,
            final Boolean reviewRequired, final String ratingFactor, final BigDecimal baseRate,
            final List<String> discounts) {
        this.quoteId = quoteId;
        this.status = status;
        this.premium = premium;
        this.skipReason = skipReason;
        this.skipReasons = skipReasons;
        this.message = message;
        this.reviewRequired = reviewRequired;
        this.ratingFactor = ratingFactor;
        this.baseRate = baseRate;
        this.discounts = discounts;
    }

    public String getQuoteId()           { return quoteId; }
    public String getStatus()            { return status; }
    public PremiumDetail getPremium()    { return premium; }
    public String getSkipReason()        { return skipReason; }
    public List<String> getSkipReasons() { return skipReasons; }
    public String getMessage()           { return message; }
    public Boolean getReviewRequired()   { return reviewRequired; }
    public String getRatingFactor()      { return ratingFactor; }
    public BigDecimal getBaseRate()      { return baseRate; }
    public List<String> getDiscounts()   { return discounts; }

    public void setQuoteId(final String quoteId)               { this.quoteId = quoteId; }
    public void setStatus(final String status)                 { this.status = status; }
    public void setPremium(final PremiumDetail premium)        { this.premium = premium; }
    public void setSkipReason(final String skipReason)         { this.skipReason = skipReason; }
    public void setSkipReasons(final List<String> skipReasons) { this.skipReasons = skipReasons; }
    public void setMessage(final String message)               { this.message = message; }
    public void setReviewRequired(final Boolean reviewRequired){ this.reviewRequired = reviewRequired; }
    public void setRatingFactor(final String ratingFactor)     { this.ratingFactor = ratingFactor; }
    public void setBaseRate(final BigDecimal baseRate)         { this.baseRate = baseRate; }
    public void setDiscounts(final List<String> discounts)     { this.discounts = discounts; }

    /**
     * Nested premium detail model.
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PremiumDetail {

        @JsonProperty("monthly")
        private BigDecimal monthly;

        @JsonProperty("annual")
        private BigDecimal annual;

        public PremiumDetail() {}

        public PremiumDetail(final BigDecimal monthly, final BigDecimal annual) {
            this.monthly = monthly;
            this.annual = annual;
        }

        public BigDecimal getMonthly() { return monthly; }
        public BigDecimal getAnnual()  { return annual; }

        public void setMonthly(final BigDecimal monthly) { this.monthly = monthly; }
        public void setAnnual(final BigDecimal annual)   { this.annual = annual; }
    }
}
