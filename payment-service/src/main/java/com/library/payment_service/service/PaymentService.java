package com.library.payment_service.service;

import com.library.payment_service.dto.PaymentDTO;
import com.library.payment_service.dto.PaymentLogDTO;
import com.library.payment_service.entity.Payment;
import com.library.payment_service.entity.PaymentLog;
import com.library.payment_service.repository.PaymentRepository;
import com.library.payment_service.repository.PaymentLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;

    public PaymentService(PaymentRepository paymentRepository, PaymentLogRepository paymentLogRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentLogRepository = paymentLogRepository;
    }

    // Payment operations
    public List<PaymentDTO> getAllPayments() {
        return paymentRepository.findAll().stream()
                .map(this::convertPaymentToDTO)
                .collect(Collectors.toList());
    }

    public Optional<PaymentDTO> getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .map(this::convertPaymentToDTO);
    }

    public List<PaymentDTO> getPaymentsByUser(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::convertPaymentToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByUserAndStatus(Long userId, Payment.PaymentStatus status) {
        return paymentRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::convertPaymentToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByReference(Long referenceId) {
        return paymentRepository.findByReferenceId(referenceId).stream()
                .map(this::convertPaymentToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentDTO> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status).stream()
                .map(this::convertPaymentToDTO)
                .collect(Collectors.toList());
    }

    public PaymentDTO createPayment(Long userId, BigDecimal amount, Payment.PaymentType type,
                                   Payment.PaymentMethod method, Long referenceId) {
        Payment payment = new Payment(userId, amount, type, method, referenceId);
        Payment savedPayment = paymentRepository.save(payment);

        // Log payment creation
        logPaymentEvent(savedPayment.getId().toString(), method == Payment.PaymentMethod.VNPAY ?
                       PaymentLog.GatewayType.VNPAY : PaymentLog.GatewayType.CASH,
                       null, "Payment created");

        return convertPaymentToDTO(savedPayment);
    }

    public PaymentDTO processVNPayPayment(Long paymentId, String transactionId, String payload) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }

        // Simulate VNPay processing (in real implementation, verify with VNPay API)
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        Payment updatedPayment = paymentRepository.save(payment);

        // Log successful payment
        logPaymentEvent(paymentId.toString(), PaymentLog.GatewayType.VNPAY,
                       transactionId, payload);

        return convertPaymentToDTO(updatedPayment);
    }

    public PaymentDTO processCashPayment(Long paymentId) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new IllegalStateException("Payment is not in pending status");
        }

        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        Payment updatedPayment = paymentRepository.save(payment);

        // Log cash payment
        logPaymentEvent(paymentId.toString(), PaymentLog.GatewayType.CASH,
                       "CASH-" + paymentId, "Cash payment processed");

        return convertPaymentToDTO(updatedPayment);
    }

    public PaymentDTO failPayment(Long paymentId, String reason) {
        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);
        if (paymentOpt.isEmpty()) {
            throw new IllegalArgumentException("Payment not found");
        }

        Payment payment = paymentOpt.get();
        payment.setStatus(Payment.PaymentStatus.FAILED);
        Payment updatedPayment = paymentRepository.save(payment);

        // Log failed payment
        logPaymentEvent(paymentId.toString(),
                       payment.getMethod() == Payment.PaymentMethod.VNPAY ?
                       PaymentLog.GatewayType.VNPAY : PaymentLog.GatewayType.CASH,
                       null, "Payment failed: " + reason);

        return convertPaymentToDTO(updatedPayment);
    }

    // Payment log operations
    public List<PaymentLogDTO> getAllPaymentLogs() {
        return paymentLogRepository.findAll().stream()
                .map(this::convertLogToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentLogDTO> getLogsByPayment(String paymentId) {
        return paymentLogRepository.findByPaymentId(paymentId).stream()
                .map(this::convertLogToDTO)
                .collect(Collectors.toList());
    }

    public List<PaymentLogDTO> getLogsByGateway(PaymentLog.GatewayType gateway) {
        return paymentLogRepository.findByGateway(gateway).stream()
                .map(this::convertLogToDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private void logPaymentEvent(String paymentId, PaymentLog.GatewayType gateway,
                                String transactionId, String payload) {
        String logId = UUID.randomUUID().toString();
        PaymentLog log = new PaymentLog(logId, paymentId, gateway, transactionId, payload);
        paymentLogRepository.save(log);
    }

    private PaymentDTO convertPaymentToDTO(Payment payment) {
        PaymentDTO dto = new PaymentDTO();
        dto.setId(payment.getId());
        dto.setUserId(payment.getUserId());
        dto.setAmount(payment.getAmount());
        dto.setType(payment.getType());
        dto.setMethod(payment.getMethod());
        dto.setStatus(payment.getStatus());
        dto.setReferenceId(payment.getReferenceId());
        dto.setCreatedAt(payment.getCreatedAt());
        return dto;
    }

    private PaymentLogDTO convertLogToDTO(PaymentLog log) {
        PaymentLogDTO dto = new PaymentLogDTO();
        dto.setId(log.getId());
        dto.setPaymentId(log.getPaymentId());
        dto.setGateway(log.getGateway());
        dto.setGatewayTransactionId(log.getGatewayTransactionId());
        dto.setPayload(log.getPayload());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}