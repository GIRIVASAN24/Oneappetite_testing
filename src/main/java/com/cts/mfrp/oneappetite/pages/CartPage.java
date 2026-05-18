package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CartPage extends BasePage {

    @FindBy(xpath = "//h1[normalize-space()='Your Cart'] | //h2[normalize-space()='Your Cart']")
    private WebElement heading;

    @FindAll({
            @FindBy(css = "[data-testid='summary-card']"),
            @FindBy(css = ".summary-card")
    })
    private List<WebElement> summaryCard;

    @FindAll({
            @FindBy(css = "[data-testid='wallet-card']"),
            @FindBy(css = ".wallet-card")
    })
    private List<WebElement> walletCard;

    @FindAll({
            @FindBy(css = "[data-testid='items-card']"),
            @FindBy(css = ".items-card")
    })
    private List<WebElement> itemsCard;

    @FindAll({
            @FindBy(css = "[data-testid='subtotal']"),
            @FindBy(css = ".subtotal")
    })
    private WebElement subtotalEl;

    @FindAll({
            @FindBy(css = "[data-testid='total']"),
            @FindBy(css = ".total")
    })
    private WebElement totalEl;

    @FindBy(xpath = "//button[normalize-space()='Place Order']")
    private WebElement placeOrderBtn;

    @FindBy(xpath = "//*[contains(.,'Processing Payment')]")
    private List<WebElement> processingModalEls;

    @FindBy(xpath = "//*[contains(.,'Insufficient wallet balance')]")
    private List<WebElement> insufficientErrEls;

    @FindAll({
            @FindBy(css = "[data-testid='wallet-balance']"),
            @FindBy(css = ".wallet-balance")
    })
    private WebElement walletBalanceEl;

    public CartPage(WebDriver driver) { super(driver); }

    public boolean isLoaded()        { return isDisplayed(heading); }
    public boolean summaryVisible()  { return anyDisplayed(summaryCard); }
    public boolean walletVisible()   { return anyDisplayed(walletCard); }
    public boolean itemsVisible()    { return anyDisplayed(itemsCard); }
    public boolean placeOrderVisible(){return isDisplayed(placeOrderBtn); }

    public String subtotalText()      { return waitVisible(subtotalEl).getText().trim(); }
    public String totalText()         { return waitVisible(totalEl).getText().trim(); }
    public String walletBalanceText() { return waitVisible(walletBalanceEl).getText().trim(); }

    public void increment() {
        driver.findElement(By.xpath("(//button[normalize-space()='+' or @aria-label='increase'])[1]")).click();
    }
    public void decrement() {
        driver.findElement(By.xpath("(//button[normalize-space()='-' or @aria-label='decrease'])[1]")).click();
    }
    public void placeOrder() { click(placeOrderBtn); }

    public boolean processingModalVisible()        { return anyDisplayed(processingModalEls); }
    public boolean insufficientWalletErrorVisible(){ return anyDisplayed(insufficientErrEls); }
}
