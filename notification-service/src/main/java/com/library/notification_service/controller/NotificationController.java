package com.library.notification_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.library.notification_service.dto.NotificationDTO;
import com.library.notification_service.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
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
}