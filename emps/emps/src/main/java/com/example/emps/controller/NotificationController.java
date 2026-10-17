package com.example.emps.controller;

import com.example.emps.dto.NotificationDto;
import com.example.emps.service.NotificationService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    @GetMapping("/me")
    public ResponseEntity<Page<NotificationDto>> getMyNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable =
                createPageable(page, size);

        return ResponseEntity.ok(
                notificationService.getMyNotifications(
                        authentication.getName(),
                        pageable
                )
        );
    }

    @GetMapping("/me/unread")
    public ResponseEntity<Page<NotificationDto>> getMyUnreadNotifications(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable =
                createPageable(page, size);

        return ResponseEntity.ok(
                notificationService.getMyUnreadNotifications(
                        authentication.getName(),
                        pageable
                )
        );
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.getUnreadCount(
                        authentication.getName()
                )
        );
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<NotificationDto> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        return ResponseEntity.ok(
                notificationService.markAsRead(
                        id,
                        authentication.getName()
                )
        );
    }

    @PutMapping("/read-all")
    public ResponseEntity<String> markAllAsRead(
            Authentication authentication) {

        int updatedCount =
                notificationService.markAllAsRead(
                        authentication.getName()
                );

        return ResponseEntity.ok(
                updatedCount
                        + " notification(s) marked as read"
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteNotification(
            @PathVariable Long id,
            Authentication authentication) {

        notificationService.deleteNotification(
                id,
                authentication.getName()
        );

        return ResponseEntity.ok(
                "Notification deleted successfully"
        );
    }

    private Pageable createPageable(
            int page,
            int size) {

        if (page < 0) {
            page = 0;
        }

        if (size < 1) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        return PageRequest.of(
                page,
                size,
                Sort.by(
                        Sort.Direction.DESC,
                        "createdAt"
                )
        );
    }
}