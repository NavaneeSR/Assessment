package com.vimalselvam.cucumber.listener;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.markuputils.Markup;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.rabo.util.Utility;
import gherkin.formatter.Formatter;
import gherkin.formatter.Reporter;
import gherkin.formatter.model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * A cucumber based reporting listener which generates the Extent Report
 */
public class ExtentCucumberFormatter implements Reporter, Formatter {
    private static ExtentReports extentReports;
    private static ExtentHtmlReporter htmlReporter;
    private static ThreadLocal<ExtentTest> featureTestThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<ExtentTest> scenarioOutlineThreadLocal = new InheritableThreadLocal<>();
    static ThreadLocal<ExtentTest> scenarioThreadLocal = new InheritableThreadLocal<>();
    private static ThreadLocal<LinkedList<Step>> stepListThreadLocal = new InheritableThreadLocal<>();
    static ThreadLocal<ExtentTest> stepTestThreadLocal = new InheritableThreadLocal<>();
    private boolean scenarioOutlineFlag;
    Utility utility;

    private List<Scenario> allScenarioPassed;
    private List<Scenario> anyScenarioFailed;
    private boolean stepFailed;
    private boolean scenarioFailed;
    private Feature feature;

    public ExtentCucumberFormatter(File file) {
        setExtentHtmlReport(file);
        setExtentReport();
        stepListThreadLocal.set(new LinkedList<Step>());
        scenarioOutlineFlag = false;
        allScenarioPassed = new ArrayList<Scenario>();
        anyScenarioFailed = new ArrayList<Scenario>();
    }

    public static void setExtentHtmlReport(File file) {
        if (htmlReporter != null) {
            return;
        }
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        htmlReporter = new ExtentHtmlReporter(file);
    }

    static ExtentHtmlReporter getExtentHtmlReport() {
        return htmlReporter;
    }

    private static void setExtentReport() {
        if (extentReports != null) {
            return;
        }
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);
        //uncomment the below step when DB report is needed
        //extentReports.attachReporter(htmlReporter,getExtentXReporter());
    }

    static ExtentReports getExtentReport() {
        return extentReports;
    }

    public void syntaxError(String state, String event, List<String> legalEvents, String uri, Integer line) {

    }

    public void uri(String uri) {

    }

    public void feature(Feature feature) {
        featureTestThreadLocal.set(getExtentReport().createTest(feature.getName()));
        ExtentTest test = featureTestThreadLocal.get();

        for (Tag tag : feature.getTags()) {
            test.assignCategory(tag.getName());
        }
        stepFailed = false;
        this.feature = feature;
    }

    public void scenarioOutline(ScenarioOutline scenarioOutline) {
        scenarioOutlineFlag = true;
        ExtentTest node = featureTestThreadLocal.get()
                .createNode(scenarioOutline.getKeyword() + ": " + scenarioOutline.getName());
        scenarioOutlineThreadLocal.set(node);
    }

    public void examples(Examples examples) {
        ExtentTest test = scenarioOutlineThreadLocal.get();

        String[][] data = null;
        List<ExamplesTableRow> rows = examples.getRows();
        int rowSize = rows.size();
        for (int i = 0; i < rowSize; i++) {
            ExamplesTableRow examplesTableRow = rows.get(i);
            List<String> cells = examplesTableRow.getCells();
            int cellSize = cells.size();
            if (data == null) {
                data = new String[rowSize][cellSize];
            }
            for (int j = 0; j < cellSize; j++) {
                data[i][j] = cells.get(j);
            }
        }
        test.info(MarkupHelper.createTable(data));
    }

    public void startOfScenarioLifeCycle(Scenario scenario) {
        if (scenarioOutlineFlag) {
            scenarioOutlineFlag = false;
        }

        ExtentTest scenarioNode;
        if (scenarioOutlineThreadLocal.get() != null
                && scenario.getKeyword().trim().equalsIgnoreCase("Scenario Outline")) {
            scenarioNode = scenarioOutlineThreadLocal.get().createNode("Scenario: " + scenario.getName());
        } else {
            scenarioNode = featureTestThreadLocal.get().createNode("Scenario: " + scenario.getName());
        }

        for (Tag tag : scenario.getTags()) {
            scenarioNode.assignCategory(tag.getName());
        }
        scenarioThreadLocal.set(scenarioNode);
    }

    public void background(Background background) {

    }

    public void scenario(Scenario scenario) {

    }

    public void step(Step step) {
        if (scenarioOutlineFlag) {
            return;
        }
        stepListThreadLocal.get().add(step);
    }

    public void endOfScenarioLifeCycle(Scenario scenario) {
        if (scenarioFailed)
            anyScenarioFailed.add(scenario);
        else
            allScenarioPassed.add(scenario);
    }

    public void done() {
        getExtentReport().flush();
    }

    public void close() {
        try (OutputStream output = new FileOutputStream("output/result.properties")) {
            Properties prop = new Properties();

            // set the properties value
            prop.setProperty("Passed :", Integer.toString(allScenarioPassed.size()));
            prop.setProperty("Failed :", Integer.toString(anyScenarioFailed.size()));

            // save properties to project root folder
            prop.store(output, "Results are updated");

        } catch (IOException io) {
            io.printStackTrace();
        }
    }

    public void eof() {

    }

    public void before(Match match, Result result) {

    }

    public void result(Result result) {
        if (!stepFailed && !result.getStatus().equals(Result.PASSED))
            stepFailed = true;

        if (scenarioOutlineFlag) {
            return;
        }

        if (Result.PASSED.equals(result.getStatus())) {
            stepTestThreadLocal.get().pass(Result.PASSED);
        } else if (Result.FAILED.equals(result.getStatus())) {
            stepTestThreadLocal.get().fail(result.getError());
            stepTestThreadLocal.get().fail(result.getStatus());
        } else if (Result.SKIPPED.equals(result)) {
            stepTestThreadLocal.get().skip(Result.SKIPPED.getStatus());
        } else if (Result.UNDEFINED.equals(result)) {
            stepTestThreadLocal.get().skip(Result.UNDEFINED.getStatus());
        }
    }

    public void after(Match match, Result result) {

    }

    public void match(Match match) {
        Step step = stepListThreadLocal.get().poll();
        String data[][] = null;
        if (step.getRows() != null) {
            List<DataTableRow> rows = step.getRows();
            int rowSize = rows.size();
            for (int i = 0; i < rowSize; i++) {
                DataTableRow dataTableRow = rows.get(i);
                List<String> cells = dataTableRow.getCells();
                int cellSize = cells.size();
                if (data == null) {
                    data = new String[rowSize][cellSize];
                }
                for (int j = 0; j < cellSize; j++) {
                    data[i][j] = cells.get(j);
                }
            }
        }

        ExtentTest scenarioTest = scenarioThreadLocal.get();
        ExtentTest stepTest = scenarioTest.createNode(step.getKeyword() + step.getName());

        if (data != null) {
            Markup table = MarkupHelper.createTable(data);
            stepTest.info(table);
        }

        stepTestThreadLocal.set(stepTest);
    }

    public void embedding(String mimeType, byte[] data) {

    }

    public void write(String text) {

    }
}
