package com.library.notification_service.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.library.notification_service.entity.Notification;
import com.library.notification_service.repository.NotificationRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final NotificationRepository notificationRepository;

    public DataInitializer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (notificationRepository.count() == 0) {
            createSampleData();
            System.out.println("Sample notification data initialized successfully!");
        } else {
            System.out.println("Notification data already exists, skipping initialization.");
        }
    }

    private void createSampleData() {
        LocalDateTime now = LocalDateTime.now();

        // Sample notification 1 - Book borrowed (Email, Sent)
        Notification notification1 = new Notification();
        notification1.setUserId(1L);
        notification1.setType(Notification.NotificationType.EMAIL);
        notification1.setTemplate("BOOK_BORROWED");
        notification1.setPayload("{\"bookTitle\":\"Harry Potter and the Philosopher's Stone\",\"dueDate\":\"2025-12-04\"}");
        notification1.setStatus(Notification.NotificationStatus.SENT);
        notification1.setCreatedAt(now.minusDays(5));
        notificationRepository.save(notification1);

        // Sample notification 2 - Book overdue (Email, Pending)
        Notification notification2 = new Notification();
        notification2.setUserId(3L);
        notification2.setType(Notification.NotificationType.EMAIL);
        notification2.setTemplate("BOOK_OVERDUE");
        notification2.setPayload("{\"bookTitle\":\"Murder on the Orient Express\",\"daysOverdue\":4}");
        notification2.setStatus(Notification.NotificationStatus.PENDING);
        notification2.setCreatedAt(now.minusDays(2));
        notificationRepository.save(notification2);

        // Sample notification 3 - Fine payment (SMS, Sent)
        Notification notification3 = new Notification();
        notification3.setUserId(3L);
        notification3.setType(Notification.NotificationType.SMS);
        notification3.setTemplate("FINE_PAYMENT");
        notification3.setPayload("{\"amount\":\"2.00\",\"paymentMethod\":\"CASH\"}");
        notification3.setStatus(Notification.NotificationStatus.SENT);
        notification3.setCreatedAt(now.minusDays(3));
        notificationRepository.save(notification3);

        // Sample notification 4 - Book returned (Email, Failed)
        Notification notification4 = new Notification();
        notification4.setUserId(2L);
        notification4.setType(Notification.NotificationType.EMAIL);
        notification4.setTemplate("BOOK_RETURNED");
        notification4.setPayload("{\"bookTitle\":\"A Game of Thrones\"}");
        notification4.setStatus(Notification.NotificationStatus.FAILED);
        notification4.setCreatedAt(now.minusDays(10));
        notificationRepository.save(notification4);

        // Sample notification 5 - Book borrowed (SMS, Sent)
        Notification notification5 = new Notification();
        notification5.setUserId(5L);
        notification5.setType(Notification.NotificationType.SMS);
        notification5.setTemplate("BOOK_BORROWED");
        notification5.setPayload("{\"bookTitle\":\"Kafka on the Shore\",\"dueDate\":\"2025-12-02\"}");
        notification5.setStatus(Notification.NotificationStatus.SENT);
        notification5.setCreatedAt(now.minusDays(1));
        notificationRepository.save(notification5);

        // Sample notification 6 - Overdue reminder (Email, Pending)
        Notification notification6 = new Notification();
        notification6.setUserId(4L);
        notification6.setType(Notification.NotificationType.EMAIL);
        notification6.setTemplate("BOOK_OVERDUE");
        notification6.setPayload("{\"bookTitle\":\"The Shining\",\"daysOverdue\":1}");
        notification6.setStatus(Notification.NotificationStatus.PENDING);
        notification6.setCreatedAt(now.minusHours(6));
        notificationRepository.save(notification6);

        // Sample notification 7 - Fine payment reminder (SMS, Cancelled)
        Notification notification7 = new Notification();
        notification7.setUserId(4L);
        notification7.setType(Notification.NotificationType.SMS);
        notification7.setTemplate("FINE_PAYMENT_REMINDER");
        notification7.setPayload("{\"outstandingAmount\":\"20.00\",\"dueDate\":\"2025-11-25\"}");
        notification7.setStatus(Notification.NotificationStatus.CANCELLED);
        notification7.setCreatedAt(now.minusDays(7));
        notificationRepository.save(notification7);

        // Sample notification 8 - Welcome message (Email, Sent)
        Notification notification8 = new Notification();
        notification8.setUserId(1L);
        notification8.setType(Notification.NotificationType.EMAIL);
        notification8.setTemplate("WELCOME");
        notification8.setPayload("{\"userName\":\"Administrator\",\"libraryName\":\"City Library\"}");
        notification8.setStatus(Notification.NotificationStatus.SENT);
        notification8.setCreatedAt(now.minusDays(30));
        notificationRepository.save(notification8);

        // Sample notification 9 - Account suspension warning (Email, Pending)
        Notification notification9 = new Notification();
        notification9.setUserId(4L);
        notification9.setType(Notification.NotificationType.EMAIL);
        notification9.setTemplate("ACCOUNT_SUSPENSION_WARNING");
        notification9.setPayload("{\"reason\":\"Outstanding fines\",\"amount\":\"20.00\",\"suspensionDate\":\"2025-11-30\"}");
        notification9.setStatus(Notification.NotificationStatus.PENDING);
        notification9.setCreatedAt(now.minusHours(12));
        notificationRepository.save(notification9);

        // Sample notification 10 - Book reservation available (SMS, Sent)
        Notification notification10 = new Notification();
        notification10.setUserId(2L);
        notification10.setType(Notification.NotificationType.SMS);
        notification10.setTemplate("BOOK_RESERVATION_AVAILABLE");
        notification10.setPayload("{\"bookTitle\":\"Harry Potter and the Chamber of Secrets\",\"availableUntil\":\"2025-11-22\"}");
        notification10.setStatus(Notification.NotificationStatus.SENT);
        notification10.setCreatedAt(now.minusDays(4));
        notificationRepository.save(notification10);
    }
}