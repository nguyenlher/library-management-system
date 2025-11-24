package com.library.payment_service.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.view.RedirectView;

import com.library.payment_service.config.VNPayConfig;
import com.library.payment_service.entity.Payment;
import com.library.payment_service.repository.PaymentRepository;
import com.library.payment_service.service.PaymentService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class VNPayReturnController {

    private static final Logger log = LoggerFactory.getLogger(VNPayReturnController.class);

    private final VNPayConfig vnPayConfig;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public VNPayReturnController(VNPayConfig vnPayConfig, PaymentRepository paymentRepository, PaymentService paymentService) {
        this.vnPayConfig = vnPayConfig;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
    }

    @GetMapping("/payments/vnpay/return")
    public RedirectView vnpayReturn(HttpServletRequest request) {
        try {
            Map<String, String> fields = new HashMap<>();
            Enumeration<String> params = request.getParameterNames();
            
            while (params.hasMoreElements()) {
                String fieldName = params.nextElement();
                String fieldValue = request.getParameter(fieldName);
                if (fieldValue != null && fieldValue.length() > 0) {
                    fields.put(fieldName, fieldValue);
                }
            }

            String vnp_SecureHash = fields.remove("vnp_SecureHash");
            String vnp_ResponseCode = fields.get("vnp_ResponseCode");
            String vnp_TxnRef = fields.get("vnp_TxnRef");
            String vnp_Amount = fields.get("vnp_Amount");
            String vnp_TransactionNo = fields.get("vnp_TransactionNo");

            // Build hash data
            String signValue = buildHashData(fields);
            String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), signValue);

            log.info("VNPay Return - TxnRef: {}, ResponseCode: {}, Amount: {}", vnp_TxnRef, vnp_ResponseCode, vnp_Amount);
            log.info("VNPay Return - Hash Received: {}", vnp_SecureHash);
            log.info("VNPay Return - Hash Calculated: {}", calculatedHash);

            if (calculatedHash.equals(vnp_SecureHash)) {
                if ("00".equals(vnp_ResponseCode)) {
                    // Payment successful - update payment status
                    paymentRepository.findById(vnp_TxnRef).ifPresent(payment -> {
                        payment.setStatus(Payment.PaymentStatus.SUCCESS);
                        payment.setTransactionId(vnp_TransactionNo);
                        paymentRepository.save(payment);
                        log.info("Payment {} marked as SUCCESS", vnp_TxnRef);
                        
                        // Send success email notification
                        paymentService.sendPaymentSuccessEmail(payment);
                    });

                    // Determine redirect URL based on payment type
                    String redirectUrl = "http://localhost:3000/payment/borrow/success?" + 
                        "paymentId=" + URLEncoder.encode(vnp_TxnRef, StandardCharsets.UTF_8) +
                        "&amount=" + URLEncoder.encode(vnp_Amount, StandardCharsets.UTF_8) +
                        "&transactionNo=" + URLEncoder.encode(vnp_TransactionNo, StandardCharsets.UTF_8);
                    
                    Payment payment = paymentRepository.findById(vnp_TxnRef).orElse(null);
                    if (payment != null && payment.getType() == Payment.PaymentType.FINE_PAYMENT) {
                        redirectUrl = "http://localhost:3000/payment/fine/success?" + 
                            "paymentId=" + URLEncoder.encode(vnp_TxnRef, StandardCharsets.UTF_8) +
                            "&amount=" + URLEncoder.encode(vnp_Amount, StandardCharsets.UTF_8) +
                            "&transactionNo=" + URLEncoder.encode(vnp_TransactionNo, StandardCharsets.UTF_8);
                    }

                    return new RedirectView(redirectUrl);
                } else {
                    // Payment failed
                    paymentRepository.findById(vnp_TxnRef).ifPresent(payment -> {
                        payment.setStatus(Payment.PaymentStatus.FAILED);
                        paymentRepository.save(payment);
                        log.info("Payment {} marked as FAILED", vnp_TxnRef);
                    });

                    // Determine redirect URL based on payment type
                    String redirectUrl = "http://localhost:3000/payment/borrow/failed?code=" + 
                        URLEncoder.encode(vnp_ResponseCode, StandardCharsets.UTF_8);
                    
                    Payment payment = paymentRepository.findById(vnp_TxnRef).orElse(null);
                    if (payment != null && payment.getType() == Payment.PaymentType.FINE_PAYMENT) {
                        redirectUrl = "http://localhost:3000/payment/fine/failed?code=" + 
                            URLEncoder.encode(vnp_ResponseCode, StandardCharsets.UTF_8);
                    }

                    return new RedirectView(redirectUrl);
                }
            } else {
                log.error("Invalid signature for payment {}", vnp_TxnRef);
                return new RedirectView("http://localhost:3000/payment/failed?code=97");
            }
        } catch (Exception e) {
            log.error("Error processing VNPay return", e);
            return new RedirectView("http://localhost:3000/payment/failed?code=99");
        }
    }

    private String buildHashData(Map<String, String> fields) throws Exception {
        java.util.List<String> fieldNames = new java.util.ArrayList<>(fields.keySet());
        java.util.Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        
        for (String fieldName : fieldNames) {
            String fieldValue = fields.get(fieldName);
            if (fieldValue != null && fieldValue.length() > 0) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }
        return hashData.toString();
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac sha512_HMAC = Mac.getInstance("HmacSHA512");
        SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
        sha512_HMAC.init(keySpec);
        byte[] result = sha512_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return byteArrayToHex(result);
    }

    private String byteArrayToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}
