@smoke
Feature: Employee Service Validation for create employee details

  Background:
    Given the user accessing base URI

  Scenario Outline: Validate the response message for creating an employee
    When the user making a POST call "/employee/createEmployee" with employee details "<id>", "<employeeName>", "<firstName>", "<lastName>", "<email>", "<password>", "<phone>", "<employeeStatus>"
    Then the user should see the response code as "200"
    Then validate the response for the created employee details
    Examples:
      | id | employeeName | firstName | lastName | email           | password | phone      | employeeStatus |
      | 1  | BretTest     | Bret      | Test     | test1@gmail.com | pass123  | 1287457450 | 0              |

  Scenario: Validate the response message for creating an multiple employee records
    When the user making a POST call "/employee/createWithArray" request with list of employees detail
      | id | employeeName | firstName | lastName | email           | password | phone      | employeeStatus |
      | 1  | BretTest     | Bret      | Test     | test1@gmail.com | pass123  | 1287457450 | 0              |
    Then the user should see the response code as "200"
    Then validate the response for employees details

  Scenario: Validate the response message for creating an multiple employee records
    When the user making a POST call "/employee/createWithList" request with list of employees detail
      | id | employeeName | firstName | lastName | email           | password | phone      | employeeStatus |
      | 1  | BretTest     | Bret      | Test     | test1@gmail.com | pass123  | 1287457450 | 0              |
      | 2  | Bret1Test1   | Bret1     | Test1    | test2@gmail.com | pass123  | 1287457450 | 0              |
    Then the user should see the response code as "200"
    Then validate the response for employees details