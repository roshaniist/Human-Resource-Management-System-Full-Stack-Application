package com.example.emps.service.impl;

import com.example.emps.dto.NotificationDto;
import com.example.emps.entity.Notification;
import com.example.emps.entity.User;
import com.example.emps.entity.UserSettings;
import com.example.emps.enums.NotificationType;
import com.example.emps.exception.ResourceNotFoundException;
import com.example.emps.repository.NotificationRepository;
import com.example.emps.repository.UserRepository;
import com.example.emps.repository.UserSettingsRepository;
import com.example.emps.service.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserSettingsRepository userSettingsRepository;

    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UserRepository userRepository,
            UserSettingsRepository userSettingsRepository) {

        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.userSettingsRepository = userSettingsRepository;
    }

    @Override
    public NotificationDto createNotification(
            Long recipientId,
            String title,
            String message,
            NotificationType type,
            String referenceType,
            Long referenceId) {

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Recipient user not found with id: " + recipientId
                        )
                );

        if (!isNotificationEnabled(recipientId, type)) {
            return null;
        }

        Notification notification = new Notification();

        notification.setRecipient(recipient);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type.name());
        notification.setReferenceType(referenceType);
        notification.setReferenceId(referenceId);
        notification.setRead(false);

        Notification savedNotification =
                notificationRepository.save(notification);

        return convertToDto(savedNotification);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getMyNotifications(
            String email,
            Pageable pageable) {

        User user = getUserByEmail(email);

        return notificationRepository
                .findByRecipientIdOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                )
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationDto> getMyUnreadNotifications(
            String email,
            Pageable pageable) {

        User user = getUserByEmail(email);

        return notificationRepository
                .findByRecipientIdAndReadFalseOrderByCreatedAtDesc(
                        user.getId(),
                        pageable
                )
                .map(this::convertToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {

        User user = getUserByEmail(email);

        return notificationRepository
                .countByRecipientIdAndReadFalse(user.getId());
    }

    @Override
    public NotificationDto markAsRead(
            Long notificationId,
            String email) {

        User user = getUserByEmail(email);

        Notification notification =
                notificationRepository
                        .findByIdAndRecipientId(
                                notificationId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                )
                        );

        if (!notification.isRead()) {

            notification.setRead(true);

            notification.setReadAt(
                    java.time.LocalDateTime.now()
            );

            notification =
                    notificationRepository.save(notification);
        }

        return convertToDto(notification);
    }

    @Override
    public int markAllAsRead(String email) {

        User user = getUserByEmail(email);

        return notificationRepository.markAllAsRead(
                user.getId()
        );
    }

    @Override
    public void deleteNotification(
            Long notificationId,
            String email) {

        User user = getUserByEmail(email);

        Notification notification =
                notificationRepository
                        .findByIdAndRecipientId(
                                notificationId,
                                user.getId()
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Notification not found"
                                )
                        );

        notificationRepository.delete(notification);
    }

    private boolean isNotificationEnabled(
            Long recipientId,
            NotificationType type) {

        UserSettings settings =
                userSettingsRepository
                        .findByUserId(recipientId)
                        .orElse(null);

        if (settings == null) {
            return true;
        }

        return switch (type) {

            case LEAVE ->
                    settings.isLeaveUpdates();

            case TASK ->
                    settings.isTaskUpdates();

            case PROJECT ->
                    settings.isProjectUpdates();

            default ->
                    true;
        };
    }

    private User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User not found with email: " + email
                        )
                );
    }

    private NotificationDto convertToDto(
            Notification notification) {

        NotificationDto dto = new NotificationDto();

        dto.setId(notification.getId());
        dto.setTitle(notification.getTitle());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setReferenceType(notification.getReferenceType());
        dto.setReferenceId(notification.getReferenceId());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setReadAt(notification.getReadAt());

        return dto;
    }
}