package com.clinic.booking.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected WebElement waitForElementVisible(By locator) {
        return wait.until(ExpectedConditions.refreshed(ExpectedConditions.visibilityOfElementLocated(locator)));
    }

    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.refreshed(ExpectedConditions.elementToBeClickable(locator)));
    }

    protected void click(By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                waitForElementClickable(locator).click();
                return;
            } catch (StaleElementReferenceException e) {
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }
        waitForElementClickable(locator).click();
    }

    protected void type(By locator, String text) {
        for (int i = 0; i < 3; i++) {
            try {
                WebElement elem = waitForElementVisible(locator);
                elem.clear();
                elem.sendKeys(text);
                return;
            } catch (StaleElementReferenceException e) {
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }
    }

    protected void selectByValue(By locator, String value) {
        WebElement elem = waitForElementVisible(locator);
        Select select = new Select(elem);
        select.selectByValue(value);
    }

    protected String getText(By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                return waitForElementVisible(locator).getText();
            } catch (StaleElementReferenceException e) {
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }
        return waitForElementVisible(locator).getText();
    }

    protected boolean isElementPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (StaleElementReferenceException ex) {
            return false;
        }
    }
}
