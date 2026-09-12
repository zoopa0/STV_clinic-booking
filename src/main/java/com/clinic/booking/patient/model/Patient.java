package com.clinic.booking.patient.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "patients")
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean hasUnpaidBills;

    public Patient() {
    }

    public Patient(Long id, String name, boolean hasUnpaidBills) {
        this.id = id;
        this.name = name;
        this.hasUnpaidBills = hasUnpaidBills;
    }

    public Patient(String name, boolean hasUnpaidBills) {
        this.name = name;
        this.hasUnpaidBills = hasUnpaidBills;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isHasUnpaidBills() {
        return hasUnpaidBills;
    }

    public void setHasUnpaidBills(boolean hasUnpaidBills) {
        this.hasUnpaidBills = hasUnpaidBills;
    }
}
