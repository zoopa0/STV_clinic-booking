package com.clinic.booking.unit;

import com.clinic.booking.appointment.service.CancellationFeeCalculator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CancellationFeeBvaTest {

    private CancellationFeeCalculator feeCalculator;

    @BeforeEach
    void setUp() {
        feeCalculator = new CancellationFeeCalculator();
    }

    @ParameterizedTest(name = "Notice hours: {0} -> Expected Fee: ${1}")
    @CsvSource({
            "100, 0.0",   // EP1 Nominal
            "49,  0.0",   // EP1 Just above boundary 48
            "48,  0.0",   // EP1 Exact boundary 48 ($0 fee)
            "47,  50.0",  // EP2 Just below boundary 48 ($50 fee)
            "30,  50.0",  // EP2 Nominal
            "25,  50.0",  // EP2 Just above boundary 24 ($50 fee)
            "24,  50.0",  // EP2 Exact boundary 24 ($50 fee)
            "23,  100.0", // EP3 Just below boundary 24 ($100 fee)
            "10,  100.0", // EP3 Nominal
            "1,   100.0", // EP3 Just above boundary 0 ($100 fee)
            "0,   100.0"  // EP3 Exact boundary 0 ($100 fee)
    })
    @DisplayName("FR2 EP & BVA Fee Calculation Test Cases")
    void testFeeCalculation_ValidBoundaries(int hours, double expectedFee) {
        double fee = feeCalculator.calculateFee(hours);
        assertThat(fee).isEqualTo(expectedFee);
    }

    @Test
    @DisplayName("FR2 BVA Invalid Boundary: -1 Hours Notice -> Exception")
    void testFeeCalculation_BoundaryNegativeOne() {
        assertThatThrownBy(() -> feeCalculator.calculateFee(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notice period hours cannot be negative");
    }

    @Test
    @DisplayName("FR2 EP Invalid Partition: -10 Hours Notice -> Exception")
    void testFeeCalculation_InvalidPartitionNegative() {
        assertThatThrownBy(() -> feeCalculator.calculateFee(-10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Notice period hours cannot be negative");
    }
}
