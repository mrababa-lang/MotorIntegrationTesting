@motor @vehicle @Regression
Feature: Vehicle Retrieval
  Background:
    Given the UAT environment is configured with base URL "https://motor-uat.shory.com"
    And the request header "custom-lang" is set to "AR"

  @plate @happy-path
  Scenario: Retrieve vehicle by plate number
    When I POST to "/vehicle/retrieve" with body:
      | vehicleIdentity          | 2               |
      | personalId               | 784197239274828 |
      | plateInfo.plateNumber    | 78881           |
      | plateInfo.plateKindId    | 1               |
      | plateInfo.plateSourceId  | 1               |
      | plateInfo.plateColorTypeId | 52            |
    Then the response status code should be 200
    And the response field "isSuccess" should be true
    And the response field "details.vehicle[0].make" should be "JAGUAR"
    And the response field "details.vehicle[0].model" should be "XF"
    And the response field "details.vehicle[0].year" should be 2012
    And the response field "details.vehicle[0].chassisNo" should be "SAJAA05D8CFS48125"
    And the response field "details.vehicle[0].vehicleSpecification.admeId" should be "12082"
    And the response field "details.vehicle[0].vehicleSpecification.specCode" should be "333"
    And the response field "details.vehicle[0].vehicleSpecification.specDesc" should be "LUXURY"
    And the response field "details.vehicle[0].vehicleSpecification.engineSize" should be "3.0 L"
    And the response field "details.vehicle[0].vehicleSpecification.bodyType" should be "Sedan"
    And the response field "details.vehicle[0].vehicleSpecification.transmission" should be "AUTOMATIC"
    And the response field "details.vehicle[0].vehicleSpecification.noOfCylinders" should be "V6"
    And the response field "details.vehicle[0].vehicleSpecification.horsePower" should be "238 HP"
    And the response field "details.vehicle[0].vehicleSpecification.doors" should be "4 DOORS"
    And the response field "details.vehicle[0].vehicleSpecification.seats" should be "5 SEATS"
    And the response field "details.vehicle[0].vehicleSpecification.finalDrive" should be "REAR WHEEL DRIVE"
    And the response field "details.vehicle[0].vehicleSpecification.vehicleType" should be "CAR"
    And the response field "details.vehicle[0].vehicleSpecification.fuelType" should be "PETROL"
    And the response field "details.vehicle[0].makeHasLogo" should be true
    And the response field "details.vehicle[0].logo" should start with "https://"
    And the VehicleId, CustomerId, and CustomerLicenseId are stored in context

  @vcc @happy-path
  Scenario: Retrieve vehicle by VCC certificate number
    When I POST to "/vehicle/retrieve" with body:
      | vehicleIdentity | 3                     |
      | vcc.cardNumber  | 10855618              |
      | vcc.date        | 5/30/2018 12:00:00 AM |
      | vcc.sourceId    | 32                    |
    Then the response status code should be 200
    And the response field "isSuccess" should be true
    And the response field "details.vehicle[0].make" should be "BMW"
    And the response field "details.vehicle[0].model" should be "X 3"
    And the response field "details.vehicle[0].chassisNo" should be "TST08"
    And the response field "details.vehicle[0].plateInfo" should be null

  @plate @missing-licence
  Scenario: Retrieve vehicle when customer driving licence is absent
    When I POST to "/vehicle/retrieve" with body:
      | vehicleIdentity          | 2               |
      | personalId               | 784197239274828 |
      | plateInfo.plateNumber    | 78881           |
      | plateInfo.plateKindId    | 1               |
      | plateInfo.plateSourceId  | 1               |
      | plateInfo.plateColorTypeId | 52            |
    Then the response status code should be 200
    And the response field "details.customerLicenseId" should be null
    And the VehicleId and CustomerId are stored in context
    And CustomerLicenseId is stored as null in context

  @negative
  Scenario Outline: Vehicle retrieval negative validations
    When I POST to "/vehicle/retrieve" with body:
      | vehicleIdentity | <vehicleIdentity> |
      | personalId      | <personalId>      |
      | plateInfo.plateNumber | <plateNumber> |
      | vcc.cardNumber  | <vccCardNumber>   |
    Then the response status code should be <statusCode>

    Examples:
      | vehicleIdentity | personalId       | plateNumber | vccCardNumber | statusCode |
      | 2               | 000000000000000  | 78881       | 10855618      | 200        |
      | 2               | 784197239274828  | 00000       | 10855618      | 200        |
      | 3               | 784197239274828  | 78881       | 0             | 200        |
