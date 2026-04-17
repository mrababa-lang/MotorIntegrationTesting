@motor @e2e @happy-path @Regression
Feature: Shory Motor UAT — Full Quote Flow

  Scenario: Complete motor insurance quote journey via plate lookup
    Given I POST to "/vehicle/retrieve" with payload:
      """
      {
        "vehicleIdentity": 2,
        "personalId": 784197239274828,
        "plateInfo": {
          "plateNumber": "78881",
          "plateKindId": 1,
          "plateSourceId": 1,
          "plateColorTypeId": 52
        }
      }
      """
    Then the response status code should be 200
    And the vehicle make is "JAGUAR" and model is "XF"
    And VehicleId, CustomerId, CustomerLicenseId are captured

    When I POST to "/quote/request" using captured VehicleId and CustomerId
    Then the response status code should be 200
    And QuoteRequestId is captured

    When I poll GET "/quote/offers?quoteRequestId={QuoteRequestId}"
    And Until the response indicates offers are ready (max 10 attempts, 3000ms interval)
    Then at least one offer is returned
    And OfferIds are captured

    When I GET "/offer/details?offerId={OfferIds[0]}"
    Then the response status code should be 200
    And the offer features include Third Party Liability (code 1)
    And the offer features include Car Rental (code 3)
    And the offer features include Roadside Assistance (code 4)
    And the test result is recorded in the report generator
