@motor @Config @Regression
Feature: Configuration Validation via Two-Quote Probe

  Background:
    Given the UAT environment is configured

  @CFG-VALID-001
  Scenario Outline: Evaluate configuration behavior for <configKey>
    And I have retrieved vehicle details for personalId <personalId> and plateNumber "<plateNumber>"
    And vehicle retrieval was successful
    When I run the configuration probe with insuranceTypeId <probeInsuranceTypeId> and constraint insuranceTypeId <constraintInsuranceTypeId>
    Then the configuration "<configKey>" should be evaluated

    Examples:
      | configKey                         | personalId       | plateNumber | probeInsuranceTypeId | constraintInsuranceTypeId |
      | restrictTplForExistingTplPolicy   | 784197239274828  | 78881       | 2                    | 1                         |
