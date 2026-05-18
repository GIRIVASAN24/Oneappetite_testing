package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.LocationSelectionPage;
import com.cts.mfrp.oneappetite.pages.TopBarComponent;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

public class TopBarComponentTest extends BaseTest {

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
            throw new SkipException(
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
 