package com.example.emps.repository;

import com.example.emps.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Page<Notification> findByRecipientIdOrderByCreatedAtDesc(
            Long recipientId,
            Pageable pageable
    );

    Page<Notification> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(
            Long recipientId,
            Pageable pageable
    );

    long countByRecipientIdAndReadFalse(Long recipientId);

    Optional<Notification> findByIdAndRecipientId(
            Long notificationId,
            Long recipientId
    );

    @Modifying
    @Query("""
            UPDATE Notification n
            SET n.read = true,
                n.readAt = CURRENT_TIMESTAMP
            WHERE n.recipient.id = :recipientId
              AND n.read = false
            """)
    int markAllAsRead(@Param("recipientId") Long recipientId);
}