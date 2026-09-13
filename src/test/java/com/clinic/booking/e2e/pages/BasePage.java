package com.clinic.booking.e2e.pages;

import org.openqa.selenium.By;
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
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected WebElement waitForElementClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void click(By locator) {
        for (int i = 0; i < 5; i++) {
            try {
                WebElement elem = driver.findElement(locator);
                if (elem.isDisplayed() && elem.isEnabled()) {
                    elem.click();
                    return;
                }
            } catch (Exception e) {
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }
        driver.findElement(locator).click();
    }

    protected void type(By locator, String text) {
        for (int i = 0; i < 5; i++) {
            try {
                WebElement elem = driver.findElement(locator);
                if (elem.isDisplayed()) {
                    elem.clear();
                    elem.sendKeys(text);
                    return;
                }
            } catch (Exception e) {
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }
        WebElement elem = driver.findElement(locator);
        elem.clear();
        elem.sendKeys(text);
    }

    protected void selectByValue(By locator, String value) {
        WebElement elem = waitForElementVisible(locator);
        Select select = new Select(elem);
        select.selectByValue(value);
    }

    protected String getText(By locator) {
        for (int i = 0; i < 5; i++) {
            try {
                WebElement elem = driver.findElement(locator);
                if (elem.isDisplayed()) {
                    return elem.getText();
                }
            } catch (Exception e) {
                try { Thread.sleep(250); } catch (InterruptedException ignored) {}
            }
        }
        return driver.findElement(locator).getText();
    }

    protected boolean isElementPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (Exception ex) {
            return false;
        }
    }
}
