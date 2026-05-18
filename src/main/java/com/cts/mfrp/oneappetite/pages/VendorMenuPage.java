package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class VendorMenuPage extends BasePage {

    @FindBy(xpath = "//*[self::button or self::label][normalize-space()='All']")
    private WebElement allChip;

    @FindAll({
            @FindBy(css = "input[type='search']"),
            @FindBy(css = "input[placeholder*='Search' i]")
    })
    private WebElement searchBox;

    @FindAll({
            @FindBy(css = "[data-testid='menu-item']"),
            @FindBy(css = ".menu-item-card")
    })
    private List<WebElement> menuItems;

    @FindBy(xpath = "//button[normalize-space()='Add']")
    private List<WebElement> addButtons;

    @FindBy(xpath = "//*[contains(.,'Out of Stock')]/ancestor::*[contains(@class,'menu-item') or contains(@class,'card')][1]")
    private List<WebElement> outOfStockCards;

    @FindAll({
            @FindBy(css = "[data-testid='sticky-cart-bar']"),
            @FindBy(css = ".sticky-bottom-bar")
    })
    private List<WebElement> stickyBarEls;

    @FindBy(xpath = "//a[contains(.,'View Cart')] | //button[contains(.,'View Cart')]")
    private WebElement viewCartLink;

    public VendorMenuPage(WebDriver driver) { super(driver); }

    public boolean allChipActive() {
        WebElement el = waitVisible(allChip);
        String cls = el.getAttribute("class");
        return cls != null && (cls.contains("active") || cls.contains("selected"));
    }
    public void clickAll()       { clickByText("All"); }
    public void clickBreakfast() { clickByText("Breakfast"); }
    public void clickLunch()     { clickByText("Lunch"); }
    public void clickDinner()    { clickByText("Dinner"); }
    public void clickVeg()       { clickByText("Veg"); }
    public void clickNonVeg()    { clickByText("Non-Veg"); }
    public void search(String q) { type(searchBox, q); }

    public boolean breakfastVisible() { return isDisplayed(By.xpath("//*[self::button or self::label][normalize-space()='Breakfast']")); }
    public boolean lunchVisible()     { return isDisplayed(By.xpath("//*[self::button or self::label][normalize-space()='Lunch']")); }
    public boolean dinnerVisible()    { return isDisplayed(By.xpath("//*[self::button or self::label][normalize-space()='Dinner']")); }
    public boolean searchVisible()    { return isDisplayed(searchBox); }
    public boolean dietaryToggleVisible() {
        return isDisplayed(By.xpath("//*[self::button or self::label][normalize-space()='Veg']"))
                && isDisplayed(By.xpath("//*[self::button or self::label][normalize-space()='Non-Veg']"));
    }

    public List<WebElement> visibleItems() { return menuItems; }
    public int itemCount() { return menuItems.size(); }

    public void addFirstItem() {
        if (!addButtons.isEmpty()) addButtons.get(0).click();
    }

    public boolean outOfStockOverlayVisible() { return anyDisplayed(outOfStockCards); }

    public boolean addButtonDisabledForOutOfStock() {
        for (WebElement card : outOfStockCards) {
            List<WebElement> buttons = card.findElements(By.xpath(".//button[normalize-space()='Add']"));
            if (!buttons.isEmpty() && buttons.get(0).getAttribute("disabled") != null) return true;
        }
        return false;
    }

    public boolean stickyBarVisible()   { return anyDisplayed(stickyBarEls); }
    public boolean viewCartLinkVisible(){ return isDisplayed(viewCartLink); }
    public CartPage clickViewCart()     { click(viewCartLink); return new CartPage(driver); }

    public void incrementFirst() {
        driver.findElement(By.xpath("(//button[normalize-space()='+' or @aria-label='increase'])[1]")).click();
    }
    public void decrementFirst() {
        driver.findElement(By.xpath("(//button[normalize-space()='-' or @aria-label='decrease'])[1]")).click();
    }

    public String firstQuantityText() {
        return driver.findElement(By.cssSelector(".quantity, [data-testid='qty']")).getText().trim();
    }
}
