package com.clinic.booking.e2e.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class AppointmentsPage extends BasePage {

    private final By successMessageText = By.id("success-message-text");
    private final By errorMessageText = By.id("error-message-text");
    private final By appointmentsTable = By.id("appointments-table");
    private final By appointmentRows = By.cssSelector("#appointments-table tbody tr");

    public AppointmentsPage(WebDriver driver) {
        super(driver);
    }

    public Long getLatestAppointmentId() {
        waitForElementVisible(appointmentsTable);
        List<WebElement> rows = driver.findElements(appointmentRows);
        if (rows.isEmpty()) {
            throw new RuntimeException("No appointment rows found in table");
        }
        WebElement lastRow = rows.get(rows.size() - 1);
        String idText = lastRow.findElement(By.cssSelector("td:first-child")).getText().replace("#", "").trim();
        return Long.parseLong(idText);
    }

    public String getAppointmentState(Long appointmentId) {
        By badge = By.id("state-badge-" + appointmentId);
        return getText(badge).trim();
    }

    public String getAppointmentFee(Long appointmentId) {
        By feeCell = By.id("fee-cell-" + appointmentId);
        return getText(feeCell).trim();
    }

    public void confirmAppointment(Long appointmentId) {
        By confirmBtn = By.id("confirm-btn-" + appointmentId);
        click(confirmBtn);
    }

    public void attendAppointment(Long appointmentId) {
        By attendBtn = By.id("attend-btn-" + appointmentId);
        click(attendBtn);
    }

    public void cancelAppointment(Long appointmentId) {
        By cancelBtn = By.id("cancel-btn-" + appointmentId);
        click(cancelBtn);
    }

    public String getSuccessMessage() {
        return getText(successMessageText);
    }

    public String getErrorMessage() {
        return getText(errorMessageText);
    }
}
