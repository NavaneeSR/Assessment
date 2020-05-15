@smoke
Feature: Employee Service Validation for LogIn

  Background:
    Given the user accessing base URI

 Scenario: Validate the response message when an employee is logged into system
 When the user making GET call "/employee/login" with "BretTest" and "pass123"
 Then the user should see the response code as "200"
 Then validate the response message "Login successful" for employee logged into system

 Scenario: Validate the error message when an employee is logged into system with invalid name
 When the user making GET call "/employee/login" with "BretT" and "pass123"
 Then the user should see the response code as "400"
 Then validate the error message "Invalid employeename/password supplied"

 Scenario: Validate the error message when an employee is not available
 When the user making GET call "/employee/login" with "Testing" and "pass123"
 Then the user should see the response code as "404"
 Then validate the error message "employee not found"