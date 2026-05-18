package com.cts.mfrp.oneappetite.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class CreateAccountPage extends BasePage {

    @FindBy(xpath = "//h1[normalize-space()='Create Account'] | //h2[normalize-space()='Create Account']")
    private WebElement heading;

    @FindAll({
            @FindBy(css = "input[name='fullName']"),
            @FindBy(css = "input[name='name']"),
            @FindBy(css = "input[placeholder*='Full' i]")
    })
    private WebElement fullName;

    @FindAll({
            @FindBy(css = "input[type='email']"),
            @FindBy(css = "input[name='email']")
    })
    private WebElement workEmail;

    @FindAll({
            @FindBy(css = "input[type='tel']"),
            @FindBy(css = "input[name='phone']")
    })
    private WebElement phone;

    @FindAll({
            @FindBy(css = "input[type='password']"),
            @FindBy(css = "input[name='password']")
    })
    private WebElement password;

    @FindBy(xpath = "//button[normalize-space()='Create account' or normalize-space()='Create Account']")
    private WebElement createBtn;

    @FindBy(xpath = "//a[normalize-space()='Sign in' or normalize-space()='Sign In'] | //button[normalize-space()='Sign in']")
    private WebElement signInLink;

    @FindBy(css = "input[name='vendorName']")
    private List<WebElement> vendorNameEls;
    @FindAll({
            @FindBy(css = "textarea[name='description']"),
            @FindBy(css = "input[name='description']")
    })
    private List<WebElement> descriptionEls;
    @FindBy(css = "input[name='buildingId']")
    private List<WebElement> buildingIdEls;

    @FindBy(xpath = "//*[contains(.,'cognizant.com') or contains(.,'cts.com')]")
    private List<WebElement> emailDomainErrors;

    @FindBy(xpath = "//*[contains(.,'Must be 10 digits') or contains(.,'Invalid format')]")
    private List<WebElement> phoneErrors;

    @FindBy(xpath = "//*[contains(.,'Must be at least 8 characters')]")
    private List<WebElement> passwordErrors;

    public CreateAccountPage(WebDriver driver) { super(driver); }

    public boolean isLoaded()        { return isDisplayed(heading); }
    public String headingText()      { return waitVisible(heading).getText(); }
    public boolean fullNameVisible() { return isDisplayed(fullName); }
    public boolean workEmailVisible(){ return isDisplayed(workEmail); }
    public boolean phoneVisible()    { return isDisplayed(phone); }
    public boolean passwordVisible() { return isDisplayed(password); }
    public boolean createBtnVisible(){ return isDisplayed(createBtn); }
    public boolean signInLinkVisible(){return isDisplayed(signInLink); }
    public boolean vendorFieldsVisible() {
        return anyDisplayed(vendorNameEls) && anyDisplayed(descriptionEls) && anyDisplayed(buildingIdEls);
    }

    public void selectRole(String role)  { clickByText(role); }
    public void enterFullName(String v)  { type(fullName, v); }
    public void enterWorkEmail(String v) { type(workEmail, v); }
    public void enterPhone(String v)     { type(phone, v); }
    public void enterPassword(String v)  { type(password, v); }
    public void clickCreate()            { click(createBtn); }
    public void clickSignIn()            { click(signInLink); }

    public boolean emailDomainErrorVisible() { return anyDisplayed(emailDomainErrors); }
    public boolean phoneErrorVisible()       { return anyDisplayed(phoneErrors); }
    public boolean passwordErrorVisible()    { return anyDisplayed(passwordErrors); }
}
