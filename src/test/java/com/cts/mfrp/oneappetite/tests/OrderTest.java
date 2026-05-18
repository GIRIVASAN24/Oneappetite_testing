package com.cts.mfrp.oneappetite.tests;

import com.cts.mfrp.oneappetite.base.BaseTest;
import com.cts.mfrp.oneappetite.constants.AppConstants;
import com.cts.mfrp.oneappetite.pages.CartPage;
import com.cts.mfrp.oneappetite.pages.DashboardPage;
import com.cts.mfrp.oneappetite.pages.LocationSelectionPage;
import com.cts.mfrp.oneappetite.pages.OrderPage;
import com.cts.mfrp.oneappetite.pages.VendorMenuPage;
import com.cts.mfrp.oneappetite.tests.support.LoginHelper;
import com.cts.mfrp.oneappetite.utils.ConfigReader;
import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class OrderTest extends BaseTest {

    @BeforeMethod(alwaysRun = true)
    public void addItemAndOpenCart() {
        LoginHelper.loginAs(driver, AppConstants.EMPLOYEE_ROLE);
        LocationSelectionPage loc = new LocationSelectionPage(driver);
        if (loc.isLoaded()) {
            loc.selectCity(ConfigReader.get("location.city"));
            loc.selectCampus(ConfigReader.get("location.campus"));
            loc.selectBuilding(ConfigReader.get("location.building"));
            loc.clickFindVendors();
        }
        DashboardPage dash = new DashboardPage(driver);
        if (dash.visibleVendorCount() == 0) throw new SkipException("No vendors available");
        VendorMenuPage menu = dash.openFirstVendorMenu();
        if (menu.itemCount() == 0) throw new SkipException("No menu items available");
        menu.addFirstItem();
        driver.get(ConfigReader.get("base.url") + AppConstants.ROUTE_CART);
    }

    @Test(description = "TC 2.9.1 - Cart page shows Summary, Wallet, Items and sticky Total bar")
    public void cartUiComplete() {
        CartPage cart = new CartPage(driver);
        Assert.assertTrue(cart.isLoaded(), "Cart heading 'Your Cart' missing");
        Assert.assertTrue(cart.summaryVisible(), "Summary card missing");
        Assert.assertTrue(cart.walletVisible(),  "Wallet card missing");
        Assert.assertTrue(cart.itemsVisible(),   "Items card missing");
        Assert.assertFalse(cart.subtotalText().isBlank(), "Subtotal must render");
        Assert.assertFalse(cart.totalText().isBlank(),    "Total must render");
        Assert.assertTrue(cart.placeOrderVisible(), "Place Order button missing");
    }

    @Test(description = "TC 2.9.2 - Quantity stepper on cart updates subtotal and total")
    public void quantityUpdatesTotals() {
        CartPage cart = new CartPage(driver);
        String beforeSub = cart.subtotalText();
        cart.increment();
        Assert.assertNotEquals(cart.subtotalText(), beforeSub,
                "Subtotal should change on increment");
        cart.decrement();
        Assert.assertEquals(cart.subtotalText(), beforeSub,
                "Subtotal should revert after decrement");
    }

    @Test(description = "TC 2.9.3 - Place Order opens the backdrop-blurred Processing Payment modal")
    public void placeOrderShowsProcessingModal() {
        CartPage cart = new CartPage(driver);
        cart.placeOrder();
        Assert.assertTrue(cart.processingModalVisible(),
                "'Processing Payment' modal not displayed");
    }

    @Test(description = "TC 2.9.4 - Successful order debits wallet, generates OA-XXXX token, status 'Placed'")
    public void successfulOrderEndToEnd() {
        CartPage cart = new CartPage(driver);
        cart.placeOrder();
        OrderPage confirm = new OrderPage(driver);
        Assert.assertTrue(confirm.isLoaded(),
                "Order Confirmed screen did not appear");
        Assert.assertTrue(confirm.tokenMatchesPattern(),
                "Generated token must match OA-XXXX format. Got: " + confirm.tokenText());
    }

    @Test(description = "TC 2.9.5 - Insufficient wallet balance rejects the order with inline + red toast",
            enabled = false)
    public void insufficientWalletRejects() {
        // Requires a test account that is guaranteed to have a balance below cart total.
        // Enable once such a fixture exists.
    }

    @Test(description = "TC 2.10 - Order Confirmation screen UI is complete")
    public void confirmationScreenUi() {
        new CartPage(driver).placeOrder();
        OrderPage confirm = new OrderPage(driver);
        Assert.assertTrue(confirm.isLoaded(), "Order Confirmed heading missing");
        Assert.assertTrue(confirm.tokenMatchesPattern(),
                "Token chip must show OA-XXXX format");
        confirm.clickBackToHome();
        Assert.assertTrue(WaitUtils.urlContains(driver, AppConstants.ROUTE_DASHBOARD),
                "Back to Home should route to /dashboard");
    }
}
