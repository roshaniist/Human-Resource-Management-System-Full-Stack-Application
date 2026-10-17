package com.example.emps.service;

import com.example.emps.dto.NotificationDto;
import com.example.emps.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationService {

    NotificationDto createNotification(
            Long recipientId,
            String title,
            String message,
            NotificationType type,
            String referenceType,
            Long referenceId
    );

    Page<NotificationDto> getMyNotifications(
            String email,
            Pageable pageable
    );

    Page<NotificationDto> getMyUnreadNotifications(
            String email,
            Pageable pageable
    );

    long getUnreadCount(String email);

    NotificationDto markAsRead(
            Long notificationId,
            String email
    );

    int markAllAsRead(String email);

    void deleteNotification(
            Long notificationId,
            String email
    );
}