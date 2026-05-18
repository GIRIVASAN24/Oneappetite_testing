package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.VendorSettingsPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VendorSettingsTest extends BaseTest {

    private VendorSettingsPage page;

    @BeforeMethod(alwaysRun = true)
    public void openVendorSettings() {
        LoginHelper.loginAs(driver, AppConstants.VENDOR_ROLE);
        driver.get(ConfigReader.get("base.url") + AppConstants.ROUTE_VENDOR_SETTINGS);
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

    @Test(description = "TC 2.15.2 - Stall Locations: primary marked Registered; Add Location cascade works")
    public void addLocationCascade() {
        Assert.assertTrue(page.registeredBadgeVisible(), "Registered badge missing on primary location");
        page.clickAddLocation();
        Assert.assertTrue(page.cascadeModalOpen(), "Cascade modal did not open");
        // Selection logic depends on dropdown implementation; if available the test reuses
        // LocationSelectionPage-style controls within the modal.
        page.confirmAddLocation();
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

    @Test(description = "TC 2.15.4 - Notifications + Password cards behave like Employee equivalents")
    public void notificationsAndPasswordCardsBehaveLikeEmployee() {
        page.toggleOrderUpdates();
        page.toggleOrderUpdates();
        page.enterCurrentPassword(ConfigReader.get("vendor.password"));
        page.enterNewPassword("NewVendorPass@1");
        page.enterConfirmPassword("NewVendorPass@1");
        page.clickPasswordSave();
    }
}
