@motor @offers @Regression
Feature: Quote Offers Polling
  Background:
    Given a quoteRequestId is available in context

  @happy-path
  Scenario: Poll until quote offers are ready
    When I GET "/quote/offers" with param "quoteRequestId" from context
    And I poll with interval 3000ms up to 10 attempts until "isReady" is true
    Then the response status code should be 200
    And the response field "isSuccess" should be true
    And the offers list is non-empty
    And all offer IDs are stored in context
