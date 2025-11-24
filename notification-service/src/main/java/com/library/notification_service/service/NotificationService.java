package com.library.notification_service.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.library.notification_service.dto.NotificationDTO;
import com.library.notification_service.entity.Notification;
import com.library.notification_service.repository.NotificationRepository;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;
    private final RestTemplate restTemplate;

    public NotificationService(NotificationRepository notificationRepository, JavaMailSender mailSender, RestTemplate restTemplate) {
        this.notificationRepository = notificationRepository;
        this.mailSender = mailSender;
        this.restTemplate = restTemplate;
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

    public NotificationDTO createBorrowPaymentSuccessNotification(Long userId, String amount, String paymentMethod, String transactionId) {
        String template = "BORROW_PAYMENT_SUCCESS";
        String payload = String.format("{\"amount\":\"%s\",\"paymentMethod\":\"%s\",\"transactionId\":\"%s\"}", amount, paymentMethod, transactionId);
        return createNotification(userId, Notification.NotificationType.EMAIL, template, payload);
    }

    public NotificationDTO createFinePaymentSuccessNotification(Long userId, String amount, String paymentMethod, String transactionId) {
        String template = "FINE_PAYMENT_SUCCESS";
        String payload = String.format("{\"amount\":\"%s\",\"paymentMethod\":\"%s\",\"transactionId\":\"%s\"}", amount, paymentMethod, transactionId);
        return createNotification(userId, Notification.NotificationType.EMAIL, template, payload);
    }

    public NotificationDTO createFinePaymentReminderNotification(Long userId, String amount, String fineReason) {
        String template = "FINE_PAYMENT_REMINDER";
        String payload = String.format("{\"amount\":\"%s\",\"fineReason\":\"%s\"}", amount, fineReason);
        return createNotification(userId, Notification.NotificationType.EMAIL, template, payload);
    }

    // Helper methods
    private boolean simulateSendNotification(Notification notification) {
        // Send actual email based on notification type and template
        if (notification.getType() == Notification.NotificationType.EMAIL) {
            return sendEmailNotification(notification);
        }
        // For SMS or other types, could integrate with SMS service
        return true; // Assume success for non-email types
    }

    private boolean sendEmailNotification(Notification notification) {
        try {
            String userEmail = getUserEmail(notification.getUserId());
            if (userEmail == null || userEmail.isEmpty()) {
                System.err.println("No email found for user " + notification.getUserId());
                return false;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("nguyenlh2004@gmail.com");
            message.setTo(userEmail);
            message.setSubject(getEmailSubject(notification.getTemplate()));
            message.setText(getEmailContent(notification));

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to send email: " + e.getMessage());
            return false;
        }
    }

    private String getUserEmail(Long userId) {
        try {
            String url = "http://localhost:8083/users/" + userId + "/email";
            return restTemplate.getForObject(url, String.class);
        } catch (Exception e) {
            System.err.println("Failed to get email for user " + userId + ": " + e.getMessage());
            return null;
        }
    }

    private String getEmailSubject(String template) {
        switch (template) {
            case "BOOK_BORROWED":
                return "Book Borrowed Successfully";
            case "BOOK_OVERDUE":
                return "Book Overdue Notice";
            case "FINE_PAYMENT":
                return "Fine Payment Confirmation";
            case "BOOK_RETURNED":
                return "Book Returned Successfully";
            case "BORROW_PAYMENT_SUCCESS":
                return "Borrow Payment Successful";
            case "FINE_PAYMENT_SUCCESS":
                return "Fine Payment Successful";
            case "FINE_PAYMENT_REMINDER":
                return "Nhắc nhở thanh toán phí phạt";
            default:
                return "Library Notification";
        }
    }

    private String getEmailContent(Notification notification) {
        String template = notification.getTemplate();

        switch (template) {
            case "BORROW_PAYMENT_SUCCESS":
                return "Chào bạn,\n\nThanh toán phí mượn sách của bạn đã được xử lý thành công.\n\nCảm ơn bạn đã sử dụng hệ thống thư viện của chúng tôi.";
            case "FINE_PAYMENT_SUCCESS":
                return "Chào bạn,\n\nThanh toán phí phạt của bạn đã được xử lý thành công.\n\nTài khoản của bạn đã được mở khóa. Cảm ơn bạn đã sử dụng hệ thống thư viện của chúng tôi.";
            case "FINE_PAYMENT_REMINDER":
                return "Chào bạn,\n\nBạn còn có phí phạt chưa thanh toán trong hệ thống thư viện.\n\nVui lòng thanh toán phí phạt để có thể tiếp tục mượn sách. Nếu bạn đã thanh toán, vui lòng bỏ qua email này.\n\nCảm ơn bạn đã sử dụng hệ thống thư viện của chúng tôi.";
            case "BOOK_BORROWED":
                return "Chào bạn,\n\nBạn đã mượn sách thành công.\n\nVui lòng trả sách trước ngày hạn.";
            case "BOOK_OVERDUE":
                return "Chào bạn,\n\nSách bạn mượn đã quá hạn.\n\nVui lòng trả sách càng sớm càng tốt.";
            case "FINE_PAYMENT":
                return "Chào bạn,\n\nĐã ghi nhận thanh toán phí phạt.";
            case "BOOK_RETURNED":
                return "Chào bạn,\n\nBạn đã trả sách thành công.";
            default:
                return "Thông báo thư viện: ";
        }
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