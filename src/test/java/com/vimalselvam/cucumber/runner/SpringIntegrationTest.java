package com.vimalselvam.cucumber.runner;

import com.SpringBootCucumberApplication;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = SpringBootCucumberApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpringIntegrationTest {

    protected RequestSpecification request;
    protected Response response;
    public static WireMockServer wireMockServer;
}