package com.library.payment_service.config;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.library.payment_service.entity.Payment;
import com.library.payment_service.entity.PaymentLog;
import com.library.payment_service.repository.PaymentLogRepository;
import com.library.payment_service.repository.PaymentRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;

    public DataInitializer(PaymentRepository paymentRepository, PaymentLogRepository paymentLogRepository) {
        this.paymentRepository = paymentRepository;
        this.paymentLogRepository = paymentLogRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (paymentRepository.count() == 0) {
            createSampleData();
            System.out.println("Sample payment data initialized successfully!");
        } else {
            System.out.println("Payment data already exists, skipping initialization.");
        }
    }

    private void createSampleData() {
        LocalDateTime now = LocalDateTime.now();

        // Sample payment 1 - Successful borrow fee payment via VNPay
        Payment payment1 = new Payment();
        payment1.setUserId(1L);
        payment1.setAmount(BigDecimal.valueOf(2.50));
        payment1.setType(Payment.PaymentType.BORROW_FEE);
        payment1.setMethod(Payment.PaymentMethod.VNPAY);
        payment1.setStatus(Payment.PaymentStatus.SUCCESS);
        payment1.setReferenceId(1L); // Reference to borrow ID
        payment1.setCreatedAt(now.minusDays(5));
        paymentRepository.save(payment1);

        // Log for payment 1
        PaymentLog log1 = new PaymentLog();
        log1.setId(UUID.randomUUID().toString());
            log1.setPaymentId(payment1.getId());
        log1.setGateway(PaymentLog.GatewayType.VNPAY);
        log1.setGatewayTransactionId("VNPAY123456789");
        log1.setPayload("{\"amount\":2.50,\"currency\":\"VND\",\"status\":\"success\"}");
        log1.setCreatedAt(now.minusDays(5));
        paymentLogRepository.save(log1);

        // Sample payment 2 - Successful fine payment via Cash
        Payment payment2 = new Payment();
        payment2.setUserId(3L);
        payment2.setAmount(BigDecimal.valueOf(2.00));
        payment2.setType(Payment.PaymentType.FINE_PAYMENT);
        payment2.setMethod(Payment.PaymentMethod.CASH);
        payment2.setStatus(Payment.PaymentStatus.SUCCESS);
        payment2.setReferenceId(1L); // Reference to fine ID
        payment2.setCreatedAt(now.minusDays(3));
        paymentRepository.save(payment2);

        // Log for payment 2
        PaymentLog log2 = new PaymentLog();
        log2.setId(UUID.randomUUID().toString());
            log2.setPaymentId(payment2.getId());
        log2.setGateway(PaymentLog.GatewayType.CASH);
        log2.setGatewayTransactionId("CASH-" + payment2.getId());
        log2.setPayload("{\"method\":\"cash\",\"amount\":2.00,\"received_by\":\"librarian\"}");
        log2.setCreatedAt(now.minusDays(3));
        paymentLogRepository.save(log2);

        // Sample payment 3 - Failed VNPay payment
        Payment payment3 = new Payment();
        payment3.setUserId(4L);
        payment3.setAmount(BigDecimal.valueOf(20.00));
        payment3.setType(Payment.PaymentType.FINE_PAYMENT);
        payment3.setMethod(Payment.PaymentMethod.VNPAY);
        payment3.setStatus(Payment.PaymentStatus.FAILED);
        payment3.setReferenceId(2L); // Reference to fine ID
        payment3.setCreatedAt(now.minusDays(1));
        paymentRepository.save(payment3);

        // Log for payment 3 (failed)
        PaymentLog log3 = new PaymentLog();
        log3.setId(UUID.randomUUID().toString());
            log3.setPaymentId(payment3.getId());
        log3.setGateway(PaymentLog.GatewayType.VNPAY);
        log3.setGatewayTransactionId("VNPAY987654321");
        log3.setPayload("{\"amount\":20.00,\"currency\":\"VND\",\"status\":\"failed\",\"error\":\"insufficient_funds\"}");
        log3.setCreatedAt(now.minusDays(1));
        paymentLogRepository.save(log3);

        // Sample payment 4 - Pending borrow fee payment
        Payment payment4 = new Payment();
        payment4.setUserId(5L);
        payment4.setAmount(BigDecimal.valueOf(3.25));
        payment4.setType(Payment.PaymentType.BORROW_FEE);
        payment4.setMethod(Payment.PaymentMethod.VNPAY);
        payment4.setStatus(Payment.PaymentStatus.PENDING);
        payment4.setReferenceId(5L); // Reference to borrow ID
        payment4.setCreatedAt(now.minusHours(2));
        paymentRepository.save(payment4);

        // Log for payment 4 (initiated)
        PaymentLog log4 = new PaymentLog();
        log4.setId(UUID.randomUUID().toString());
            log4.setPaymentId(payment4.getId());
        log4.setGateway(PaymentLog.GatewayType.VNPAY);
        log4.setGatewayTransactionId(null);
        log4.setPayload("{\"amount\":3.25,\"currency\":\"VND\",\"status\":\"pending\"}");
        log4.setCreatedAt(now.minusHours(2));
        paymentLogRepository.save(log4);

        // Sample payment 5 - Successful fine payment via VNPay
        Payment payment5 = new Payment();
        payment5.setUserId(2L);
        payment5.setAmount(BigDecimal.valueOf(1.50));
        payment5.setType(Payment.PaymentType.FINE_PAYMENT);
        payment5.setMethod(Payment.PaymentMethod.VNPAY);
        payment5.setStatus(Payment.PaymentStatus.SUCCESS);
        payment5.setReferenceId(3L); // Reference to fine ID
        payment5.setCreatedAt(now.minusDays(7));
        paymentRepository.save(payment5);

        // Log for payment 5
        PaymentLog log5 = new PaymentLog();
        log5.setId(UUID.randomUUID().toString());
            log5.setPaymentId(payment5.getId());
        log5.setGateway(PaymentLog.GatewayType.VNPAY);
        log5.setGatewayTransactionId("VNPAY555666777");
        log5.setPayload("{\"amount\":1.50,\"currency\":\"VND\",\"status\":\"success\"}");
        log5.setCreatedAt(now.minusDays(7));
        paymentLogRepository.save(log5);
    }
}