package com.library.notification_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.library.notification_service.dto.NotificationDTO;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.repository.NotificationRepository;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    // Notification operations
    public List<NotificationDTO> getAllNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<NotificationDTO> getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .map(this::convertToDTO);
    }

    public List<NotificationDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByUserAndStatus(Long userId, Notification.NotificationStatus status) {
        return notificationRepository.findByUserIdAndStatus(userId, status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByType(Notification.NotificationType type) {
        return notificationRepository.findByType(type).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getNotificationsByStatus(Notification.NotificationStatus status) {
        return notificationRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<NotificationDTO> getPendingNotifications() {
        return getNotificationsByStatus(Notification.NotificationStatus.PENDING);
    }

    public NotificationDTO createNotification(Long userId, Notification.NotificationType type,
                                             String template, String payload) {
        Notification notification = new Notification(userId, type, template, payload);
        Notification savedNotification = notificationRepository.save(notification);
        return convertToDTO(savedNotification);
    }

    public NotificationDTO sendNotification(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new IllegalArgumentException("Notification not found");
        }

        Notification notification = notificationOpt.get();
        if (notification.getStatus() != Notification.NotificationStatus.PENDING) {
            throw new IllegalStateException("Notification is not in pending status");
        }

        // Simulate sending notification (in real implementation, integrate with email/SMS service)
        boolean success = simulateSendNotification(notification);

        if (success) {
            notification.setStatus(Notification.NotificationStatus.SENT);
        } else {
            notification.setStatus(Notification.NotificationStatus.FAILED);
        }

        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    public NotificationDTO cancelNotification(Long notificationId) {
        Optional<Notification> notificationOpt = notificationRepository.findById(notificationId);
        if (notificationOpt.isEmpty()) {
            throw new IllegalArgumentException("Notification not found");
        }

        Notification notification = notificationOpt.get();
        if (notification.getStatus() != Notification.NotificationStatus.PENDING) {
            throw new IllegalStateException("Only pending notifications can be cancelled");
        }

        notification.setStatus(Notification.NotificationStatus.CANCELLED);
        Notification updatedNotification = notificationRepository.save(notification);
        return convertToDTO(updatedNotification);
    }

    public void retryFailedNotifications() {
        List<Notification> failedNotifications = notificationRepository.findByStatus(Notification.NotificationStatus.FAILED);

        for (Notification notification : failedNotifications) {
            // Reset to pending for retry
            notification.setStatus(Notification.NotificationStatus.PENDING);
            notificationRepository.save(notification);
        }
    }

    public int sendPendingNotifications() {
        List<Notification> pendingNotifications = notificationRepository.findByStatus(Notification.NotificationStatus.PENDING);
        int sentCount = 0;

        for (Notification notification : pendingNotifications) {
            boolean success = simulateSendNotification(notification);
            if (success) {
                notification.setStatus(Notification.NotificationStatus.SENT);
                sentCount++;
            } else {
                notification.setStatus(Notification.NotificationStatus.FAILED);
            }
            notificationRepository.save(notification);
        }

        return sentCount;
    }

    // Template methods for different notification types
    public NotificationDTO createBookBorrowedNotification(Long userId, String bookTitle, String dueDate) {
        String template = "BOOK_BORROWED";
        String payload = String.format("{\"bookTitle\":\"%s\",\"dueDate\":\"%s\"}", bookTitle, dueDate);
        return createNotification(userId, Notification.NotificationType.EMAIL, template, payload);
    }

    public NotificationDTO createBookOverdueNotification(Long userId, String bookTitle, int daysOverdue) {
        String template = "BOOK_OVERDUE";
        String payload = String.format("{\"bookTitle\":\"%s\",\"daysOverdue\":%d}", bookTitle, daysOverdue);
        return createNotification(userId, Notification.NotificationType.EMAIL, template, payload);
    }

    public NotificationDTO createFinePaymentNotification(Long userId, String amount, String paymentMethod) {
        String template = "FINE_PAYMENT";
        String payload = String.format("{\"amount\":\"%s\",\"paymentMethod\":\"%s\"}", amount, paymentMethod);
        return createNotification(userId, Notification.NotificationType.SMS, template, payload);
    }

    public NotificationDTO createBookReturnedNotification(Long userId, String bookTitle) {
        String template = "BOOK_RETURNED";
        String payload = String.format("{\"bookTitle\":\"%s\"}", bookTitle);
        return createNotification(userId, Notification.NotificationType.EMAIL, template, payload);
    }

    // Helper methods
    private boolean simulateSendNotification(Notification notification) {
        // Simulate 90% success rate for demo purposes
        // In real implementation, integrate with actual email/SMS services
        return Math.random() > 0.1;
    }

    private NotificationDTO convertToDTO(Notification notification) {
        NotificationDTO dto = new NotificationDTO();
        dto.setId(notification.getId());
        dto.setUserId(notification.getUserId());
        dto.setType(notification.getType());
        dto.setTemplate(notification.getTemplate());
        dto.setPayload(notification.getPayload());
        dto.setStatus(notification.getStatus());
        dto.setCreatedAt(notification.getCreatedAt());
        return dto;
    }
}