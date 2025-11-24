package com.library.payment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.payment_service.entity.PaymentLog;

@Repository
public interface PaymentLogRepository extends JpaRepository<PaymentLog, String> {
    List<PaymentLog> findByPaymentId(String paymentId);
}
