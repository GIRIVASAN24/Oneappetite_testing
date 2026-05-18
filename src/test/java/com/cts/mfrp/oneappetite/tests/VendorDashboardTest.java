package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.VendorDashboardPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class VendorDashboardTest extends BaseTest {

    private VendorDashboardPage kanban;

    @BeforeMethod(alwaysRun = true)
    public void openVendorDashboard() {
        LoginHelper.loginAs(driver, AppConstants.VENDOR_ROLE);
        kanban = new VendorDashboardPage(driver);
    }

    @Test(description = "TC 2.13.1 - Kanban dashboard shows three columns (Placed/Preparing/Ready)")
    public void kanbanColumnsRender() {
        Assert.assertTrue(kanban.placedColumnVisible(),    "Placed column missing");
        Assert.assertTrue(kanban.preparingColumnVisible(), "Preparing column missing");
        Assert.assertTrue(kanban.readyColumnVisible(),     "Ready column missing");
    }

    @Test(description = "TC 2.13.2 - Drag-and-drop moves orders forward through the board",
            enabled = false)
    public void dragOrderForward() {
        // Disabled: Angular CDK drag-drop does not accept Selenium synthetic events
        // (isTrusted=false). Verify manually or via API-level test.
        if (kanban.placedOrders().isEmpty()) {
            throw new SkipException("No orders in Placed column to drag");
        }
        int before = kanban.preparingOrders().size();
        kanban.dragFirstPlacedToPreparing();
        Assert.assertTrue(kanban.preparingOrders().size() > before,
                "Preparing column should have one more order after drag");
    }

    @Test(description = "TC 2.13.3 - Backward drag from Preparing to Placed is rejected",
            enabled = false)
    public void backwardDragRejected() {
        // Disabled: see TC 2.13.2.
        if (kanban.preparingOrders().isEmpty()) {
            throw new SkipException("No orders in Preparing column");
        }
        int before = kanban.preparingOrders().size();
        kanban.dragFirstPreparingToPlaced();
        Assert.assertEquals(kanban.preparingOrders().size(), before,
                "Order should remain in Preparing after backward drag attempt");
    }

    @Test(description = "TC 2.13.4 - Optimistic update reverts on API failure with red toast",
            enabled = false)
    public void optimisticRevertOnFailure() {
        // Requires the test backend to expose a way to inject API failures (e.g. network mocking)
    }

    @Test(description = "TC 2.13.5 - New order plays audio and shows info toast",
            enabled = false)
    public void newOrderAlertOnPoll() {
        // Requires triggering an order from a separate session; out of scope for single-driver tests
    }
}
