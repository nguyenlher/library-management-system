package com.library.notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.notification_service.dto.NotificationDTO;
import com.library.notification_service.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping("/borrow-payment-success/{userId}")
    public ResponseEntity<NotificationDTO> sendBorrowPaymentSuccessNotification(
            @PathVariable Long userId,
            @RequestParam String amount,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {

        System.out.println("Received borrow payment success notification request for userId: " + userId + ", amount: " + amount);

        NotificationDTO notification = notificationService.createBorrowPaymentSuccessNotification(
                userId, amount, paymentMethod, transactionId);

        // Automatically send the notification
        notificationService.sendNotification(notification.getId());

        return ResponseEntity.ok(notification);
    }

    @PostMapping("/fine-payment-success/{userId}")
    public ResponseEntity<NotificationDTO> sendFinePaymentSuccessNotification(
            @PathVariable Long userId,
            @RequestParam String amount,
            @RequestParam String paymentMethod,
            @RequestParam String transactionId) {

        NotificationDTO notification = notificationService.createFinePaymentSuccessNotification(
                userId, amount, paymentMethod, transactionId);

        // Automatically send the notification
        notificationService.sendNotification(notification.getId());

        return ResponseEntity.ok(notification);
    }

    @PostMapping("/fine-payment-reminder/{fineId}")
    public ResponseEntity<NotificationDTO> sendFinePaymentReminderNotification(@PathVariable Long fineId) {
        try {
            // Get fine details from borrow-service
            String fineUrl = "http://localhost:8086/fines/" + fineId;
            var fineResponse = new org.springframework.web.client.RestTemplate().getForObject(fineUrl, java.util.Map.class);
            
            if (fineResponse == null) {
                return ResponseEntity.notFound().build();
            }

            Long userId = Long.valueOf(fineResponse.get("userId").toString());
            Double amount = Double.valueOf(fineResponse.get("amount").toString());
            String reason = fineResponse.get("reason").toString();

            NotificationDTO notification = notificationService.createFinePaymentReminderNotification(
                    userId, String.format("%.0f", amount), reason);

            // Automatically send the notification
            notificationService.sendNotification(notification.getId());

            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            System.err.println("Failed to send fine payment reminder: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
}