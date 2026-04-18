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
    