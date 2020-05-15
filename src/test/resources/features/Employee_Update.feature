@smoke
Feature: Employee Service Validation for update employee details

  Background:
    Given the user accessing base URI

  Scenario: Validate the response message to update employee details by Name
    When the user making PUT call "/employee/{employeeName}" employee to update the name "BretTest"
      | id | employeeName | firstName | lastName | email           | password | phone      | employeeStatus |
      | 1  | JohnTest     | Bret2     | Test2    | test3@gmail.com | pass1234 | 1287457451 | 1              |
    Then the user should see the response code as "200"
    Then validate the response for the employee details are updated

  Scenario: Validate the error message to get employee details which is not system
    When the user making PUT call "/employee/{employeeName}" employee to update the name "BretT"
      | id | employeeName | firstName | lastName | email           | password | phone      | employeeStatus |
      | 1  | JohnTest     | Bret2     | Test2    | test3@gmail.com | pass1234 | 1287457451 | 1              |
    Then the user should see the response code as "400"
    Then validate the error message "Invalid employeename supplied"

  Scenario: Validate the error message to get employee details with invalid name
    When the user making PUT call "/employee/{employeeName}" employee to update the name "Testing"
      | id | employeeName | firstName | lastName | email           | password | phone      | employeeStatus |
      | 1  | JohnTest     | Bret2     | Test2    | test3@gmail.com | pass1234 | 1287457451 | 1              |
    Then the user should see the response code as "404"
    Then validate the error message "employee not found"