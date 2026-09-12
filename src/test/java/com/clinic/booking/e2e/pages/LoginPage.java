package com.clinic.booking.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private final By patientSelect = By.id("patientSelect");
    private final By submitButton = By.id("login-submit-btn");
    private final By errorAlert = By.id("login-error-alert");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/login");
    }

    public void selectPatient(Long patientId) {
        selectByValue(patientSelect, String.valueOf(patientId));
    }

    public DoctorListPage clickLogin() {
        click(submitButton);
        return new DoctorListPage(driver);
    }

    public DoctorListPage loginAs(String baseUrl, Long patientId) {
        navigateTo(baseUrl);
        selectPatient(patientId);
        return clickLogin();
    }
}
