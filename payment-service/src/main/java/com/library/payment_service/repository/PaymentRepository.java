package com.library.payment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.payment_service.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByUserId(Long userId);

    List<Payment> findByUserIdAndStatus(Long userId, Payment.PaymentStatus status);

    List<Payment> findByReferenceId(Long referenceId);

    List<Payment> findByType(Payment.PaymentType type);

    List<Payment> findByMethod(Payment.PaymentMethod method);

    List<Payment> findByStatus(Payment.PaymentStatus status);
}