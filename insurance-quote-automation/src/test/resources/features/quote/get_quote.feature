@SuiteTag @Quote @Regression
Feature: Get Quote — Premium Calculation

  @TC-001
  Scenario Outline: Quote generated for valid applicant in <state>
    Given a quote request for state "<state>", age <age>, vehicle year <year>
    And coverage type is "<coverage>" with credit score <credit>
    And prior insurance is <priorInsurance>
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "quoted"
    And the monthly premium is between <minPremium> and <maxPremium>

    Examples:
      | state | age | year | coverage      | credit | priorInsurance | minPremium | maxPremium |
      | CA    | 35  | 2021 | comprehensive | 720    | true           | 85         | 110        |
      | TX    | 28  | 2020 | liability     | 680    | false          | 60         | 85         |
      | NY    | 45  | 2019 | comprehensive | 750    | true           | 90         | 120        |
      | WA    | 50  | 2023 | comprehensive | 800    | true           | 70         | 95         |

  @TC-002
  Scenario: Prior insurance discount reduces premium by 5%
    Given a quote request for state "CA", age 34, vehicle year 2022
    And coverage type is "comprehensive" with credit score 740
    And prior insurance is true
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "quoted"
    And the prior insurance discount reduces monthly premium by 5 percent

  @TC-003
  Scenario: Stacked discounts are applied for prior insurance and multi-car
    Given a quote request for state "TX", age 40, vehicle year 2021
    And coverage type is "liability" with credit score 760
    And prior insurance is true
    And multi car is true
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "quoted"
    And stacked discounts are applied for prior insurance and multi car
