@smoke
Feature: Employee Service Validation for delete employee details

  Background:
    Given the user accessing base URI

  Scenario: Validate the response message when deleting employee from system
    When the user making DELETE call "/employee/{employeeName}" with "BretTest"
    Then the user should see the response code as "200"
    Then validate the response message "Deleted successful" for employee logged into system

  Scenario: Validate the error message when deleting an employee with invalid name
    When the user making DELETE call "/employee/{employeeName}" with "BretT"
    Then the user should see the response code as "400"
    Then validate the error message "Invalid employeename supplied"

  Scenario: Validate the error message to get employee details with invalid name
    When the user making DELETE call "/employee/{employeeName}" with "Testing"
    Then the user should see the response code as "404"
    Then validate the error message "employee not found"