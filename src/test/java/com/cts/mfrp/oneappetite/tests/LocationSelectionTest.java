package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.DashboardPage;
import com.cts.mfrp.oneappetite.pages.LocationSelectionPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

public class LocationSelectionTest extends BaseTest {

    @Test(description = "TC 2.6.1 - Cascading dropdowns are disabled until previous selection")
    public void cascadingDropdownsDisabledByDefault() {
        LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);
        LocationSelectionPage page = new LocationSelectionPage(driver);
        Assert.assertTrue(page.isLoaded(), "Location selection page did not load");
        Assert.assertTrue(page.cityVisible(),    "City dropdown missing");
        Assert.assertTrue(page.campusVisible(),  "Campus dropdown missing");
        Assert.assertTrue(page.buildingVisible(),"Building dropdown missing");
        Assert.assertTrue(page.campusDisabled(),  "Campus should be disabled before City selection");
        Assert.assertTrue(page.buildingDisabled(),"Building should be disabled before Campus selection");
    }

    @Test(description = "TC 2.6.2 - Each selection enables the next dropdown")
    public void selectingCascadesEnableNext() {
        LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);
        LocationSelectionPage page = new LocationSelectionPage(driver);
        page.selectCity(ConfigReader.get("location.city"));
        Assert.assertFalse(page.campusDisabled(), "Campus should be enabled after city");
        page.selectCampus(ConfigReader.get("location.campus"));
        Assert.assertFalse(page.buildingDisabled(), "Building should be enabled after campus");
        page.selectBuilding(ConfigReader.get("location.building"));
        Assert.assertTrue(page.findVendorsActive(), "Find Vendors button should be active");
    }

    @Test(description = "TC 2.6.3 - Find Vendors saves location and switches to Vendor List Mode")
    public void findVendorsSwitchesToVendorList() {
        LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);
        LocationSelectionPage page = new LocationSelectionPage(driver);
        page.selectCity(ConfigReader.get("location.city"));
        page.selectCampus(ConfigReader.get("location.campus"));
        page.selectBuilding(ConfigReader.get("location.building"));
        page.clickFindVendors();
        DashboardPage dash = new DashboardPage(driver);
        Assert.assertTrue(dash.vendorListMode(), "Did not switch to Vendor List Mode");
        Assert.assertTrue(dash.currentLocationVisible(), "Current Location card not visible");
        Assert.assertTrue(dash.vendorListHeaderText().toLowerCase().contains("vendors near you"),
                "Header should read 'N Vendors Near You'");
        String storage = (String) ((org.openqa.selenium.JavascriptExecutor) driver)
                .executeScript("return localStorage.getItem('selectedLocation') || localStorage.getItem('location');");
        Assert.assertNotNull(storage, "Location should be persisted in localStorage");
    }
}
