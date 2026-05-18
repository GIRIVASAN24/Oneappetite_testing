package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Order confirmation screen. */
public class OrderPage extends BasePage {

    private static final Pattern TOKEN = Pattern.compile("OA-\\d{4}");

    @FindBy(xpath = "//*[contains(.,'Order Confirmed')]")
    private WebElement heading;

    @FindBy(xpath = "//*[contains(.,'YOUR TOKEN')]/following::*[contains(text(),'OA-')][1]")
    private WebElement tokenChip;

    @FindBy(xpath = "//button[contains(.,'Back to Home')] | //a[contains(.,'Back to Home')]")
    private WebElement backHomeBtn;

    public OrderPage(WebDriver driver) { super(driver); }

    public boolean isLoaded() { return isDisplayed(heading); }

    public String tokenText() { return waitVisible(tokenChip).getText().trim(); }

    public boolean tokenMatchesPattern() {
        if (!isDisplayed(tokenChip)) return false;
        Matcher m = TOKEN.matcher(tokenText());
        return m.find();
    }

    public void clickBackToHome() { click(backHomeBtn); }
}
