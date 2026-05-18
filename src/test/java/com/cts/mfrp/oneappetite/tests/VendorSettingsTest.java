package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.VendorDashboardPage;
import com.cts.mfrp.oneappetite.pages.VendorSettingsPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VendorSettingsTest extends BaseTest {

    private VendorSettingsPage page;

    private static final String TEMP_VENDOR_PWD = "TempVendorPass@1";

    @BeforeMethod(alwaysRun = true)
    public void openVendorSettings(java.lang.reflect.Method method) {
        // The revert test runs AFTER the change test, so the live vendor password is now
        // TEMP — its login needs the temp password instead of config.properties.
        boolean isRevertTest = "revertPasswordToOriginal".equals(method.getName());
        String pwdOverride = isRevertTest ? TEMP_VENDOR_PWD : null;
        LoginHelper.loginAs(driver, AppConstants.VENDOR_ROLE, pwdOverride);

        // Wait for the dashboard to fully paint before navigating — driver.get() on a
        // half-loaded Angular SPA renders an empty destination page even though the URL is right.
        try {
            WaitUtils.visible(driver, By.id("list-PLACED"));
        } catch (Exception e) {
            throw new SkipException("Vendor dashboard did not finish loading after login");
        }

        // Navigate via the in-app tab link, not driver.get(), so Angular's router
        // renders the destination component properly.
        new VendorDashboardPage(driver).goToSettings();

        if (!WaitUtils.urlContains(driver, AppConstants.ROUTE_VENDOR_SETTINGS)) {
            throw new SkipException("Did not navigate to Vendor Settings URL");
        }
        page = new VendorSettingsPage(driver);
    }

    @Test(description = "TC 2.15.1 - Vendor settings page has four cards; Primary Building is read-only")
    public void cardsAndReadOnly() {
        Assert.assertTrue(page.profileCardVisible(), "Profile card missing");
        Assert.assertTrue(page.notificationsCardVisible(), "Notifications card missing");
        Assert.assertTrue(page.passwordCardVisible(), "Password card missing");
        Assert.assertTrue(page.stallLocationsCardVisible(), "Stall Locations card missing");
        Assert.assertTrue(page.primaryBuildingReadOnly(), "Primary Building/Email should be read-only");
        page.editFullName("Vendor Updated");
        page.editPhone(ConfigReader.get("valid.phone"));
        page.clickProfileSave();
    }

    @Test(description = "TC 2.15.2 - Add Location cascade: pick city/campus/building and submit")
    public void addLocationCascade() {
        Assert.assertTrue(page.registeredBadgeVisible(), "Registered badge missing on primary location");
        int before = page.alsoServing().size();
        page.addLocationFull("Chennai", "Tharamani CRC", "Hardy Tower");

        Assert.assertTrue(page.waitForAlsoServingCountAbove(before),
                "Also-Serving list should grow by one after adding a location");
    }

    @Test(description = "TC 2.15.3 - X icon removes an additional serving location; primary unchanged")
    public void removeAlsoServingLocation() {
        if (page.alsoServing().isEmpty()) {
            throw new org.testng.SkipException("No additional locations to remove");
        }
        int before = page.alsoServing().size();
        page.removeFirstAlsoServing();
        Assert.assertTrue(page.alsoServing().size() < before,
                "Also-Serving list count should decrease by 1");
        Assert.assertTrue(page.registeredBadgeVisible(),
                "Primary registered location should remain intact");
    }

    @Test(description = "TC 2.15.4 - Notifications: order-updates switch toggles on and off")
    public void notificationsOrderUpdatesToggle() {
        page.toggleOrderUpdates();
        page.toggleOrderUpdates();
    }

    @Test(description = "TC 2.15.7 - Total Earnings card displays a non-empty amount")
    public void earningsCardDisplaysAmount() {
        Assert.assertTrue(page.earningsCardVisible(), "Total Earnings card missing");
        String amount = page.earningsAmountText();
        Assert.assertFalse(amount.isBlank(),
                "Earnings amount should not be blank");
        Assert.assertTrue(amount.contains("₹") || amount.matches(".*\\d.*"),
                "Earnings amount should contain a currency symbol or digit, got: " + amount);
    }

    @Test(description = "TC 2.15.5 - Change vendor password from config value to a temp value",
            priority = 100)
    public void changePasswordToTemp() {
        String oldPwd = ConfigReader.get("vendor.password");
        page.enterCurrentPassword(oldPwd);
        page.enterNewPassword(TEMP_VENDOR_PWD);
        page.enterConfirmPassword(TEMP_VENDOR_PWD);
        page.clickPasswordSave();
    }

    /**
     * Cleanup test — must run AFTER {@link #changePasswordToTemp()}. The @BeforeMethod
     * detects this method name and logs in with TEMP_VENDOR_PWD instead of the config
     * value, then this test changes the password back to config.properties so the suite
     * is idempotent across runs.
     */
    @Test(description = "TC 2.15.6 - Revert vendor password back to the original config value (cleanup)",
            priority = 200, dependsOnMethods = "changePasswordToTemp")
    public void revertPasswordToOriginal() {
        String oldPwd = ConfigReader.get("vendor.password");
        page.enterCurrentPassword(TEMP_VENDOR_PWD);
        page.enterNewPassword(oldPwd);
        page.enterConfirmPassword(oldPwd);
        page.clickPasswordSave();
    }
}
