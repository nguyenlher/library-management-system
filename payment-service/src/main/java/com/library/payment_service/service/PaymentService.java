package com.library.payment_service.service;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.library.payment_service.config.VNPayConfig;
import com.library.payment_service.dto.PaymentDTO;
import com.library.payment_service.entity.Payment;
import com.library.payment_service.entity.PaymentLog;
import com.library.payment_service.repository.PaymentLogRepository;
import com.library.payment_service.repository.PaymentRepository;

@Service
@Transactional
public class PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentRepository paymentRepository;
    private final PaymentLogRepository paymentLogRepository;
    private final VNPayConfig vnPayConfig;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate;

    public PaymentService(PaymentRepository paymentRepository,
                          PaymentLogRepository paymentLogRepository,
                          VNPayConfig vnPayConfig,
                          RestTemplate restTemplate) {
        this.paymentRepository = paymentRepository;
        this.paymentLogRepository = paymentLogRepository;
        this.vnPayConfig = vnPayConfig;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = restTemplate;
    }

    public Map<String, String> createVNPayPayment(Long userId, BigDecimal amount, Long referenceId, String orderInfo, String type, String ipAddress) throws Exception {
        // Determine payment type
        Payment.PaymentType paymentType = "fine".equals(type) ? Payment.PaymentType.FINE_PAYMENT : Payment.PaymentType.BORROW_FEE;
        
        // Create payment record
        Payment payment = new Payment(userId, amount, paymentType, Payment.PaymentMethod.VNPAY, referenceId);
        payment = paymentRepository.save(payment);

        // Build VNPay parameters
        String vnp_TxnRef = payment.getId();
        long amountInCents = amount.multiply(BigDecimal.valueOf(100)).longValue();

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", vnPayConfig.getVersion());
        vnp_Params.put("vnp_Command", vnPayConfig.getCommand());
        vnp_Params.put("vnp_TmnCode", vnPayConfig.getTmnCode());
        vnp_Params.put("vnp_Amount", String.valueOf(amountInCents));
        vnp_Params.put("vnp_CurrCode", vnPayConfig.getCurrency());
        
        // Add bank code for direct bank payment (NCB - Ngân hàng Quốc Dân)
        vnp_Params.put("vnp_BankCode", "NCB");
        
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", orderInfo);
        vnp_Params.put("vnp_OrderType", vnPayConfig.getOrderType());
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getReturnUrl());
        vnp_Params.put("vnp_IpAddr", ipAddress);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        // Build query string
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = vnp_Params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                // Build hash data
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                
                // Build query
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String queryUrl = query.toString();
        String vnp_SecureHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = vnPayConfig.getPayUrl() + "?" + queryUrl;

        log.info("========== VNPay Payment Request ==========");
        log.info("TmnCode: {}", vnPayConfig.getTmnCode());
        log.info("Amount (cents): {}", amountInCents);
        log.info("TxnRef: {}", vnp_TxnRef);
        log.info("Hash Data (Raw): {}", hashData.toString());
        log.info("Hash Secret: {}", vnPayConfig.getHashSecret());
        log.info("Secure Hash: {}", vnp_SecureHash);
        log.info("Full Payment URL: {}", paymentUrl);
        log.info("===========================================");

        // Log payment request
        PaymentLog paymentLog = new PaymentLog(
            UUID.randomUUID().toString(),
            payment.getId(),
            PaymentLog.GatewayType.VNPAY,
            vnp_TxnRef,
            objectMapper.writeValueAsString(vnp_Params)
        );
        paymentLogRepository.save(paymentLog);

        Map<String, String> result = new HashMap<>();
        result.put("paymentId", payment.getId());
        result.put("paymentUrl", paymentUrl);
        result.put("orderInfo", orderInfo);

        return result;
    }

    public PaymentDTO getPaymentById(String id) {
        return paymentRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public List<PaymentDTO> getPaymentsByUserId(Long userId) {
        return paymentRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .toList();
    }

    private PaymentDTO convertToDTO(Payment payment) {
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

    private String hmacSHA512(String key, String data) throws NoSuchAlgorithmException, InvalidKeyException {
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

    public Map<String, String> processVNPayReturn(Map<String, String> params) throws Exception {
        // Validate secure hash
        String vnp_SecureHash = params.get("vnp_SecureHash");
        params.remove("vnp_SecureHash");
        params.remove("vnp_SecureHashType");

        // Build hash data
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();

        for (String fieldName : fieldNames) {
            String fieldValue = params.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                if (fieldNames.indexOf(fieldName) < fieldNames.size() - 1) {
                    hashData.append('&');
                }
            }
        }

        String calculatedHash = hmacSHA512(vnPayConfig.getHashSecret(), hashData.toString());
        
        if (!calculatedHash.equals(vnp_SecureHash)) {
            throw new Exception("Invalid secure hash");
        }

        // Update payment status
        String paymentId = params.get("vnp_TxnRef");
        String responseCode = params.get("vnp_ResponseCode");
        
        Payment payment = paymentRepository.findById(paymentId).orElseThrow(() -> new Exception("Payment not found"));
        
        if ("00".equals(responseCode)) {
            payment.setStatus(Payment.PaymentStatus.SUCCESS);
            
            // Send success email notification
            sendPaymentSuccessEmail(payment);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }
        
        paymentRepository.save(payment);

        // Log response
        PaymentLog responseLog = new PaymentLog(
            UUID.randomUUID().toString(),
            payment.getId(),
            PaymentLog.GatewayType.VNPAY,
            paymentId,
            objectMapper.writeValueAsString(params)
        );
        paymentLogRepository.save(responseLog);

        Map<String, String> result = new HashMap<>();
        result.put("paymentId", paymentId);
        result.put("vnp_ResponseCode", responseCode);
        result.put("vnp_Amount", params.get("vnp_Amount"));
        result.put("vnp_TransactionNo", params.get("vnp_TransactionNo"));
        
        return result;
    }

    public void sendPaymentSuccessEmail(Payment payment) {
        try {
            log.info("Sending payment success email for payment: {}, type: {}, userId: {}", 
                     payment.getId(), payment.getType(), payment.getUserId());
            
            String endpoint;
            if (payment.getType() == Payment.PaymentType.BORROW_FEE) {
                endpoint = "/borrow-payment-success/" + payment.getUserId();
                log.info("Sending borrow payment success email to user: {}", payment.getUserId());
            } else {
                endpoint = "/fine-payment-success/" + payment.getUserId();
                log.info("Sending fine payment success email to user: {}", payment.getUserId());
            }

            String url = "http://localhost:8085/api/notifications" + endpoint +
                         "?amount=" + payment.getAmount().toString() +
                         "&paymentMethod=VNPAY" +
                         "&transactionId=" + payment.getId();
            
            log.info("Calling notification service URL: {}", url);
            String response = restTemplate.postForObject(url, null, String.class);
            log.info("Notification service response: {}", response);
            log.info("Payment success email sent successfully for payment: {}", payment.getId());
        } catch (Exception e) {
            log.error("Failed to send payment success email for payment: {}", payment.getId(), e);
        }
    }
}
