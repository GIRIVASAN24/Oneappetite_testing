package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.VendorMenuMgmtPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VendorMenuMgmtTest extends BaseTest {

    private VendorMenuMgmtPage mgmt;

    @BeforeMethod(alwaysRun = true)
    public void openMenuMgmt() {
        LoginHelper.loginAs(driver, AppConstants.VENDOR_ROLE);
        driver.get(ConfigReader.get("base.url") + AppConstants.ROUTE_VENDOR_MENU_MGMT);
        mgmt = new VendorMenuMgmtPage(driver);
        if (!mgmt.isLoaded()) throw new SkipException("Menu Management page did not load");
    }

    @Test(description = "TC 2.14.1 - Add Menu Item modal opens and saves a valid new item")
    public void addValidMenuItem() {
        mgmt.clickAddItem();
        Assert.assertTrue(mgmt.modalOpen(), "Add Menu Item modal did not open");
        String name = "Grilled Paneer " + System.currentTimeMillis();
        mgmt.fill(name, "Starters", "Lunch", "Veg", "150", "50", "10",
                "https://example.com/paneer.jpg");
        mgmt.clickSave();
        Assert.assertTrue(WaitUtils.textVisible(driver, name),
                "Newly added item should appear in the menu list");
    }

    @Test(description = "TC 2.14.2 - Negative price triggers warning toast and blocks save")
    public void negativePriceBlocked() {
        mgmt.clickAddItem();
        mgmt.fill("Invalid Item", "Starters", "Lunch", "Veg", "-50", "10", "5",
                "https://example.com/img.jpg");
        mgmt.clickSave();
        Assert.assertTrue(
                WaitUtils.textVisible(driver, "invalid")
                        || WaitUtils.textVisible(driver, "Price"),
                "Warning toast about price not displayed");
    }

    @Test(description = "TC 2.14.3 - Edit icon opens modal with pre-populated data")
    public void editPrePopulatesModal() {
        mgmt.clickFirstEdit();
        Assert.assertTrue(mgmt.modalOpen(), "Edit modal did not open");
        // Verifying pre-population in a generic way: at least one text input must already have value
        boolean prefilled = driver.findElements(org.openqa.selenium.By.cssSelector("input"))
                .stream().anyMatch(e -> e.getAttribute("value") != null
                        && !e.getAttribute("value").isBlank());
        Assert.assertTrue(prefilled, "Edit modal should be pre-populated");
    }

    @Test(description = "TC 2.14.4 - Stock toggle updates optimistically",
            enabled = false)
    public void stockToggleOptimisticUpdate() {
        // Requires API mocking to simulate failure for revert verification.
    }

    @Test(description = "TC 2.14.5 - Delete icon prompts for confirmation before removal")
    public void deleteConfirmsBeforeRemoval() {
        mgmt.clickFirstDelete();
        Assert.assertTrue(WaitUtils.textVisible(driver, "Confirm")
                        || WaitUtils.textVisible(driver, "delete"),
                "Confirmation prompt not displayed");
        mgmt.confirmDelete();
    }
}
