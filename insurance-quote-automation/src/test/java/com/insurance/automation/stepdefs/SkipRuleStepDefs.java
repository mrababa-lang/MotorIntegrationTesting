package com.insurance.automation.stepdefs;

import static org.assertj.core.api.Assertions.assertThat;

import com.insurance.automation.context.ScenarioContext;
import com.insurance.automation.models.response.QuoteResponse;
import io.cucumber.java.en.And;
import java.util.ArrayList;
import java.util.List;

/**
 * Skip-rule focused step definitions.
 */
public class SkipRuleStepDefs {

    private final ScenarioContext scenarioContext;

    /**
     * Creates skip-rule step definitions.
     *
     * @param scenarioContext shared context.
     */
    public SkipRuleStepDefs(final ScenarioContext scenarioContext) {
        this.scenarioContext = scenarioContext;
    }

    /**
     * Checks that expected skip rule code exists in single or multi-rule response fields.
     *
     * @param ruleCode expected skip rule code.
     */
    @And("skip rule {string} is present in the response")
    public void verifySkipRulePresent(final String ruleCode) {
        final QuoteResponse response = scenarioContext.getLastQuoteResponse();
        final List<String> allRules = new ArrayList<>();
        if (response.getSkipReason() != null) {
            allRules.add(response.getSkipReason());
        }
        if (response.getSkipReasons() != null) {
            allRules.addAll(response.getSkipReasons());
        }
        assertThat(allRules).contains(ruleCode);
        scenarioContext.setLastExpectedStatement("skip rule " + ruleCode + " present");
    }
}
