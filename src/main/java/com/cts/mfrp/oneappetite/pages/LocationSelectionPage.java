package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

public class LocationSelectionPage extends BasePage {

    @FindBy(xpath = "//*[contains(.,'Find your Flavor')]")
    private WebElement heading;

    @FindAll({
            @FindBy(css = "[data-testid='city-select']"),
            @FindBy(css = "select[name='city']"),
            @FindBy(id = "city")
    })
    private WebElement cityDropdown;

    @FindAll({
            @FindBy(css = "[data-testid='campus-select']"),
            @FindBy(css = "select[name='campus']"),
            @FindBy(id = "campus")
    })
    private WebElement campusDropdown;

    @FindAll({
            @FindBy(css = "[data-testid='building-select']"),
            @FindBy(css = "select[name='building']"),
            @FindBy(id = "building")
    })
    private WebElement buildingDropdown;

    @FindBy(xpath = "//button[normalize-space()='Find Vendors' or contains(.,'Find Vendors')]")
    private WebElement findVendorsBtn;

    public LocationSelectionPage(WebDriver driver) { super(driver); }

    public boolean isLoaded() { return isDisplayed(heading); }
    public boolean cityVisible()     { return isDisplayed(cityDropdown); }
    public boolean campusVisible()   { return isDisplayed(campusDropdown); }
    public boolean buildingVisible() { return isDisplayed(buildingDropdown); }

    public boolean campusDisabled()   { return isDisabled(campusDropdown); }
    public boolean buildingDisabled() { return isDisabled(buildingDropdown); }
    public boolean findVendorsActive(){ return !isDisabled(findVendorsBtn); }

    private boolean isDisabled(WebElement el) {
        WebElement v = waitVisible(el);
        return v.getAttribute("disabled") != null
                || "true".equalsIgnoreCase(v.getAttribute("aria-disabled"));
    }

    public void selectCity(String value)     { selectFromCombo(cityDropdown, value); }
    public void selectCampus(String value)   { selectFromCombo(campusDropdown, value); }
    public void selectBuilding(String value) { selectFromCombo(buildingDropdown, value); }

    private void selectFromCombo(WebElement combo, String value) {
        WebElement el = waitClickable(combo);
        el.click();
        try {
            el.sendKeys(value);
            el.sendKeys(Keys.ENTER);
        } catch (Exception ignored) {
            By option = By.xpath("//*[self::li or self::option or self::div][normalize-space()='" + value + "']");
            driver.findElement(option).click();
        }
    }

    public void clickFindVendors() { click(findVendorsBtn); }
}
