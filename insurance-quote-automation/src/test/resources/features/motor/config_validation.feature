@motor @config @Regression
Feature: Motor Insurance — In-Scope Rules and Configuration Validation

  Validates the exact set of skip rules and configuration parameters that are in scope for testing.
  Uses the two-probe pattern for each rule:
    PROBE  = standard valid request (must succeed for the test to be meaningful)
    CONSTRAINT = same request with one field changed to exercise the specific rule
  Outcome logic:
    Probe FAILS                       → DATA_ISSUE  (env/data problem — cannot evaluate)
    Probe PASSES + Constraint FAILS   → ENABLED     (rule is active and enforced)
    Probe PASSES + Constraint PASSES  → DISABLED    (rule is configured off / not enforced)

  Background:
    Given a vehicle has been successfully retrieved and IDs are in context

  # ════════════════════════════════════════════════════════════════════════════
  # SKIP RULES
  # ════════════════════════════════════════════════════════════════════════════

  @skip-rule @TC-SR-001
  Scenario: SR-001 — TPL to Comprehensive upgrade restriction
    # Rule: should the system allow a customer who currently holds TPL insurance
    # to upgrade and request a Comprehensive quote?
    # Probe:      standard Comprehensive quote → expect success
    # Constraint: same request with insuranceTypeId=1 (TPL) → if rejected, rule is ENABLED
    When I submit a probe quote using standard contextual IDs
    And I submit a constraint quote requesting TPL insurance type
    Then the rule "TPL to Comprehensive Upgrade" in category "Skip Rules" is evaluated and recorded

  @skip-rule @TC-SR-002
  Scenario: SR-002 — Expired prior insurance to Comprehensive upgrade restriction
    # Rule: should the system allow a customer whose prior insurance has expired
    # to request a new Comprehensive policy?
    # Probe:      standard quote with no prior-insurance flag → expect success
    # Constraint: same request flagged as coming from an expired prior policy
    When I submit a probe quote using standard contextual IDs
    And I submit a constraint quote with expired prior insurance
    Then the rule "Expired Insurance to Comprehensive Upgrade" in category "Skip Rules" is evaluated and recorded

  @skip-rule @TC-SR-003
  Scenario: SR-003 — Non-GCC vehicle allowance
    # Rule: are non-GCC-registered vehicles allowed to obtain a quote?
    # Probe:      standard GCC vehicle quote → expect success
    # Constraint: same quote flagged as a non-GCC vehicle origin
    When I submit a probe quote using standard contextual IDs
    And I submit a constraint quote for a non-GCC vehicle
    Then the rule "Non-GCC Vehicle Allowed" in category "Skip Rules" is evaluated and recorded

  # ════════════════════════════════════════════════════════════════════════════
  # CONFIGURATION PARAMETERS
  # ════════════════════════════════════════════════════════════════════════════

  @config-test @TC-CFG-001
  Scenario: CFG-001 — Maximum driver age limit
    # Rule: is there an upper driver-age limit above which quotes are rejected?
    # Probe:      standard quote (driver age within normal range) → expect success
    # Constraint: quote with driver age 85 → if rejected, max-age cap is ENABLED
    When I submit a probe quote using standard contextual IDs
    And I submit a constraint quote with driver age 85
    Then the rule "Maximum Driver Age" in category "Configuration Parameters" is evaluated and recorded

  @config-test @TC-CFG-002
  Scenario: CFG-002 — Minimum driver age limit
    # Rule: is there a lower driver-age limit below which quotes are rejected?
    # Probe:      standard quote → expect success
    # Constraint: quote with driver age 17 → if rejected, min-age floor is ENABLED
    When I submit a probe quote using standard contextual IDs
    And I submit a constraint quote with driver age 17
    Then the rule "Minimum Driver Age" in category "Configuration Parameters" is evaluated and recorded

  @config-test @TC-CFG-003
  Scenario: CFG-003 — Maximum vehicle value limit
    # Rule: is there a maximum insured vehicle value above which quotes are rejected?
    # Probe:      standard quote → expect success
    # Constraint: quote with vehicle value 5,000,000 AED → if rejected, cap is ENABLED
    When I submit a probe quote using standard contextual IDs
    And I submit a constraint quote with vehicle value 5000000
    Then the rule "Maximum Vehicle Value" in category "Configuration Parameters" is evaluated and recorded

  @config-test @TC-CFG-004
  Scenario: CFG-004 — Minimum vehicle value limit
    # Rule: is there a minimum insured vehicle value below which quotes are rejected?
    # Probe:      standard quote → expect success
    # Constraint: quote with vehicle value 1,000 AED → if rejected, floor is ENABLED
    When I submit a probe quote using standard contextual IDs
    And I submit a constraint quote with vehicle value 1000
    Then the rule "Minimum Vehicle Value" in category "Configuration Parameters" is evaluated and recorded
