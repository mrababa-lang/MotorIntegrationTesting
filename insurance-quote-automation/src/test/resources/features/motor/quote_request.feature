@motor @quote @Regression
Feature: Quote Request
  Background:
    Given a vehicle has been successfully retrieved and IDs are in context

  @happy-path
  Scenario: Request a motor insurance quote
    When I POST to "/quote/request" with contextual IDs:
      | vehicleId         | {context:VehicleId}         |
      | customerId        | {context:CustomerId}        |
      | customerLicenseId | {context:CustomerLicenseId} |
    Then the response status code should be 200
    And the response field "isSuccess" should be true
    And the quoteRequestId is stored in context

  @missing-licence
  Scenario: Request a quote when driving licence is absent
    Given CustomerLicenseId in context is null
    When I POST to "/quote/request" omitting the customerLicenseId field
    Then the response status code should be 200
    And the response field "isSuccess" should be true
    And the quoteRequestId is stored in context

  @negative
  Scenario: Invalid vehicle ID in quote request
    When I POST to "/quote/request" with body:
      | vehicleId  | INVALID_ID_00000 |
      | customerId | Zwy9bZvbtWirHIuTheJMhQ |
    Then the response status code should be 200
