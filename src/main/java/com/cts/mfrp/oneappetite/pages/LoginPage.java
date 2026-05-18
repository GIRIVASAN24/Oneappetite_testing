package com.cts.mfrp.oneappetite.pages;

import com.cts.mfrp.oneappetite.utils.WaitUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;

import java.util.List;

public class LoginPage extends BasePage {

    @FindBy(xpath = "//h1[contains(text(),'Welcome back to One Appetite']")
    private WebElement heading;

    @FindBy(xpath = "//p[@class='hero-tagline']")
    private WebElement tagline;


    @FindBy(id = "email")
    private WebElement emailInput;


    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(xpath="//button[@aria-label='Toggle password visibility']")
    private WebElement passwordEyeIcon;

    @FindBy(xpath = "//button[normalize-space()='Sign In' or normalize-space()='Sign in']")
    private WebElement signInButton;

    @FindBy(xpath = "//a[contains(translate(.,'FP','fp'),'forgot password')] | //button[contains(translate(.,'FP','fp'),'forgot password')]")
    private WebElement forgotPasswordLink;

    @FindBy(xpath = "//a[contains(.,'Create one now')] | //button[contains(.,'Create one now')]")
    private WebElement createAccountLink;

    @FindBy(xpath = "//*[contains(@class,'error') or contains(@class,'invalid')][contains(translate(.,'EMAIL','email'),'email')]")
    private List<WebElement> emailErrors;

    @FindBy(xpath = "//*[contains(.,'Must be at least 8 characters')]")
    private List<WebElement> passwordErrors;

    @FindBy(xpath = "//*[contains(.,'Please select a role to continue')]")
    private List<WebElement> roleErrors;

    @FindBy(xpath = "//*[contains(translate(.,'COGNIZANTCTS','cognizantcts'),'cognizant.com') or contains(.,'cts.com')]")
    private List<WebElement> domainErrors;

    @FindBy(xpath = "//*[contains(.,'No vendor account found') or contains(.,'Your account is registered')]")
    private List<WebElement> roleMismatchErrors;

    @FindAll({
            @FindBy(css = "[data-testid='google-login']"),
            @FindBy(css = "button[aria-label*='Google' i]"),
            @FindBy(css = "img[alt*='Google' i]")
    })
    private List<WebElement> googleSocialEls;

    @FindAll({
            @FindBy(css = "[data-testid='microsoft-login']"),
            @FindBy(css = "button[aria-label*='Microsoft' i]"),
            @FindBy(css = "img[alt*='Microsoft' i]")
    })
    private List<WebElement> microsoftSocialEls;

    public LoginPage(WebDriver driver) { super(driver); }

    public boolean isLoaded() {
        return isDisplayed(heading) && isDisplayed(emailInput)
                && isDisplayed(passwordInput) && isDisplayed(signInButton);
    }

    public String headingText() { return waitVisible(heading).getText(); }
    public String taglineText() { return waitVisible(tagline).getText(); }

    public boolean emailFieldVisible()    { return isDisplayed(emailInput); }
    public boolean passwordFieldVisible() { return isDisplayed(passwordInput); }
    public boolean signInButtonVisible()  { return isDisplayed(signInButton); }
    public boolean forgotPasswordVisible(){ return isDisplayed(forgotPasswordLink); }
    public boolean createAccountVisible() { return isDisplayed(createAccountLink); }
    public boolean googleSocialVisible()  { return anyDisplayed(googleSocialEls); }
    public boolean microsoftSocialVisible(){ return anyDisplayed(microsoftSocialEls); }
    public boolean eyeIconVisible()       { return isDisplayed(passwordEyeIcon); }

    public String emailPlaceholder()   { return waitVisible(emailInput).getAttribute("placeholder"); }
    public String passwordInputType()  { return waitVisible(passwordInput).getAttribute("type"); }

    public LoginPage enterEmail(String email)       { type(emailInput, email);     return this; }
    public LoginPage enterPassword(String password) { type(passwordInput, password); return this; }

    public LoginPage selectRole(String role) { clickByText(role); return this; }

    public boolean isRoleSelected(String role) {
        WebElement chip = driver.findElement(
                org.openqa.selenium.By.xpath(
                        "//*[self::button or self::div or self::label][normalize-space()='" + role + "']"));
        String classes = chip.getAttribute("class");
        String aria = chip.getAttribute("aria-pressed");
        return (classes != null && (classes.contains("active") || classes.contains("selected")))
                || "true".equalsIgnoreCase(aria);
    }

    public void togglePasswordVisibility() { click(passwordEyeIcon); }
    public void clickSignIn()              { click(signInButton); }

    public ForgotPasswordModal openForgotPassword() {
        click(forgotPasswordLink);
        return new ForgotPasswordModal(driver);
    }

    public CreateAccountPage openCreateAccount() {
        click(createAccountLink);
        return new CreateAccountPage(driver);
    }

    public boolean emailErrorVisible()        { return anyDisplayed(emailErrors) || pageContains("email"); }
    public boolean passwordErrorVisible()     { return anyDisplayed(passwordErrors); }
    public boolean roleErrorVisible()         { return anyDisplayed(roleErrors); }
    public boolean domainErrorVisible()       { return anyDisplayed(domainErrors) || WaitUtils.textVisible(driver, "cognizant.com"); }
    public boolean roleMismatchErrorVisible() { return anyDisplayed(roleMismatchErrors); }
}
