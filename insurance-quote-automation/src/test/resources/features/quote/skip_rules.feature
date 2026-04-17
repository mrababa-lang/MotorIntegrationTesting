@SuiteTag @SkipRule @Regression
Feature: Skip Rules — Quote Exclusions

  @TC-004
  Scenario: SR-AGE-001 — Underage applicant is declined
    Given a quote request for state "TX", age 17, vehicle year 2019
    And coverage type is "liability" with credit score 680
    And prior insurance is false
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "skipped"
    And skip rule "SR-AGE-001" is present in the response

  @TC-005
  Scenario: SR-GEO-003 — Restricted state applicant is declined
    Given a quote request for state "FL", age 25, vehicle year 2020
    And coverage type is "liability" with credit score 690
    And prior insurance is false
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "skipped"
    And skip rule "SR-GEO-003" is present in the response

  @TC-006
  Scenario: SR-CREDIT-002 — Low credit score applicant is declined
    Given a quote request for state "TX", age 30, vehicle year 2020
    And coverage type is "liability" with credit score 540
    And prior insurance is true
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "skipped"
    And skip rule "SR-CREDIT-002" is present in the response

  @TC-007
  Scenario: SR-VEH-004 — Vehicle older than 15 years is declined
    Given a quote request for state "TX", age 30, vehicle year 2008
    And coverage type is "liability" with credit score 700
    And prior insurance is true
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "skipped"
    And skip rule "SR-VEH-004" is present in the response

  @TC-008
  Scenario: Multiple skip rules fire simultaneously
    Given a quote request for state "FL", age 16, vehicle year 2021
    And coverage type is "liability" with credit score 700
    And prior insurance is false
    When the Get Quote API is called
    Then the response status is 200
    And the quote status is "skipped"
    And skip rule "SR-AGE-001" is present in the response
    And skip rule "SR-GEO-003" is present in the response
