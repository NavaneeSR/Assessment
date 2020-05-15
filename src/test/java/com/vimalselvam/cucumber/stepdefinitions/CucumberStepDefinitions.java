package com.vimalselvam.cucumber.stepdefinitions;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.rabo.model.Employee;
import com.rabo.model.ErrorMessage;
import com.rabo.model.SuccessMessage;
import com.vimalselvam.cucumber.runner.SpringIntegrationTest;
import cucumber.api.DataTable;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import io.restassured.RestAssured;
import org.springframework.http.HttpStatus;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class CucumberStepDefinitions extends SpringIntegrationTest {

    List<Employee> employeeList = new ArrayList<>();

    @Before
    public static void stubSetUp() {

        //Configure WireMock Server
        wireMockServer = new WireMockServer(wireMockConfig().port(8082));
        wireMockServer.start();
        WireMock.configureFor("localhost", 8082);

        /*Create an Employee*/
        stubFor(post(urlEqualTo("/employee/createEmployee")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-response.json")));

        stubFor(post(urlEqualTo("/employee/createWithArray")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-array-req.json")));

        stubFor(post(urlEqualTo("/employee/createWithList")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-list-req.json")));

        /*Login of Employee*/
        stubFor(get(urlEqualTo("/employee/login?employeeName=BretTest&password=pass123")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-login-success.json")));

        stubFor(get(urlEqualTo("/employee/login?employeeName=BretT&password=pass123")).willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-bad-request.json")));

        stubFor(get(urlEqualTo("/employee/login?employeeName=Testing&password=pass123")).willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-not-found.json")));

        /*Logout of Employee*/
        stubFor(get(urlEqualTo("/employee/logout")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-logout-success.json")));

        /*Getting Employee Details*/
        stubFor(get(urlEqualTo("/employee/BretTest")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-response.json")));

        stubFor(get(urlEqualTo("/employee/BretT")).willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-bad-request1.json")));

        stubFor(get(urlEqualTo("/employee/Testing")).willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-not-found.json")));

        /*Update Employee Details*/
        stubFor(put(urlEqualTo("/employee/BretTest")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-updated-response.json")));

        stubFor(put(urlEqualTo("/employee/BretT")).willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-bad-request1.json")));

        stubFor(put(urlEqualTo("/employee/Testing")).willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-not-found.json")));

        /*Delete Employee Details*/
        stubFor(delete(urlEqualTo("/employee/BretTest")).willReturn(aResponse().withStatus(HttpStatus.OK.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-delete-success.json")));

        stubFor(delete(urlEqualTo("/employee/BretT")).willReturn(aResponse().withStatus(HttpStatus.BAD_REQUEST.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-bad-request1.json")));

        stubFor(delete(urlEqualTo("/employee/Testing")).willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                .withHeader("Content-Type", "application/json").withBodyFile("employee-not-found.json")));
    }

    @After
    public void tearDown(){
        wireMockServer.stop();
    }

    @Given("the user accessing base URI")
    public void the_user_accessing_base_URI() {
        request = RestAssured.with();
        request.baseUri("http://localhost:8082");
    }

    @Then("the user should see the response code as \"([^\"]*)\"")
    public void the_user_should_see_the_response_code_as(Integer statusCode) {
        Assert.assertEquals(statusCode.intValue(), response.getStatusCode());
    }

    @When("the user making a POST call \"([^\"]*)\" with employee details \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\", \"([^\"]*)\"")
    public void the_user_making_a_POST_call_with_employee_details(String resource, Integer id, String empName, String fName, String lName, String email, String password, String phone, Integer empStatus) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        Employee employee = new Employee();
        employee.setId(id);
        employee.setEmployeeName(empName);
        employee.setFirstName(fName);
        employee.setLastName(lName);
        employee.setEmail(email);
        employee.setPassword(password);
        employee.setPhone(phone);
        employee.setEmployeeStatus(empStatus);
        employeeList.add(employee);
        response = request.headers(headers).body(employee).post(resource);
    }

    @When("the user making a POST call \"([^\"]*)\" request with list of employees detail")
    public void the_user_making_a_POST_call_request_with_list_employees_details(String resource, DataTable dataTable) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        List<Map<String, String>> employeesInputList = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> employee1 : employeesInputList) {
            Employee employee = new Employee();
            employee.setId(Integer.parseInt(employee1.get("id")));
            employee.setEmployeeName(employee1.get("employeeName"));
            employee.setFirstName(employee1.get("firstName"));
            employee.setLastName(employee1.get("lastName"));
            employee.setEmail(employee1.get("email"));
            employee.setPassword(employee1.get("password"));
            employee.setPhone(employee1.get("phone"));
            employee.setEmployeeStatus(Integer.parseInt(employee1.get("employeeStatus")));
            employeeList.add(employee);
        }
        response = request.headers(headers).body(employeeList).post(resource);
    }

    @Then("validate the response for the created employee details")
    public void validate_the_response_for_the_employee_details() {
        Employee employeeResponse = response.as(Employee.class);
        for (Employee employee : employeeList) {
            Assert.assertEquals(employee.getId(), employeeResponse.getId());
            Assert.assertEquals(employee.getEmployeeName(), employeeResponse.getEmployeeName());
            Assert.assertEquals(employee.getFirstName(), employeeResponse.getFirstName());
            Assert.assertEquals(employee.getLastName(), employeeResponse.getLastName());
            Assert.assertEquals(employee.getEmail(), employeeResponse.getEmail());
            Assert.assertEquals(employee.getPassword(), employeeResponse.getPassword());
            Assert.assertEquals(employee.getPhone(), employeeResponse.getPhone());
            Assert.assertEquals(employee.getEmployeeStatus(), employeeResponse.getEmployeeStatus());
        }
    }

    @Then("validate the response for employees details")
    public void validate_the_response_for_employees_details() {
        Employee[] employeesResponse = response.as(Employee[].class);
        for (int i = 0; i < employeesResponse.length; i++) {
            Assert.assertEquals(employeeList.get(i).getId(), employeesResponse[i].getId());
            Assert.assertEquals(employeeList.get(i).getEmployeeName(), employeesResponse[i].getEmployeeName());
            Assert.assertEquals(employeeList.get(i).getFirstName(), employeesResponse[i].getFirstName());
            Assert.assertEquals(employeeList.get(i).getLastName(), employeesResponse[i].getLastName());
            Assert.assertEquals(employeeList.get(i).getEmail(), employeesResponse[i].getEmail());
            Assert.assertEquals(employeeList.get(i).getPassword(), employeesResponse[i].getPassword());
            Assert.assertEquals(employeeList.get(i).getPhone(), employeesResponse[i].getPhone());
            Assert.assertEquals(employeeList.get(i).getEmployeeStatus(), employeesResponse[i].getEmployeeStatus());
        }
    }

    @Then("validate the response message \"([^\"]*)\" for employee logged into system")
    public void validate_the_response_message_for_employee_logged_into_system(String message) {
        SuccessMessage successMessage = response.as(SuccessMessage.class);
        Assert.assertEquals(message, successMessage.getSuccessMessage());
    }

    @When("the user making GET call \"([^\"]*)\" with name")
    public void the_user_making_GET_call_with_name(String resource) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        response = request.headers(headers).get(resource);
    }

    @Then("validate the response message \"([^\"]*)\" for employee logged out from system")
    public void validate_the_response_message_for_employee_logged_out_from_system(String message) {
        SuccessMessage successMessage = response.as(SuccessMessage.class);
        Assert.assertEquals(message, successMessage.getSuccessMessage());
    }

    @When("the user making GET call \"([^\"]*)\" employee by name \"([^\"]*)\"")
    public void the_user_making_GET_call_employee_by_name(String resource, String empName) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("employeeName", empName);
        response = request.headers(headers).pathParams(reqParams).get(resource);
    }

    @Then("validate the response for the employee details by name")
    public void validate_the_response_for_the_employee_details_by_name(DataTable dataTable) {
        Employee employee = response.as(Employee.class);
        List<Map<String, String>> employeesInputList = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> employee1 : employeesInputList) {
            Assert.assertEquals(employee.getId(), Integer.parseInt(employee1.get("id")));
            Assert.assertEquals(employee.getEmployeeName(), employee1.get("employeeName"));
            Assert.assertEquals(employee.getFirstName(), employee1.get("firstName"));
            Assert.assertEquals(employee.getLastName(), employee1.get("lastName"));
            Assert.assertEquals(employee.getEmail(), employee1.get("email"));
            Assert.assertEquals(employee.getPassword(), employee1.get("password"));
            Assert.assertEquals(employee.getPhone(), employee1.get("phone"));
            Assert.assertEquals(employee.getEmployeeStatus(), Integer.parseInt(employee1.get("employeeStatus")));
        }
    }

    @When("the user making PUT call \"([^\"]*)\" employee to update the name \"([^\"]*)\"")
    public void theUserMakingPUTCallEmployeeToUpdateTheName(String resource, String empName, DataTable dataTable) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("employeeName", empName);
        Employee employee = new Employee();
        List<Map<String, String>> employeesInputList = dataTable.asMaps(String.class, String.class);
        for (Map<String, String> employee1 : employeesInputList) {
            employee.setId(Integer.parseInt(employee1.get("id")));
            employee.setEmployeeName(employee1.get("employeeName"));
            employee.setFirstName(employee1.get("firstName"));
            employee.setLastName(employee1.get("lastName"));
            employee.setEmail(employee1.get("email"));
            employee.setPassword(employee1.get("password"));
            employee.setPhone(employee1.get("phone"));
            employee.setEmployeeStatus(Integer.parseInt(employee1.get("employeeStatus")));
            employeeList.add(employee);
        }
        response = request.headers(headers).body(employee).pathParams(reqParams).put(resource);
    }

    @When("the user making DELETE call \"([^\"]*)\" with \"([^\"]*)\"")
    public void theUserMakingDELETECallWith(String resource, String empName) {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("employeeName", empName);
        response = request.headers(headers).pathParams(reqParams).delete(resource);
    }

    @Then("validate the error message \"([^\"]*)\"")
    public void validateTheErrorMessage(String message) {
        ErrorMessage errorMessage = response.as(ErrorMessage.class);
        Assert.assertEquals(message, errorMessage.getErrorMessage());
    }

    @When("^the user making GET call \"([^\"]*)\" with \"([^\"]*)\" and \"([^\"]*)\"$")
    public void theUserMakingGETCallWithAnd(String resource, String empName, String password) throws Throwable {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        Map<String, String> reqParams = new HashMap<>();
        reqParams.put("employeeName", empName);
        reqParams.put("password", password);
        response = request.headers(headers).queryParams(reqParams).get(resource);
    }

    @Then("^validate the response for the employee details are updated$")
    public void validateTheResponseForTheEmployeeDetailsAreUpdated() {
        Employee employeeResponse = response.as(Employee.class);
        for (Employee employee : employeeList) {
            Assert.assertEquals(employee.getId(), employeeResponse.getId());
            Assert.assertEquals(employee.getEmployeeName(), employeeResponse.getEmployeeName());
            Assert.assertEquals(employee.getFirstName(), employeeResponse.getFirstName());
            Assert.assertEquals(employee.getLastName(), employeeResponse.getLastName());
            Assert.assertEquals(employee.getEmail(), employeeResponse.getEmail());
            Assert.assertEquals(employee.getPassword(), employeeResponse.getPassword());
            Assert.assertEquals(employee.getPhone(), employeeResponse.getPhone());
            Assert.assertEquals(employee.getEmployeeStatus(), employeeResponse.getEmployeeStatus());
        }
    }
}
