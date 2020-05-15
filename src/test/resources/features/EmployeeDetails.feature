@smoke
Feature: Employee Service Validation for getting employee details

  Background:
    Given the user accessing base URI

  Scenario: Validate the response message to get employee details by Name
    When the user making GET call "/employee/{employeeName}" employee by name "BretTest"
    Then the user should see the response code as "200"
    Then validate the response for the employee details by name
      | id | employeeName | firstName | lastName | email           | password | phone      | employeeStatus |
      | 1  | BretTest     | Bret      | Test     | test1@gmail.com | pass123  | 1287457450 | 0              |

  Scenario: Validate the error message to get employee details which is not system
    When the user making GET call "/employee/{employeeName}" employee by name "BretT"
    Then the user should see the response code as "400"
    Then validate the error message "Invalid employeename supplied"

  Scenario: Validate the error message to get employee details with invalid name
    When the user making GET call "/employee/{employeeName}" employee by name "Testing"
    Then the user should see the response code as "404"
    Then validate the error message "employee not found"