package com.cts.mfrp.oneappetite.tests.support;

import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.LoginPage;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class LoginHelper {

    private LoginHelper() {}


    public static void gotoViaSidebar(WebDriver driver, String routerPath) {
        WebDriverWait longWait = new WebDriverWait(driver,
                Duration.ofSeconds(ConfigReader.getInt("explicit.wait.seconds", 20)));
        // Short, per-candidate probe — avoids burning the full 40s timeout on each miss.
        WebDriverWait probe = new WebDriverWait(driver, Duration.ofSeconds(2));
        // Per-click navigation check — long enough for the SPA route to flip, short enough
        // that clicking a wrong element doesn't stall the whole sequence.
        WebDriverWait navWait = new WebDriverWait(driver, Duration.ofSeconds(5));

        String label = routerPath.substring(routerPath.lastIndexOf('/') + 1);
        String pretty = Character.toUpperCase(label.charAt(0)) + label.substring(1);

        // Try a sequence of selectors covering different sidebar/nav implementations
        // (some pages use <aside>, others <nav> or a header-mounted menu; element may be
        // an <a>, <button>, or routerLink-bound <div>).
        List<By> candidates = List.of(
                By.cssSelector("a.nav-item[href$='" + routerPath + "']"),
                By.cssSelector("a[href$='" + routerPath + "']"),
                By.cssSelector("[routerLink$='" + routerPath + "']"),
                By.cssSelector("[data-route='" + routerPath + "']"),
                By.xpath("//aside//a[contains(@class,'nav-item')]"
                        + "[contains(normalize-space(.),'" + pretty + "')]"),
                By.xpath("//aside//*[self::a or self::button][contains(normalize-space(.),'"
                        + pretty + "')]"),
                By.xpath("//nav//*[self::a or self::button][contains(normalize-space(.),'"
                        + pretty + "')]"),
                By.xpath("//*[self::a or self::button][normalize-space()='" + pretty + "']")
        );

        // Wait once for the nav shell to render, so the first probe isn't racing the SPA.
        try {
            longWait.until(ExpectedConditions.presenceOfElementLocated(
                    By.cssSelector("aside, nav, header")));
        } catch (Exception ignored) {}

        for (By by : candidates) {
            if (tryClick(driver, probe, by) && urlReached(navWait, routerPath)) return;
        }

        // Last resort: profile/avatar menus often hide Settings — open any visible
        // menu trigger then retry the label-based selectors.
        openProfileMenuIfPresent(driver);
        for (By by : candidates) {
            if (tryClick(driver, probe, by) && urlReached(navWait, routerPath)) return;
        }

        throw new IllegalStateException(
                "Could not navigate to '" + routerPath + "' via UI click. "
                + "No sidebar/menu element matched href, routerLink, or label '" + pretty + "'.");
    }

    private static boolean tryClick(WebDriver driver, WebDriverWait probe, By by) {
        try {
            WebElement el = probe.until(ExpectedConditions.elementToBeClickable(by));
            try {
                el.click();
            } catch (Exception clickEx) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean urlReached(WebDriverWait w, String routerPath) {
        try {
            w.until(ExpectedConditions.urlContains(routerPath));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void openProfileMenuIfPresent(WebDriver driver) {
        List<By> triggers = List.of(
                By.cssSelector("button.profile-menu, button.user-menu, button.avatar, button.profile-btn"),
                By.cssSelector("[aria-label*='profile' i], [aria-label*='account' i], [aria-label*='menu' i]"),
                By.cssSelector("img.avatar, .user-avatar, .profile-avatar")
        );
        for (By by : triggers) {
            try {
                WebElement t = driver.findElement(by);
                if (t.isDisplayed()) {
                    try { t.click(); } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", t);
                    }
                    return;
                }
            } catch (Exception ignored) {}
        }
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
