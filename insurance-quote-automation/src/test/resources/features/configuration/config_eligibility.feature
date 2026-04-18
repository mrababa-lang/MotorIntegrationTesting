@motor @configuration @tpl-to-comp-upgrade @Regression
Feature: Configuration Eligibility - Restrict TPL for Existing TPL Policy Holders

  Background:
    Given the UAT environment is configured
    And I have retrieved vehicle details for personalId 784197239274828 and plateNumber "78881"
    And vehicle retrieval was successful

  Scenario: Evaluate whether TPL restriction for existing TPL holders is enabled
    When I run the configuration probe with insuranceTypeId 2 and constraint insuranceTypeId 1
    Then the configuration "restrictTplForExistingTplPolicy" should be evaluated
