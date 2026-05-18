package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class VendorMenuMgmtPage extends BasePage {

    @FindBy(xpath = "//button[normalize-space()='Add Menu Item']")
    private WebElement addItemBtn;

    @FindAll({
            @FindBy(css = "[role='dialog']"),
            @FindBy(css = ".modal")
    })
    private List<WebElement> modalEls;

    @FindAll({
            @FindBy(css = "input[name='name']"),
            @FindBy(css = "input[placeholder*='Item name' i]")
    })
    private WebElement itemName;

    @FindAll({
            @FindBy(css = "input[name='category']"),
            @FindBy(css = "select[name='category']")
    })
    private WebElement category;

    @FindAll({
            @FindBy(css = "select[name='mealCourse']"),
            @FindBy(css = "input[name='mealCourse']")
    })
    private WebElement mealCourse;

    @FindBy(css = "input[name='price']")
    private WebElement price;

    @FindAll({
            @FindBy(css = "input[name='quantity']"),
            @FindBy(css = "input[name='qty']")
    })
    private WebElement quantity;

    @FindBy(css = "input[name='prepTime']")
    private WebElement prepTime;

    @FindAll({
            @FindBy(css = "input[name='imageUrl']"),
            @FindBy(css = "input[name='image']")
    })
    private WebElement imageUrl;

    @FindBy(xpath = "//button[normalize-space()='Save']")
    private WebElement saveBtn;

    @FindAll({
            @FindBy(css = "[data-testid='edit-item']"),
            @FindBy(css = "button[aria-label*='Edit' i]")
    })
    private WebElement editIcon;

    @FindAll({
            @FindBy(css = "[data-testid='delete-item']"),
            @FindBy(css = "button[aria-label*='Delete' i]")
    })
    private WebElement deleteIcon;

    @FindAll({
            @FindBy(css = "[data-testid='stock-toggle']"),
            @FindBy(css = "input[type='checkbox'].stock-toggle")
    })
    private WebElement stockToggle;

    @FindBy(xpath = "//button[normalize-space()='Confirm']")
    private WebElement confirmBtn;

    public VendorMenuMgmtPage(WebDriver driver) { super(driver); }

    public boolean isLoaded()  { return isDisplayed(addItemBtn); }
    public void clickAddItem() { click(addItemBtn); }
    public boolean modalOpen() { return anyDisplayed(modalEls); }

    public void fill(String name, String cat, String course, String diet,
                     String pr, String qty, String prep, String img) {
        if (name != null)   type(itemName, name);
        if (cat != null)    type(category, cat);
        if (course != null) type(mealCourse, course);
        if ("Veg".equalsIgnoreCase(diet))    clickByText("Veg");
        if ("Non-Veg".equalsIgnoreCase(diet)) clickByText("Non-Veg");
        if (pr != null)   type(price, pr);
        if (qty != null)  type(quantity, qty);
        if (prep != null) type(prepTime, prep);
        if (img != null)  type(imageUrl, img);
    }

    public void clickSave()       { click(saveBtn); }
    public void clickFirstEdit()  { click(editIcon); }
    public void clickFirstDelete(){ click(deleteIcon); }
    public void confirmDelete()   { click(confirmBtn); }

    public void toggleFirstStock() { click(stockToggle); }
    public WebElement firstStockSwitch() { return waitVisible(stockToggle); }
}
