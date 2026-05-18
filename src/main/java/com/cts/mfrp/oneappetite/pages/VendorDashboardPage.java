package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;
import java.util.List;

public class VendorDashboardPage extends BasePage {

    @FindBy(xpath = "//*[contains(@class,'column') or self::section][.//*[normalize-space()='Placed']]")
    private WebElement placedColumn;

    @FindBy(xpath = "//*[contains(@class,'column') or self::section][.//*[normalize-space()='Preparing']]")
    private WebElement preparingColumn;

    @FindBy(xpath = "//*[contains(@class,'column') or self::section][.//*[normalize-space()='Ready']]")
    private WebElement readyColumn;

    @FindAll({
            @FindBy(css = "[data-testid='kanban-card']"),
            @FindBy(css = ".kanban-card"),
            @FindBy(css = ".order-card")
    })
    private List<WebElement> allOrderCards;

    public VendorDashboardPage(WebDriver driver) { super(driver); }

    public boolean placedColumnVisible()    { return isDisplayed(placedColumn); }
    public boolean preparingColumnVisible() { return isDisplayed(preparingColumn); }
    public boolean readyColumnVisible()     { return isDisplayed(readyColumn); }

    public List<WebElement> ordersIn(WebElement column) {
        WebElement col = waitVisible(column);
        List<WebElement> a = col.findElements(By.cssSelector("[data-testid='kanban-card']"));
        if (!a.isEmpty()) return a;
        a = col.findElements(By.cssSelector(".kanban-card"));
        if (!a.isEmpty()) return a;
        return col.findElements(By.cssSelector(".order-card"));
    }

    public List<WebElement> placedOrders()    { return ordersIn(placedColumn); }
    public List<WebElement> preparingOrders() { return ordersIn(preparingColumn); }
    public List<WebElement> readyOrders()     { return ordersIn(readyColumn); }

    public void dragOrder(WebElement fromColumn, WebElement toColumn) {
        List<WebElement> orders = ordersIn(fromColumn);
        if (orders.isEmpty()) return;
        WebElement source = orders.get(0);
        WebElement target = waitVisible(toColumn);
        new Actions(driver).clickAndHold(source)
                .moveToElement(target).pause(Duration.ofMillis(400))
                .release().perform();
    }

    public void dragFirstPlacedToPreparing() { dragOrder(placedColumn, preparingColumn); }
    public void dragFirstPreparingToPlaced() { dragOrder(preparingColumn, placedColumn); }
    public void dragFirstPreparingToReady()  { dragOrder(preparingColumn, readyColumn); }
}
