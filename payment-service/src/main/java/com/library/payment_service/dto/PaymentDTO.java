package com.library.payment_service.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.library.payment_service.entity.Payment;

public class PaymentDTO {

    private String id;
    private Long userId;
    private BigDecimal amount;
    private Payment.PaymentType type;
    private Payment.PaymentMethod method;
    private Payment.PaymentStatus status;
    private Long referenceId;
    private LocalDateTime createdAt;

    // Constructors
    public PaymentDTO() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Payment.PaymentType getType() {
        return type;
    }

    public void setType(Payment.PaymentType type) {
        this.type = type;
    }

    public Payment.PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(Payment.PaymentMethod method) {
        this.method = method;
    }

    public Payment.PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(Payment.PaymentStatus status) {
        this.status = status;
    }

    public Long getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(Long referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}