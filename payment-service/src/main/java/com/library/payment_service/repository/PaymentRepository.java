package com.library.payment_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.payment_service.entity.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByReferenceId(Long referenceId);
}
