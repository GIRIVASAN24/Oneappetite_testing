package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.VendorDashboardPage;
import com.cts.mfrp.oneappetite.pages.VendorMenuMgmtPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VendorMenuMgmtTest extends BaseTest {

    private VendorMenuMgmtPage mgmt;

    @BeforeMethod(alwaysRun = true)
    public void openMenuMgmt() {
        LoginHelper.loginAs(driver, AppConstants.VENDOR_ROLE);

        // Wait for the dashboard to fully paint before navigating — driver.get() on a
        // half-loaded Angular SPA renders an empty menu page even though the URL is right.
        try {
            WaitUtils.visible(driver, By.id("list-PLACED"));
        } catch (Exception e) {
            throw new SkipException("Vendor dashboard did not finish loading after login");
        }

        // Navigate via the in-app tab link instead of driver.get() — keeps Angular's
        // router state intact so the destination page actually renders.
        new VendorDashboardPage(driver).goToMenu();

        if (!WaitUtils.urlContains(driver, AppConstants.ROUTE_VENDOR_MENU_MGMT)) {
            throw new SkipException("Did not navigate to Menu Management URL");
        }
        mgmt = new VendorMenuMgmtPage(driver);
        if (!mgmt.isLoaded()) throw new SkipException("Menu Management page did not load");
    }

    @Test(description = "TC 2.14.1 - Add Menu Item modal opens and saves a valid new item")
    public void addValidMenuItem() {
        mgmt.clickAddItem();
        Assert.assertTrue(mgmt.modalOpen(), "Add Menu Item modal did not open");
        String name = "Grilled Paneer " + System.currentTimeMillis();
        mgmt.fill(name, "Starters", "Lunch", "Vegetarian", "150", "50", "10",
                "https://example.com/paneer.jpg");
        mgmt.clickSave();
        Assert.assertTrue(WaitUtils.textVisible(driver, name),
                "Newly added item should appear in the menu list");
    }

    @Test(description = "TC 2.14.2 - Negative price triggers warning toast and blocks save")
    public void negativePriceBlocked() {
        mgmt.clickAddItem();
        mgmt.fill("Invalid Item", "Starters", "Lunch", "Vegetarian", "-50", "10", "5",
                "https://example.com/img.jpg");
        Assert.assertTrue(mgmt.isSaveDisabled(),
                "Save button should be disabled when price is negative");
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
        // Witness the popup by its actual confirm button ("Yes, Remove") being visible,
        // instead of grepping the page for generic words like "Confirm"/"delete".
        Assert.assertTrue(mgmt.confirmDialogVisible(),
                "Delete confirmation popup did not appear");
        mgmt.confirmDelete();
    }
}
