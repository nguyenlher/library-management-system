package com.library.borrow_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.library.borrow_service.entity.BorrowFine;

public class BorrowFineDTO {

    private Long id;
    private Long borrowId;
    private Long userId;
    private BigDecimal amount;
    private BorrowFine.FineReason reason;
    private Boolean paid;
    private LocalDateTime createdAt;

    // Constructors
    public BorrowFineDTO() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBorrowId() {
        return borrowId;
    }

    public void setBorrowId(Long borrowId) {
        this.borrowId = borrowId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BorrowFine.FineReason getReason() {
        return reason;
    }

    public void setReason(BorrowFine.FineReason reason) {
        this.reason = reason;
    }

    public Boolean getPaid() {
        return paid;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}