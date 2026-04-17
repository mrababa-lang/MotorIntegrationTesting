@motor @offer-details @Regression
Feature: Offer Details
  Background:
    Given one or more offer IDs are available in context

  @happy-path
  Scenario: Retrieve details for the first available offer
    When I GET "/offer/details" with param "offerId" from context (first offer)
    Then the response status code should be 200
    And the response field "isSuccess" should be true
    And the offer features list is non-empty
    And feature code 1 corresponds to "Third Party Liability"
    And feature code 3 corresponds to "Car Rental"
    And feature code 4 corresponds to "Roadside Assistance"
