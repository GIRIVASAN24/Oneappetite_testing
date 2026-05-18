package com.cts.mfrp.oneappetite.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class ExtentManager {

    private static final String STAMP =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
    private static final ConcurrentMap<String, ExtentReports> REPORTS = new ConcurrentHashMap<>();
    private static final String REPORT_DIR = "test-output/extent";

    private ExtentManager() {}

    /** One ExtentReports instance per page (test class), lazily created. */
    public static ExtentReports forPage(String pageName) {
        return REPORTS.computeIfAbsent(pageName, ExtentManager::buildReporter);
    }

    /** All page-scoped reports created so far — call .flush() on each at end of suite. */
    public static Collection<ExtentReports> all() {
        return REPORTS.values();
    }

    private static synchronized ExtentReports buildReporter(String pageName) {
        File dir = new File(REPORT_DIR);
        if (!dir.exists()) dir.mkdirs();
        String path = dir.getAbsolutePath() + "/" + pageName + "-" + STAMP + ".html";
        ExtentSparkReporter spark = new ExtentSparkReporter(path);
        spark.config().setTheme(Theme.STANDARD);
        spark.config().setDocumentTitle("OneAppetite - " + pageName);
        spark.config().setReportName(pageName + " Page Tests");
        ExtentReports r = new ExtentReports();
        r.attachReporter(spark);
        r.setSystemInfo("Page", pageName);
        r.setSystemInfo("Base URL", ConfigReader.get("base.url"));
        r.setSystemInfo("Browser", ConfigReader.get("browser", "chrome"));
        r.setSystemInfo("Headless", String.valueOf(ConfigReader.getBoolean("headless", false)));
        return r;
    }
}
