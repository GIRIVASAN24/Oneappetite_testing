package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class VendorSettingsPage extends BasePage {

    @FindBy(xpath = "//*[contains(@class,'card')][.//*[normalize-space()='Profile']]")
    private List<WebElement> profileCard;
    @FindBy(xpath = "//*[contains(@class,'card')][.//*[normalize-space()='Notifications']]")
    private List<WebElement> notificationsCard;
    @FindBy(xpath = "//*[contains(@class,'card')][.//*[normalize-space()='Password']]")
    private List<WebElement> passwordCard;
    @FindBy(xpath = "//*[contains(@class,'card')][.//*[contains(normalize-space(),'Stall Locations')]]")
    private List<WebElement> stallLocationsCard;

    @FindBy(css = "input[type='email']")
    private WebElement emailInput;

    @FindBy(xpath = "//*[contains(.,'Registered')]")
    private List<WebElement> registeredBadges;

    @FindBy(xpath = "//button[contains(.,'Add Location')]")
    private WebElement addLocationBtn;

    @FindBy(css = "[role='dialog']")
    private List<WebElement> cascadeModalEls;

    @FindBy(xpath = "//button[normalize-space()='Confirm']")
    private WebElement confirmBtn;

    @FindAll({
            @FindBy(css = "[data-testid='also-serving-item']"),
            @FindBy(css = ".also-serving-item")
    })
    private List<WebElement> alsoServingItems;

    @FindAll({
            @FindBy(css = "[data-testid='remove-location']"),
            @FindBy(css = "button[aria-label*='Remove' i]")
    })
    private List<WebElement> removeXIcons;

    @FindAll({
            @FindBy(css = "input[name='fullName']"),
            @FindBy(css = "input[name='name']")
    })
    private WebElement fullName;

    @FindBy(css = "input[type='tel']")
    private WebElement phoneInput;

    @FindBy(xpath = "(//button[normalize-space()='Save'])[1]")
    private WebElement profileSaveBtn;

    @FindBy(xpath = "//*[contains(.,'Order updates')]/following::input[@type='checkbox'][1]")
    private WebElement orderUpdatesToggle;

    @FindBy(css = "input[name='currentPassword']")
    private WebElement currentPassword;
    @FindBy(css = "input[name='newPassword']")
    private WebElement newPassword;
    @FindBy(css = "input[name='confirmPassword']")
    private WebElement confirmPassword;
    @FindBy(xpath = "//*[contains(@class,'card')][.//*[normalize-space()='Password']]//button[normalize-space()='Save']")
    private WebElement passwordSaveBtn;

    public VendorSettingsPage(WebDriver driver) { super(driver); }

    public boolean profileCardVisible()        { return anyDisplayed(profileCard); }
    public boolean notificationsCardVisible()  { return anyDisplayed(notificationsCard); }
    public boolean passwordCardVisible()       { return anyDisplayed(passwordCard); }
    public boolean stallLocationsCardVisible() { return anyDisplayed(stallLocationsCard); }
    public boolean primaryBuildingReadOnly() {
        WebElement el = waitVisible(emailInput);
        return el.getAttribute("readonly") != null || el.getAttribute("disabled") != null;
    }
    public boolean registeredBadgeVisible() { return anyDisplayed(registeredBadges); }

    public void editFullName(String v) { type(fullName, v); }
    public void editPhone(String v)    { type(phoneInput, v); }
    public void clickProfileSave()     { click(profileSaveBtn); }

    public void clickAddLocation()    { click(addLocationBtn); }
    public boolean cascadeModalOpen() { return anyDisplayed(cascadeModalEls); }
    public void confirmAddLocation()  { click(confirmBtn); }

    public List<WebElement> alsoServing() { return alsoServingItems; }
    public void removeFirstAlsoServing() {
        if (!removeXIcons.isEmpty()) removeXIcons.get(0).click();
    }

    public void toggleOrderUpdates() { click(orderUpdatesToggle); }
    public void enterCurrentPassword(String v) { type(currentPassword, v); }
    public void enterNewPassword(String v)     { type(newPassword, v); }
    public void enterConfirmPassword(String v) { type(confirmPassword, v); }
    public void clickPasswordSave()            { click(passwordSaveBtn); }
}
