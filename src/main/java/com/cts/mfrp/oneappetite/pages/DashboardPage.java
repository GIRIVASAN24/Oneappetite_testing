package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class DashboardPage extends BasePage {

    @FindBy(xpath = "//*[contains(.,'Vendors Near You')]")
    private List<WebElement> vendorListHeader;

    @FindAll({
            @FindBy(css = "[data-testid='current-location']"),
            @FindBy(css = ".current-location-card")
    })
    private List<WebElement> currentLocationCard;

    @FindBy(xpath = "//*[contains(.,'Change Location')]")
    private WebElement changeLocationPill;

    @FindAll({
            @FindBy(css = "input[type='search']"),
            @FindBy(css = "input[placeholder*='Search' i]")
    })
    private WebElement searchBox;

    @FindAll({
            @FindBy(css = "[data-testid='vendor-card']"),
            @FindBy(css = ".vendor-card")
    })
    private List<WebElement> vendorCards;

    @FindBy(xpath = "//*[contains(.,'No vendors match your filters')]")
    private List<WebElement> emptyStateEls;

    @FindBy(xpath = "//button[normalize-space()='View Menu']")
    private List<WebElement> viewMenuButtons;

    public DashboardPage(WebDriver driver) { super(driver); }

    public boolean vendorListMode()         { return anyDisplayed(vendorListHeader); }
    public boolean currentLocationVisible() { return anyDisplayed(currentLocationCard); }
    public boolean changeLocationVisible()  { return isDisplayed(changeLocationPill); }
    public String vendorListHeaderText()    { return waitVisible(vendorListHeader.get(0)).getText(); }

    public void clickAll()            { clickByText("All"); }
    public void clickVeg()            { clickByText("Veg"); }
    public void clickNonVeg()         { clickByText("Non-Veg"); }
    public void clickChangeLocation() { click(changeLocationPill); }

    public List<WebElement> visibleVendors() { return vendorCards; }
    public int visibleVendorCount() { return vendorCards.size(); }

    public void search(String q) { type(searchBox, q); }
    public void clearSearch()     { waitVisible(searchBox).clear(); }

    public boolean emptyStateVisible() { return anyDisplayed(emptyStateEls); }

    public VendorMenuPage openFirstVendorMenu() {
        viewMenuButtons.get(0).click();
        return new VendorMenuPage(driver);
    }
}
