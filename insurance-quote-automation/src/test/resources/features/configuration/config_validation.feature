@SuiteTag @Config @Regression
Feature: Configuration Validation

  @TC-009
  Scenario Outline: Configuration <configKey> is active and expected
    When the configuration status API is called for key "<configKey>"
    Then the configuration enabled state is <expectedEnabled>
    And the configuration value is "<expectedValue>"
    And the result is recorded in the report as category "<category>"

    Examples:
      | configKey              | expectedEnabled | expectedValue | category       |
      | priorInsuranceDiscount | true            | -5%           | Discounts      |
      | multiCarDiscount       | true            | -8%           | Discounts      |
      | skipRule_SR-AGE-001    | true            | minAge=18     | Skip Rules     |
      | skipRule_SR-GEO-003    | true            | FL,LA,MS      | Skip Rules     |
      | skipRule_SR-CREDIT-002 | true            | minScore=580  | Skip Rules     |
      | skipRule_SR-VEH-004    | true            | maxAge=15yrs  | Skip Rules     |
      | baseRate_CA            | true            | $78           | Rating Factors |
      | stateSurcharge_CA      | true            | 7%            | Rating Factors |
