package com.clinic.booking.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DoctorListPage extends BasePage {

    private final By successMessageText = By.id("success-message-text");
    private final By errorMessageText = By.id("error-message-text");

    public DoctorListPage(WebDriver driver) {
        super(driver);
    }

    public AppointmentsPage bookDoctor(Long doctorId, int hoursNotice) {
        By hoursInput = By.id("hours-input-" + doctorId);
        By bookBtn = By.id("book-btn-" + doctorId);

        type(hoursInput, String.valueOf(hoursNotice));
        click(bookBtn);
        return new AppointmentsPage(driver);
    }

    public void attemptBookDoctor(Long doctorId, int hoursNotice) {
        By hoursInput = By.id("hours-input-" + doctorId);
        By bookBtn = By.id("book-btn-" + doctorId);

        type(hoursInput, String.valueOf(hoursNotice));
        click(bookBtn);
    }

    public boolean isDoctorAvailable(Long doctorId) {
        By availableBadge = By.id("status-available-" + doctorId);
        return isElementPresent(availableBadge);
    }

    public String getErrorMessage() {
        return getText(errorMessageText);
    }

    public String getSuccessMessage() {
        return getText(successMessageText);
    }
}
