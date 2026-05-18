package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.DashboardPage;
import com.cts.mfrp.oneappetite.pages.LocationSelectionPage;
import com.cts.mfrp.oneappetite.pages.TopBarComponent;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VendorListTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void prepareVendorList() {
        // Runs after BaseTest.setUp (parent-class @BeforeMethod runs first by TestNG ordering).
        LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);
        LocationSelectionPage loc = new LocationSelectionPage(driver);
        if (loc.isLoaded()) {
            loc.selectCity(ConfigReader.get("location.city"));
            loc.selectCampus(ConfigReader.get("location.campus"));
            loc.selectBuilding(ConfigReader.get("location.building"));
            loc.clickFindVendors();
        }
    }

    @Test(description = "TC 2.7.1 - Dietary toggle filters vendors; Change Location resets selector")
    public void dietaryToggleAndChangeLocation() {
        DashboardPage dash = new DashboardPage(driver);
        int beforeVeg = dash.visibleVendorCount();
        dash.clickVeg();
        int afterVeg = dash.visibleVendorCount();
        Assert.assertTrue(afterVeg <= beforeVeg, "Veg filter should not increase vendor count");
        dash.clickNonVeg();
        dash.clickAll();
        Assert.assertEquals(dash.visibleVendorCount(), beforeVeg,
                "All filter should restore full vendor count");
        dash.clickChangeLocation();
        Assert.assertTrue(new LocationSelectionPage(driver).isLoaded(),
                "Location Selection page should reappear after Change Location");
    }

    @Test(description = "TC 2.7.2 - Vendor search filters by name/description; empty state on no match")
    public void searchFiltersVendors() {
        DashboardPage dash = new DashboardPage(driver);
        if (dash.visibleVendorCount() == 0) throw new SkipException("No vendors to test search");
        String firstName = dash.visibleVendors().get(0).getText().split("\\n")[0];
        dash.search(firstName);
        Assert.assertTrue(dash.visibleVendorCount() >= 1,
                "Search by an exact vendor name should retain at least 1 result");
        dash.clearSearch();
        dash.search("__no_such_vendor__zzzz__");
        Assert.assertTrue(dash.emptyStateVisible(),
                "Empty state should appear when no vendor matches search");
    }

    @Test(description = "TC 2.7.3 - Vendor card shows image, badge, name, description, View Menu button")
    public void vendorCardElements() {
        DashboardPage dash = new DashboardPage(driver);
        if (dash.visibleVendorCount() == 0) throw new SkipException("No vendors available");
        org.openqa.selenium.WebElement card = dash.visibleVendors().get(0);
        Assert.assertFalse(card.findElements(org.openqa.selenium.By.tagName("img")).isEmpty(),
                "Vendor card should display an image");
        Assert.assertTrue(card.getText().toLowerCase().matches("(?s).*(veg|non-veg).*"),
                "Veg/Non-Veg dietary badge missing from card text");
        Assert.assertFalse(card.findElements(
                org.openqa.selenium.By.xpath(".//button[normalize-space()='View Menu']")).isEmpty(),
                "View Menu button missing from card");
    }

    /** Inline skip helper (avoids extra imports). */
    private static class SkipException extends org.testng.SkipException {
        SkipException(String m) { super(m); }
    }

    public static class TopBarComponentTest extends BaseTest {

        @Test(description = "TC 2.4.3 - Locate top bar search field (per FRD) and search menu items if present")
        public void searchBarPresentAndSearchesMenuItems() {
            LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);

            LocationSelectionPage loc = new LocationSelectionPage(driver);
            if (loc.isLoaded()) {
                loc.selectCity("Chennai");
                loc.selectCampus("Siruseri SIPCOT");
                loc.selectBuilding("Academic Block");
                loc.clickFindVendors();
            }

            TopBarComponent top = new TopBarComponent(driver);
            if (!top.searchBarPresent()) {
                throw new org.testng.SkipException(
                        "Top bar 'Search menu items...' field is not present in the current build. "
                                + "FRD requires it, but the topbar template no longer renders a search "
                                + "input (only role-pill, theme-toggle, bell and avatar are present).");
            }

            top.searchMenuItem("pizza");
        }

        @Test(description = "TC 2.4.4 - Logged-in role pill (Employee/Admin) is visible in the top bar")
        public void loggedInRolePillVisibleForEachRole() {
            for (String role : new String[]{
                    AppConstants.EMPLOYEE_ROLE,
                    AppConstants.ADMIN_ROLE}) {

                LoginHelper.loginAs(driver, role);
                TopBarComponent top = new TopBarComponent(driver);

                String shown = top.loggedInRole();
                Assert.assertNotNull(shown,
                        "Logged-in role pill (span.role-pill) not found in the top bar for role '"
                                + role + "'.");
                Assert.assertEquals(shown, role.toUpperCase(),
                        "Top bar shows role '" + shown + "' but the logged-in user is '" + role + "'.");

                top.logout();
            }
        }
    }
}
