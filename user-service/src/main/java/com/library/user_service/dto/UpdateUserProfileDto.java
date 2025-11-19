package com.library.user_service.dto;

import java.math.BigDecimal;

public class UpdateUserProfileDto {

    private String name;
    private String phone;
    private String address;
    private Boolean borrowLock;
    private BigDecimal totalFine;

    // Constructors
    public UpdateUserProfileDto() {}

    public UpdateUserProfileDto(String name, String phone, String address, Boolean borrowLock, BigDecimal totalFine) {
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.borrowLock = borrowLock;
        this.totalFine = totalFine;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getBorrowLock() {
        return borrowLock;
    }

    public void setBorrowLock(Boolean borrowLock) {
        this.borrowLock = borrowLock;
    }

    public BigDecimal getTotalFine() {
        return totalFine;
    }

    public void setTotalFine(BigDecimal totalFine) {
        this.totalFine = totalFine;
    }
}