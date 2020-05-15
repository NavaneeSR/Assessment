package com.rabo.controller;

import com.rabo.util.Utility;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Properties;

@RestController
public class EmployeeController extends Utility {

    @RequestMapping("/employee")
    public String index() throws IOException {
        Properties propValue = readPropertiesFile("output/result.properties");
        if (Integer.parseInt(propValue.getProperty("Passed :")) != 0 && Integer.parseInt(propValue.getProperty("Failed :")) == 0) {
            return "Hello, all API tests were successful";
        } else {
            return "Hello, all API tests were not successful";
        }
    }
}
