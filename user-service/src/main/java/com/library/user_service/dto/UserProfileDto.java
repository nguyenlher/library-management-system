package com.library.user_service.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UserProfileDto {

    private Long id;
    private Long userId;

    @NotBlank(message = "Name is required")
    private String name;

    private String phone;
    private String address;

    @NotNull(message = "Borrow lock status is required")
    private Boolean borrowLock;

    @NotNull(message = "Total fine is required")
    private BigDecimal totalFine;

    // Constructors
    public UserProfileDto() {}

    public UserProfileDto(Long userId, String name, String phone, String address, Boolean borrowLock, BigDecimal totalFine) {
        this.userId = userId;
        this.name = name;
        this.phone = phone;
        this.address = address;
        this.borrowLock = borrowLock;
        this.totalFine = totalFine;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

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