package com.library.notification_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.library.notification_service.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserId(Long userId);

    List<Notification> findByUserIdAndStatus(Long userId, Notification.NotificationStatus status);

    List<Notification> findByType(Notification.NotificationType type);

    List<Notification> findByStatus(Notification.NotificationStatus status);

    List<Notification> findByTemplate(String template);

    List<Notification> findByUserIdAndType(Long userId, Notification.NotificationType type);
}