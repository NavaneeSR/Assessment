package com.vimalselvam.cucumber.runner;

import com.vimalselvam.cucumber.listener.ExtentProperties;
import com.vimalselvam.cucumber.listener.Reporter;
import cucumber.api.CucumberOptions;
import cucumber.api.testng.AbstractTestNGCucumberTests;
import org.junit.Before;
import org.junit.BeforeClass;
import org.testng.annotations.AfterClass;

import java.io.File;

@CucumberOptions(
        features = {"src/test/resources/features"},
        glue = {"com.vimalselvam.cucumber.stepdefinitions"},
        tags = {"@smoke"},
        plugin = {"com.vimalselvam.cucumber.listener.ExtentCucumberFormatter:output/report.html"}
)
public class CucumberIntegrationTest extends AbstractTestNGCucumberTests {

    @BeforeClass
    public static void setUp() {
        ExtentProperties extentProperties = ExtentProperties.INSTANCE;
        extentProperties.setReportPath("output/report.html");
    }

    @AfterClass
    public static void teardown() {
        Reporter.loadXMLConfig(new File("src/test/resources/extent-config.xml"));
        Reporter.setSystemInfo("user", System.getProperty("user.name"));
        Reporter.setSystemInfo("os", "Mac OSX");
        Reporter.setTestRunnerOutput("Sample test runner output message");
    }
}
