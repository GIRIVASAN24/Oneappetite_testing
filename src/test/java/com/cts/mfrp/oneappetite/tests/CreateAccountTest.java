package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.CreateAccountPage;
import com.cts.mfrp.oneappetite.pages.LoginPage;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CreateAccountTest extends BaseTest {

    private CreateAccountPage openSignUp() {
        return new LoginPage(driver).openCreateAccount();
    }

    @Test(description = "TC 2.3.1 - Create Account page UI is complete")
    public void createAccountUiComplete() {
        CreateAccountPage page = openSignUp();
        Assert.assertTrue(page.headingText().contains(AppConstants.CREATE_ACCOUNT_HEADING));
        for (String role : new String[]{
                AppConstants.EMPLOYEE_ROLE, AppConstants.VENDOR_ROLE, AppConstants.ADMIN_ROLE}) {
            Assert.assertTrue(WaitUtils.textVisible(driver, role), "Role chip missing: " + role);
        }
        Assert.assertTrue(page.fullNameVisible(), "Full Name field missing");
        Assert.assertTrue(page.workEmailVisible(), "Work Email field missing");
        Assert.assertTrue(page.phoneVisible(), "Phone field missing");
        Assert.assertTrue(page.passwordVisible(), "Password field missing");
        Assert.assertTrue(page.createBtnVisible(), "Create button missing");
        Assert.assertTrue(page.signInLinkVisible(), "Sign in link missing");
    }

    @Test(description = "TC 2.3.2 - Selecting Vendor role reveals Vendor-specific fields")
    public void vendorRoleRevealsExtraFields() {
        CreateAccountPage page = openSignUp();
        page.selectRole(AppConstants.VENDOR_ROLE);
        Assert.assertTrue(page.vendorFieldsVisible(),
                "Vendor Name / Description / Building ID should be visible for Vendor role");
        page.selectRole(AppConstants.EMPLOYEE_ROLE);
        Assert.assertFalse(page.vendorFieldsVisible(),
                "Vendor-only fields must hide when switching back to Employee");
    }

    @Test(description = "TC 2.3.3 - Invalid inputs show appropriate inline errors")
    public void invalidInputsShowErrors() {
        CreateAccountPage page = openSignUp();
        page.selectRole(AppConstants.EMPLOYEE_ROLE);
        page.enterFullName("Test User");
        page.enterWorkEmail("testuser@gmail.com");
        page.enterPhone("9876543210");
        page.enterPassword("ValidPass@1");
        page.clickCreate();
        Assert.assertTrue(page.emailDomainErrorVisible() || WaitUtils.textVisible(driver, "cognizant.com"),
                "Email domain error not shown");

        page.enterWorkEmail("testuser@cognizant.com");
        page.enterPhone("98765");
        page.clickCreate();
        Assert.assertTrue(page.phoneErrorVisible() || WaitUtils.textVisible(driver, "10 digits"),
                "Phone error not shown for short input");

        page.enterPhone("9876543210");
        page.enterPassword("Pass1");
        page.clickCreate();
        Assert.assertTrue(page.passwordErrorVisible(), "Password length error not shown");
        Assert.assertFalse(driver.getCurrentUrl().contains(AppConstants.ROUTE_LOGIN),
                "Should stay on Create Account page when errors are present");
    }

    @Test(description = "TC 2.3.4 - Valid details register the user and route back to Login with toast")
    public void validRegistrationCreatesAccount() {
        CreateAccountPage page = openSignUp();
        page.selectRole(AppConstants.EMPLOYEE_ROLE);
        page.enterFullName("Valid User");
        String uniqueEmail = "newvalid+" + System.currentTimeMillis() + "@cognizant.com";
        page.enterWorkEmail(uniqueEmail);
        page.enterPhone(ConfigReader.get("valid.phone"));
        page.enterPassword("ValidPass@1");
        page.clickCreate();
        Assert.assertTrue(WaitUtils.urlContains(driver, AppConstants.ROUTE_LOGIN),
                "User should be routed back to Login after account creation");
        Assert.assertTrue(WaitUtils.textVisible(driver, AppConstants.TOAST_ACCOUNT_CREATED),
                "Account-created toast not shown");
    }

    @Test(description = "TC 2.3.5 - 'Sign in' link on Create Account navigates back to Login")
    public void signInLinkReturnsToLogin() {
        CreateAccountPage page = openSignUp();
        page.clickSignIn();
        Assert.assertTrue(WaitUtils.urlContains(driver, AppConstants.ROUTE_LOGIN));
        Assert.assertTrue(new LoginPage(driver).isLoaded(), "Login page did not load");
    }
}
