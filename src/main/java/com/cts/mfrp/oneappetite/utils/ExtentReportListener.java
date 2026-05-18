package com.cts.mfrp.oneappetite.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExtentReportListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    private static String pageNameFor(ITestResult result) {
        String className = result.getMethod().getRealClass().getSimpleName();
        return className.endsWith("Test") ? className.substring(0, className.length() - 4) : className;
    }

    private static ExtentReports reportFor(ITestResult result) {
        return ExtentManager.forPage(pageNameFor(result));
    }

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest t = reportFor(result).createTest(
                result.getMethod().getMethodName(),
                result.getMethod().getDescription());
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) t.assignCategory(groups);
        TEST.set(t);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TEST.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TEST.get().log(Status.FAIL, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TEST.get().log(Status.SKIP, "Skipped: " +
                (result.getThrowable() != null ? result.getThrowable().getMessage() : ""));
    }

    @Override
    public void onFinish(ITestContext context) {
        for (ExtentReports r : ExtentManager.all()) {
            r.flush();
        }
    }

    public static ExtentTest current() {
        return TEST.get();
    }
}
