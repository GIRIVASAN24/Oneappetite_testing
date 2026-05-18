package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.ForgotPasswordModal;
import com.cts.mfrp.oneappetite.pages.LoginPage;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class ForgotPasswordTest extends BaseTest {

    @Test(description = "TC 2.2.1 - Forgot password link opens modal at Step 1 without navigation")
    public void forgotPasswordOpensModal() {
        LoginPage login = new LoginPage(driver);
        ForgotPasswordModal modal = login.openForgotPassword();
        Assert.assertTrue(modal.isModalOpen(), "Modal should be open");
        Assert.assertTrue(driver.getCurrentUrl().contains("login"),
                "URL must remain /login when modal opens");
        Assert.assertTrue(modal.isStep1Visible(),
                "Step 1 with phone input + Send OTP button should be visible");
    }

    @Test(description = "TC 2.2.2 - Phone fewer than 10 digits shows inline validation error")
    public void shortPhoneShowsValidationError() {
        ForgotPasswordModal modal = new LoginPage(driver).openForgotPassword();
        modal.enterPhone(ConfigReader.get("short.phone"));
        modal.clickSendOtp();
        Assert.assertTrue(modal.phoneErrorVisible() || WaitUtils.textVisible(driver, "10 digits"),
                "Phone validation error not shown");
        Assert.assertTrue(modal.isStep1Visible(), "Modal should remain at Step 1");
    }

    @Test(description = "TC 2.2.3 - Valid 10-digit phone advances to Step 2 with success toast")
    public void validPhoneAdvancesToStep2() {
        ForgotPasswordModal modal = new LoginPage(driver).openForgotPassword();
        modal.enterPhone(ConfigReader.get("valid.phone"));
        modal.clickSendOtp();
        Assert.assertTrue(WaitUtils.textVisible(driver, AppConstants.TOAST_OTP_SENT),
                "OTP toast not shown");
        Assert.assertTrue(modal.isStep2Visible(), "Modal should advance to Step 2");
    }

    @Test(description = "TC 2.2.4 - Wrong number returns to Step 1 and Resend OTP works on Step 2")
    public void wrongNumberAndResend() {
        ForgotPasswordModal modal = new LoginPage(driver).openForgotPassword();
        modal.enterPhone(ConfigReader.get("valid.phone"));
        modal.clickSendOtp();
        Assert.assertTrue(modal.isStep2Visible());
        modal.clickWrongNumber();
        Assert.assertTrue(modal.isStep1Visible(), "Should return to Step 1");
        modal.enterPhone(ConfigReader.get("valid.phone"));
        modal.clickSendOtp();
        Assert.assertTrue(modal.isStep2Visible());
        modal.clickResendOtp();
        Assert.assertTrue(WaitUtils.textVisible(driver, AppConstants.TOAST_OTP_SENT),
                "Toast not re-emitted on Resend OTP");
    }

    @Test(description = "TC 2.2.5 - Incorrect OTP shows 'Invalid or expired OTP' message")
    public void invalidOtpShowsError() {
        ForgotPasswordModal modal = new LoginPage(driver).openForgotPassword();
        modal.enterPhone(ConfigReader.get("valid.phone"));
        modal.clickSendOtp();
        Assert.assertTrue(modal.isStep2Visible());
        modal.enterOtp("000000");
        modal.clickVerify();
        Assert.assertTrue(modal.otpErrorVisible() || WaitUtils.textVisible(driver, "Invalid or expired OTP"),
                "Invalid OTP error not shown");
        Assert.assertFalse(modal.isStep3Visible(),
                "Modal must not advance to Step 3 on invalid OTP");
    }

    // TC 2.2.6 - Valid OTP + new password completes the reset flow.
    // Disabled by default because the test environment does not deliver real OTPs.
    @Test(description = "TC 2.2.6 - Valid OTP + new password resets password", enabled = false)
    public void resetPasswordHappyPath() {
    }
}
