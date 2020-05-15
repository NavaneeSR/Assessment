@smoke
Feature: Employee Service Validation for Logout

  Background:
    Given the user accessing base URI

  Scenario: Validate the response message when an employee is logged out from system
    When the user making GET call "/employee/logout" with name
    Then the user should see the response code as "200"
    Then validate the response message "Logout successful" for employee logged out from system