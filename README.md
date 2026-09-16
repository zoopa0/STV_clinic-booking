# Clinic Booking Application

A Spring Boot Modular Monolith Application for clinic appointment bookings, built for the Software Testing and Validation Final Project.

## Group Members (Addis Ababa University)
- **Ayansa Adugna** - ATE/6100/14
- **Biruk Tesfa** - ATE/5576/14
- **Fuad Temam** - ATE/4704/14
- **Ebisa Daba** - ATE/6263/14

## Features
- Patient and Doctor management.
- Appointment booking with state transitions.
- Cancellation fee calculation.
- E2E testing with Selenium.

## Setup & Running the Application
1. **Build the project:**
   ```bash
   mvn clean install
   ```
2. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```
   The application will be accessible at `http://localhost:8080`.

## Running the Test Suites
The project implements testing at multiple levels (Unit, Integration, System/E2E).

1. **Run all tests (including Selenium E2E):**
   ```bash
   mvn clean test
   ```
2. **Generate JaCoCo Coverage Report:**
   ```bash
   mvn clean verify
   ```
   *The coverage report can be found at `target/site/jacoco/index.html`. We enforce an 80% branch coverage minimum on core logic.*

## Continuous Integration Pipelines
- **GitHub Actions:** Located in `.github/workflows/ci.yml`. It runs automatically on every push to `main`, installing dependencies, running all tests, and generating coverage reports.
- **Jenkins:** Located in `Jenkinsfile`. It is configured to run tests inside a Dockerized Jenkins agent environment.
