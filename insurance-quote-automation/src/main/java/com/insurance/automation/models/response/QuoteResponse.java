package com.insurance.automation.models.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Response payload model for the Get Quote API.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * Nested premium detail model.
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PremiumDetail {

        @JsonProperty("monthly")
        private BigDecimal monthly;

        @JsonProperty("annual")
        private BigDecimal annual;
    }
}
