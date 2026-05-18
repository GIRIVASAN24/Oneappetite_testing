package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.CreateAccountPage;
import com.cts.mfrp.oneappetite.pages.LoginPage;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseTest {

    @Test(description = "TC 2.1.1 - Application URL loads the Login page correctly", groups = "smoke")
    public void loginPageLoadsSuccessfully() {
        LoginPage page = new LoginPage(driver);
        Assert.assertTrue(page.isLoaded(), "Login page elements not loaded");
        Assert.assertTrue(page.headingText().contains(AppConstants.LOGIN_HEADING),
                "Heading text mismatch");
        Assert.assertTrue(page.taglineText().contains(AppConstants.LOGIN_TAGLINE),
                "Tagline text mismatch");
    }

    @Test(description = "TC 2.1.2 - Unauthenticated user accessing /dashboard is redirected to Login")
    public void unauthenticatedRouteRedirectsToLogin() {
        driver.get(ConfigReader.get("base.url") + AppConstants.ROUTE_DASHBOARD);
        Assert.assertTrue(WaitUtils.urlContains(driver, "login"),
                "Did not redirect to /login when accessing protected route while logged out");
        Assert.assertTrue(new LoginPage(driver).isLoaded(),
                "Login page not displayed after redirect");
    }

    @Test(description = "TC 2.1.3 - Login page displays all expected UI elements")
    public void loginPageUiCompleteness() {
        LoginPage page = new LoginPage(driver);
        Assert.assertTrue(page.headingText().contains(AppConstants.LOGIN_HEADING));
        Assert.assertEquals(page.emailPlaceholder(), "you@cognizant.com",
                "Email placeholder unexpected");
        Assert.assertEquals(page.passwordInputType(), "password",
                "Password should be masked by default");
        Assert.assertTrue(page.eyeIconVisible(), "Eye icon missing");
        for (String role : new String[]{
                AppConstants.EMPLOYEE_ROLE, AppConstants.VENDOR_ROLE, AppConstants.ADMIN_ROLE}) {
            Assert.assertTrue(WaitUtils.textVisible(driver, role),
                    "Role chip missing: " + role);
        }
        Assert.assertTrue(page.forgotPasswordVisible(), "Forgot password link missing");
        Assert.assertTrue(page.signInButtonVisible(), "Sign In button missing");
        Assert.assertTrue(page.googleSocialVisible() || page.microsoftSocialVisible(),
                "Social login placeholders missing");
        Assert.assertTrue(page.createAccountVisible(), "Create one now link missing");
    }

    @Test(description = "TC 2.1.4 - Submitting empty form shows inline validation errors")
    public void emptyFormShowsValidationErrors() {
        LoginPage page = new LoginPage(driver);
        page.clickSignIn();
        Assert.assertTrue(page.emailErrorVisible(), "Email error not shown");
        Assert.assertTrue(page.passwordErrorVisible(), "Password length error not shown");
        Assert.assertTrue(page.roleErrorVisible(), "Role required error not shown");
        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "User should remain on login page");
    }

    @Test(description = "TC 2.1.5 - Email with invalid domain is rejected")
    public void invalidEmailDomainIsRejected() {
        LoginPage page = new LoginPage(driver);
        page.enterEmail(ConfigReader.get("invalid.email"))
                .enterPassword("Test@1234")
                .selectRole(AppConstants.EMPLOYEE_ROLE);
        page.clickSignIn();
        Assert.assertTrue(page.domainErrorVisible(),
                "Domain validation error not shown");
        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "User should remain on login page");
    }

    @Test(description = "TC 2.1.6 - Short password shows 'Must be at least 8 characters.'")
    public void shortPasswordShowsLengthError() {
        LoginPage page = new LoginPage(driver);
        page.enterEmail("testuser@cognizant.com")
                .enterPassword(ConfigReader.get("short.password"))
                .selectRole(AppConstants.EMPLOYEE_ROLE);
        page.clickSignIn();
        Assert.assertTrue(page.passwordErrorVisible(),
                "Password length error not shown");
        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "User should remain on login page");
    }

    @Test(description = "TC 2.1.7 - Role mismatch login is rejected with role-mismatch message",
            dependsOnMethods = "loginPageLoadsSuccessfully", alwaysRun = true)
    public void roleMismatchIsRejected() {
        LoginPage page = new LoginPage(driver);
        page.enterEmail(ConfigReader.get("employee.email"))
                .enterPassword(ConfigReader.get("employee.password"))
                .selectRole(AppConstants.VENDOR_ROLE);
        page.clickSignIn();
        Assert.assertTrue(
                page.roleMismatchErrorVisible()
                        || WaitUtils.textVisible(driver, "registered as employee"),
                "Role mismatch error not displayed");
    }

    @Test(description = "TC 2.1.8a - Valid Employee login routes to /dashboard")
    public void validEmployeeLoginRoutesToDashboard() {
        LoginPage page = new LoginPage(driver);
        page.enterEmail(ConfigReader.get("employee.email"))
                .enterPassword(ConfigReader.get("employee.password"))
                .selectRole(AppConstants.EMPLOYEE_ROLE);
        page.clickSignIn();
        Assert.assertTrue(WaitUtils.urlContains(driver, AppConstants.ROUTE_DASHBOARD),
                "Did not navigate to /dashboard");
    }

    @Test(description = "TC 2.1.8b - Valid Vendor login routes to /vendor/dashboard")
    public void validVendorLoginRoutesToVendorDashboard() {
        LoginPage page = new LoginPage(driver);
        page.enterEmail(ConfigReader.get("vendor.email"))
                .enterPassword(ConfigReader.get("vendor.password"))
                .selectRole(AppConstants.VENDOR_ROLE);
        page.clickSignIn();
        Assert.assertTrue(WaitUtils.urlContains(driver, AppConstants.ROUTE_VENDOR_DASHBOARD),
                "Did not navigate to vendor dashboard");
    }

    @Test(description = "TC 2.1.8c - Valid Admin login routes to /admin/dashboard")
    public void validAdminLoginRoutesToAdminDashboard() {
        LoginPage page = new LoginPage(driver);
        page.enterEmail(ConfigReader.get("admin.email"))
                .enterPassword(ConfigReader.get("admin.password"))
                .selectRole(AppConstants.ADMIN_ROLE);
        page.clickSignIn();
        Assert.assertTrue(WaitUtils.urlContains(driver, AppConstants.ROUTE_ADMIN_DASHBOARD),
                "Did not navigate to admin dashboard");
    }

    @Test(description = "TC 2.1.9 - Password eye icon toggles visibility")
    public void passwordEyeIconToggles() {
        LoginPage page = new LoginPage(driver);
        page.enterPassword("TestPass@1");
        Assert.assertEquals(page.passwordInputType(), "password",
                "Password should start masked");
        page.togglePasswordVisibility();
        Assert.assertEquals(page.passwordInputType(), "text",
                "Password should be revealed after toggle");
        page.togglePasswordVisibility();
        Assert.assertEquals(page.passwordInputType(), "password",
                "Password should be masked again");
    }

    @Test(description = "TC 2.1.10 - 'Create one now' link navigates to Create Account page")
    public void createOneNowNavigatesToCreateAccount() {
        LoginPage page = new LoginPage(driver);
        CreateAccountPage create = page.openCreateAccount();
        Assert.assertTrue(create.isLoaded(),
                "Create Account page did not load after clicking 'Create one now'");
        Assert.assertTrue(create.headingText().contains(AppConstants.CREATE_ACCOUNT_HEADING));
    }
}
