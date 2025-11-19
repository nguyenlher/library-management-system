package com.library.payment_service.dto;

import com.library.payment_service.entity.PaymentLog;

import java.time.LocalDateTime;

public class PaymentLogDTO {

    private String id;
    private String paymentId;
    private PaymentLog.GatewayType gateway;
    private String gatewayTransactionId;
    private String payload;
    private LocalDateTime createdAt;

    // Constructors
    public PaymentLogDTO() {}

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public PaymentLog.GatewayType getGateway() {
        return gateway;
    }

    public void setGateway(PaymentLog.GatewayType gateway) {
        this.gateway = gateway;
    }

    public String getGatewayTransactionId() {
        return gatewayTransactionId;
    }

    public void setGatewayTransactionId(String gatewayTransactionId) {
        this.gatewayTransactionId = gatewayTransactionId;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}