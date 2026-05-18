package com.cts.mfrp.oneappetite.tests.support;

import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.LoginPage;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class LoginHelper {

    private LoginHelper() {}


    public static void gotoViaSidebar(WebDriver driver, String routerPath) {
    WebDriverWait w = new WebDriverWait(driver,
            Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds", 20)));
    By byHref = By.cssSelector("a.nav-item[href$='" + routerPath + "']");
    try {
        w.until(ExpectedConditions.elementToBeClickable(byHref)).click();
    } catch (Exception ignored) {
        String label = routerPath.substring(routerPath.lastIndexOf('/') + 1);
        String pretty = Character.toUpperCase(label.charAt(0)) + label.substring(1);
        By byText = By.xpath("//aside//a[contains(@class,'nav-item')]"
                + "[contains(normalize-space(.),'" + pretty + "')]");
        w.until(ExpectedConditions.elementToBeClickable(byText)).click();
    }
    w.until(ExpectedConditions.urlContains(routerPath));
}
    public static void loginAs(WebDriver driver, String role, String overridePassword) {
        String emailKey, passKey, route;
        switch (role) {
            case AppConstants.VENDOR_ROLE -> {
                emailKey = "vendor.email"; passKey = "vendor.password";
                route = AppConstants.ROUTE_VENDOR_DASHBOARD;
            }
            case AppConstants.ADMIN_ROLE -> {
                emailKey = "admin.email"; passKey = "admin.password";
                route = AppConstants.ROUTE_ADMIN_DASHBOARD;
            }
            default -> {
                emailKey = "employee.email"; passKey = "employee.password";
                route = AppConstants.ROUTE_DASHBOARD;
            }
        }
        String password = overridePassword != null ? overridePassword : ConfigReader.get(passKey);
        LoginPage page = new LoginPage(driver);
        page.enterEmail(ConfigReader.get(emailKey))
                .enterPassword(password)
                .selectRole(role);
        page.clickSignIn();
        WaitUtils.urlContains(driver, route);
    }
    public static void loginAs(WebDriver driver, String role) {
        String emailKey, passKey, route;
        switch (role) {
            case AppConstants.VENDOR_ROLE -> {
                emailKey = "vendor.email"; passKey = "vendor.password";
                route = AppConstants.ROUTE_VENDOR_DASHBOARD;
            }
            case AppConstants.ADMIN_ROLE -> {
                emailKey = "admin.email"; passKey = "admin.password";
                route = AppConstants.ROUTE_ADMIN_DASHBOARD;
            }
            default -> {
                emailKey = "employee.email"; passKey = "employee.password";
                route = AppConstants.ROUTE_DASHBOARD;
            }
        }
        LoginPage page = new LoginPage(driver);
        page.enterEmail(ConfigReader.get(emailKey))
                .enterPassword(ConfigReader.get(passKey))
                .selectRole(role);
        page.clickSignIn();
        if (!WaitUtils.urlContains(driver, route)) {
            throw new IllegalStateException(
                    "Login failed for role '" + role + "'. Expected URL to contain '" + route
                    + "', but was '" + driver.getCurrentUrl() + "'. "
                    + "Check '" + emailKey + "' and '" + passKey
                    + "' in config.properties — the account credentials may be out of sync.");
        }
    }
}
