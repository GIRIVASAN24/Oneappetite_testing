package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.EmployeeSettingsPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

public class EmployeeSettingsTest extends BaseTest {

    private static final List<String> EXPECTED_CARD_TITLES =
            Arrays.asList("Profile", "Notifications", "Wallet", "Password");

    private EmployeeSettingsPage settings;

    @BeforeMethod(alwaysRun = true)
    public void openSettings() {
        LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);
        settings = new EmployeeSettingsPage(driver).openFromSidebar();
    }

    /* ====================================================================== */
    /* Header & route                                                         */
    /* ====================================================================== */

    @Test(description = "TC 2.12.1 - Settings page loads with correct route and heading")
    public void pageLoadsWithCorrectRouteAndHeading() {
        Assert.assertTrue(settings.currentUrl().contains(AppConstants.ROUTE_SETTINGS),
                "URL should contain " + AppConstants.ROUTE_SETTINGS + " but was " + settings.currentUrl());
        Assert.assertTrue(settings.isLoaded(), "Settings heading not displayed");
        Assert.assertEquals(settings.getHeading(), "Settings");
    }

    @Test(description = "TC 2.12.2 - Subtitle mentions profile, notifications, password, and wallet")
    public void subtitleMentionsAllFourAreas() {
        String s = settings.getSubtitle().toLowerCase();
        Assert.assertFalse(s.isBlank(), "Subtitle is blank");
        for (String topic : new String[]{"profile", "notifications", "password", "wallet"}) {
            Assert.assertTrue(s.contains(topic),
                    "Subtitle should mention '" + topic + "', got: " + settings.getSubtitle());
        }
    }

    /* ====================================================================== */
    /* Card grid                                                              */
    /* ====================================================================== */

    @Test(description = "TC 2.12.3 - All four settings cards render in the expected order")
    public void allFourCardsRender() {
        Assert.assertEquals(settings.cardCount(), 4,
                "Expected exactly 4 settings cards, got " + settings.cardCount());
        Assert.assertEquals(settings.cardTitles(), EXPECTED_CARD_TITLES,
                "Card titles mismatch. Expected " + EXPECTED_CARD_TITLES + " got " + settings.cardTitles());
    }

    @Test(description = "TC 2.12.4 - Profile / Notifications / Wallet / Password cards all visible")
    public void eachCardIsVisible() {
        Assert.assertTrue(settings.profileCardVisible(),       "Profile card missing");
        Assert.assertTrue(settings.notificationsCardVisible(), "Notifications card missing");
        Assert.assertTrue(settings.walletCardVisible(),        "Wallet card missing");
        Assert.assertTrue(settings.passwordCardVisible(),      "Password card missing");
    }

    /* ====================================================================== */
    /* Profile card                                                           */
    /* ====================================================================== */

    @Test(description = "TC 2.12.5 - Profile card shows EMPLOYEE role badge")
    public void profileRoleBadgeIsEmployee() {
        Assert.assertEquals(settings.roleBadgeText().toUpperCase(), "EMPLOYEE",
                "Profile role badge should read EMPLOYEE");
    }

    @Test(description = "TC 2.12.6 - Email is displayed as read-only text, not an editable input")
    public void emailIsReadOnly() {
        Assert.assertTrue(settings.emailRenderedAsReadOnlyText(),
                "Email should be displayed as static text (span.meta-val), not an editable input");
        String email = settings.profileEmail();
        Assert.assertTrue(email.contains("@"),
                "Profile email should look like an email, got: " + email);
    }

    @Test(description = "TC 2.12.7 - Full name and phone inputs are editable; phone has 10-digit pattern")
    public void nameAndPhoneInputsEditable() {
        Assert.assertTrue(settings.nameInputEditable(),  "Full name input should be editable");
        Assert.assertTrue(settings.phoneInputEditable(), "Phone input should be editable");
        Assert.assertEquals(settings.phonePattern(), "^\\d{10}$",
                "Phone input should enforce 10-digit pattern");
    }

    @Test(description = "TC 2.12.8 - Saving with no changes shows the 'Nothing changed' toast (non-destructive)")
    public void profileSaveNoOpShowsNothingChangedToast() {
        Assert.assertEquals(settings.profileSaveBtnText().toLowerCase(),
                "save changes", "Profile button label should read 'Save changes'");
        // Click Save without modifying anything — should not mutate the account.
        settings.clickProfileSave();
        Assert.assertTrue(settings.waitForToastContaining("Nothing changed"),
                "Expected 'Nothing changed in your profile' toast after a no-op save");
    }

    /* ====================================================================== */
    /* Notifications card                                                     */
    /* ====================================================================== */

    @Test(description = "TC 2.12.9 - Order updates toggle flips state and reverts back to original")
    public void orderUpdatesTogglesState() {
        boolean initial = settings.orderUpdatesOn();
        settings.toggleOrderUpdates();
        Assert.assertNotEquals(settings.orderUpdatesOn(), initial,
                "Toggling Order updates should flip its state");
        settings.toggleOrderUpdates();
        Assert.assertEquals(settings.orderUpdatesOn(), initial,
                "Toggling Order updates again should revert to original state");
    }

    @Test(description = "TC 2.12.9b - Toggling Order updates OFF shows the 'Notifications muted' toast")
    public void togglingOrderUpdatesOffShowsMutedToast() {
        if (!settings.orderUpdatesOn()) {
            // Already off — flip on first so we can test the off transition.
            settings.toggleOrderUpdates();
        }
        settings.toggleOrderUpdates(); // now turning OFF
        Assert.assertTrue(settings.waitForToastContaining("muted"),
                "Expected 'Notifications muted' toast after switching Order updates OFF");
        // Restore original state so we leave the account unchanged.
        settings.toggleOrderUpdates();
    }

    /* ====================================================================== */
    /* Wallet card                                                            */
    /* ====================================================================== */

    @Test(description = "TC 2.12.10 - Wallet shows a balance formatted as ₹N(.NN)?")
    public void walletBalanceFormatted() {
        String balance = settings.walletBalanceText();
        Assert.assertTrue(balance.matches("₹[\\d,]+(\\.\\d{2})?"),
                "Wallet balance should be in ₹N(,NNN)?(.NN)? format, got: " + balance);
    }

    @Test(description = "TC 2.12.11 - Wallet renders exactly the four quick-amount chips ₹100/₹250/₹500/₹1000")
    public void quickChipsRenderExpectedAmounts() {
        Assert.assertEquals(settings.quickChipCount(), 4,
                "Expected 4 quick-amount chips, got " + settings.quickChipCount());
        List<String> expected = Arrays.asList("₹100", "₹250", "₹500", "₹1000");
        Assert.assertEquals(settings.quickChipLabels(), expected,
                "Quick chip labels mismatch. Got: " + settings.quickChipLabels());
    }

    @Test(description = "TC 2.12.12 - Clicking a quick-amount chip marks it active")
    public void clickingQuickChipMakesItActive() {
        settings.clickQuickChip("₹500");
        Assert.assertEquals(settings.activeQuickChipLabel(), "₹500",
                "₹500 chip should be the active one after clicking it");
    }

    @Test(description = "TC 2.12.13 - 'Top up wallet' button is present with the expected label")
    public void topUpButtonPresent() {
        Assert.assertEquals(settings.topUpBtnText().toLowerCase(), "top up wallet",
                "Top up button label should read 'Top up wallet'");
    }
    @Test(description = "TC 2.12.14 - 'Update password' button is disabled until form is filled")
    public void updatePasswordDisabledInitially() {
        Assert.assertFalse(settings.updatePasswordBtnEnabled(),
                "Update password button should be disabled before fields are filled");
        Assert.assertEquals(settings.updatePasswordBtnText().toLowerCase(), "update password",
                "Password submit button label should read 'Update password'");
    }

    @Test(description = "TC 2.12.15 - New password input enforces 6-character minimum length")
    public void newPasswordMinLengthIsSix() {
        Assert.assertEquals(settings.newPasswordMinLength(), 6,
                "New password input should enforce minlength=6 per the UI copy");
    }
    /* ====================================================================== */
    /* Negative validations (no submissions — input state only)               */
    /* ====================================================================== */

    @Test(description = "TC 2.12.17 - Phone field rejects non-numeric input (HTML5 pattern)")
    public void phoneRejectsNonNumeric() {
        settings.editPhone("abcdefghij");
        Assert.assertFalse(settings.profilePhoneValid(),
                "Phone input should be invalid when filled with letters");
    }

    @Test(description = "TC 2.12.18 - Phone field rejects fewer than 10 digits")
    public void phoneRejectsShortNumber() {
        settings.editPhone("12345");
        Assert.assertFalse(settings.profilePhoneValid(),
                "Phone input should be invalid with fewer than 10 digits");
    }

    @Test(description = "TC 2.12.19 - Full name field rejects an empty value (required)")
    public void fullNameRejectsEmpty() {
        settings.editFullName("");
        Assert.assertFalse(settings.profileNameValid(),
                "Full name input should be invalid when empty");
    }

    @Test(description = "TC 2.12.20 - UPI field rejects an entry without '@' (pattern)")
    public void upiRejectsMissingAtSign() {
        settings.enterUpi("notanupi");
        Assert.assertFalse(settings.upiValid(),
                "UPI input should be invalid without an '@' separator");
    }

    @Test(description = "TC 2.12.21 - Custom amount rejects values below min=1")
    public void customAmountRejectsZero() {
        settings.enterCustomAmount("0");
        Assert.assertFalse(settings.customAmountValid(),
                "Custom amount input should be invalid when below the min=1");
    }

    @Test(description = "TC 2.12.22 - Update password button stays disabled when new password is too short")
    public void updateBtnStaysDisabledWithShortNewPassword() {
        settings.enterCurrentPassword("AnyValueHere@1");
        settings.enterNewPassword("abc");
        settings.enterConfirmPassword("abc");
        Assert.assertFalse(settings.updatePasswordBtnEnabled(),
                "Update password button should stay disabled when new password is shorter than 6 chars");
    }

    @Test(description = "TC 2.12.23 - Mismatched new/confirm passwords show inline 'do not match' error on submit")
    public void mismatchShowsInlineError() {
        settings.enterCurrentPassword("AnyValueHere@1");
        settings.enterNewPassword("LongEnough@1");
        settings.enterConfirmPassword("Different@2");
        // Click Update password — the client-side mismatch validator catches it
        // before the request hits the backend, so this stays non-destructive.
        settings.clickPasswordSave();
        boolean errorVisible =
                settings.waitForPasswordErrorContaining("do not match")
                        || com.cts.mfrp.oneappetite.utils.WaitUtils.textVisible(driver, "do not match");
        Assert.assertTrue(errorVisible,
                "Expected inline 'New password and confirmation do not match.' error, "
                        + "got: '" + settings.passwordErrorText() + "'");
    }

    /* ====================================================================== */
    /* Destructive — runs last                                                */
    /* ====================================================================== */

    @Test(priority = 100,
            description = "TC 2.12.16 - Wrong current password produces an error (non-destructive)")
    public void wrongCurrentPasswordProducesError() {
        settings.enterCurrentPassword("DefinitelyWrong@9999");
        settings.enterNewPassword("DummyNew@1234");
        settings.enterConfirmPassword("DummyNew@1234");
        settings.clickPasswordSave();
        Assert.assertTrue(settings.passwordErrorVisible(),
                "Expected an error after submitting a wrong current password");
    }
}
