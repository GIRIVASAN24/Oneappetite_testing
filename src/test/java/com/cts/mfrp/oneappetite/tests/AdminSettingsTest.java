package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.AdminSettingsPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

public class AdminSettingsTest extends BaseTest {

    @Test(description = "TC 2.17.1 - Admin settings has 3 cards, no Wallet card; profile editable")
    public void adminSettingsStructure() {
        LoginHelper.loginAs(driver, AppConstants.ADMIN_ROLE);
        LoginHelper.gotoViaSidebar(driver, AppConstants.ROUTE_ADMIN_SETTINGS);
        AdminSettingsPage page = new AdminSettingsPage(driver);
        Assert.assertTrue(page.profileCardVisible(),       "Profile card missing");
        Assert.assertTrue(page.notificationsCardVisible(), "Notifications card missing");
        Assert.assertTrue(page.passwordCardVisible(),      "Password card missing");
        Assert.assertFalse(page.walletCardVisible(),
                "Admin settings must not display the Wallet card (employee-only feature)");
        page.editFullName("Admin Updated");
        page.editPhone(ConfigReader.get("valid.phone"));
        page.clickProfileSave();
        page.toggleAdminAlerts();
    }

    @Test(description = "TC 2.17.2 - Password card rejects wrong current and accepts correct one")
    public void passwordCardFlow() {
        String original = ConfigReader.get("admin.password");
        String temp = "NewAdmin@1";
        LoginHelper.loginAs(driver, AppConstants.ADMIN_ROLE);
        LoginHelper.gotoViaSidebar(driver, AppConstants.ROUTE_ADMIN_SETTINGS);
        AdminSettingsPage page = new AdminSettingsPage(driver);

        boolean rotated = false;
        try {
            page.enterCurrentPassword("WrongPass@1");
            page.enterNewPassword(temp);
            page.enterConfirmPassword(temp);
            page.clickPasswordSave();
            Assert.assertTrue(WaitUtils.textVisible(driver, "incorrect")
                            || WaitUtils.textVisible(driver, "Current password"),
                    "Error not shown on wrong current password");

            page.enterCurrentPassword(original);
            page.enterNewPassword(temp);
            page.enterConfirmPassword(temp);
            page.clickPasswordSave();
            rotated = true;
        } finally {
            if (rotated) {
                try {
                    driver.manage().deleteAllCookies();
                    driver.get(ConfigReader.get("base.url"));
                    LoginHelper.loginAs(driver, AppConstants.ADMIN_ROLE, temp);
                    LoginHelper.gotoViaSidebar(driver, AppConstants.ROUTE_ADMIN_SETTINGS);
                    AdminSettingsPage revertPage = new AdminSettingsPage(driver);
                    revertPage.enterCurrentPassword(temp);
                    revertPage.enterNewPassword(original);
                    revertPage.enterConfirmPassword(original);
                    revertPage.clickPasswordSave();
                } catch (Exception revertEx) {
                    System.err.println("WARN: password revert failed; admin password may now be '"
                            + temp + "'. Update config.properties or reset manually. Cause: "
                            + revertEx.getMessage());
                }
            }
        }
    }

    // TODO: re-enable once the frontend deploys SPA fallback on Render (rewrite /* -> /index.html).
    // Direct driver.get('/admin/settings') currently serves a blank page because Render doesn't
    // route unknown paths to index.html, so Angular never bootstraps and the route guard cannot run.
    @Test(enabled = false,
          description = "TC 2.17.3 - Admin settings is role-gated for non-admin users")
    public void roleGuardOnAdminSettings() {
        LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);
        driver.get(ConfigReader.get("base.url") + AppConstants.ROUTE_ADMIN_SETTINGS);
        boolean redirected = WaitUtils.urlContains(driver, AppConstants.ROUTE_DASHBOARD)
                || WaitUtils.urlContains(driver, "403")
                || WaitUtils.urlContains(driver, "unauthorized");
        Assert.assertTrue(redirected,
                "Non-admin users should be redirected away from /admin/settings");
    }
}
 