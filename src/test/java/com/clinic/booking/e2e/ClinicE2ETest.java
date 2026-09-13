package com.clinic.booking.e2e;

import com.clinic.booking.e2e.pages.AppointmentsPage;
import com.clinic.booking.e2e.pages.DoctorListPage;
import com.clinic.booking.e2e.pages.LoginPage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ClinicE2ETest {

    @LocalServerPort
    private int port;

    private WebDriver driver;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--remote-allow-origins=*");

        File chromeFile = new File("C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe");
        if (chromeFile.exists()) {
            options.setBinary(chromeFile);
        }
        
        driver = new ChromeDriver(options);
        baseUrl = "http://localhost:" + port;
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("E2E Test 1: Patient Login -> View Doctors -> Book Appointment -> Confirm -> Attend")
    void testEndToEndBookingAndLifecycleFlow() {
        // Step 1: Login as John Doe (Patient ID = 1)
        LoginPage loginPage = new LoginPage(driver);
        DoctorListPage doctorListPage = loginPage.loginAs(baseUrl, 1L);

        // Step 2: Book available Doctor Jenkins (Doctor ID = 1) with 48h notice
        AppointmentsPage appointmentsPage = doctorListPage.bookDoctor(1L, 48);
        Long apptId = appointmentsPage.getLatestAppointmentId();

        // Step 3: Verify initial state REQUESTED
        assertThat(appointmentsPage.getAppointmentState(apptId)).isEqualTo("REQUESTED");

        // Step 4: Confirm appointment state transition (REQUESTED -> CONFIRMED)
        appointmentsPage.confirmAppointment(apptId);
        assertThat(appointmentsPage.getAppointmentState(apptId)).isEqualTo("CONFIRMED");

        // Step 5: Mark attended state transition (CONFIRMED -> ATTENDED)
        appointmentsPage.attendAppointment(apptId);
        assertThat(appointmentsPage.getAppointmentState(apptId)).isEqualTo("ATTENDED");
    }

    @Test
    @DisplayName("E2E Test 2: Unpaid Bills Patient Booking Rejection (Decision Table FR1)")
    void testUnpaidBillsBookingRejection() {
        // Login as Alice Smith (Patient ID = 2, hasUnpaidBills = true)
        LoginPage loginPage = new LoginPage(driver);
        DoctorListPage doctorListPage = loginPage.loginAs(baseUrl, 2L);

        // Attempt to book available Doctor Jenkins (Doctor ID = 1)
        doctorListPage.attemptBookDoctor(1L, 48);

        // Verify rejection error message on page
        assertThat(doctorListPage.getErrorMessage()).contains("Patient has unpaid bills");
    }

    @Test
    @DisplayName("E2E Test 3: Booking Cancellation with Fee Calculation (FR2 BVA)")
    void testBookingCancellationWithFee() {
        // Login as Bob Johnson (Patient ID = 3, Clean billing)
        LoginPage loginPage = new LoginPage(driver);
        DoctorListPage doctorListPage = loginPage.loginAs(baseUrl, 3L);

        // Book Doctor Chen (Doctor ID = 2) with 20 hours notice (BVA 0..23h -> $100 fee)
        AppointmentsPage appointmentsPage = doctorListPage.bookDoctor(2L, 20);
        Long apptId = appointmentsPage.getLatestAppointmentId();

        // Cancel appointment
        appointmentsPage.cancelAppointment(apptId);

        // Verify cancellation state and $100 fee applied
        assertThat(appointmentsPage.getAppointmentState(apptId)).isEqualTo("CANCELLED");
        assertThat(appointmentsPage.getAppointmentFee(apptId)).isEqualTo("$100.00");
    }
}
