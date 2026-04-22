package com.smartqueue.notificationservice.service;

import com.smartqueue.notificationservice.dto.NotificationResponse;
import com.smartqueue.notificationservice.enums.NotificationStatus;
import com.smartqueue.notificationservice.enums.NotificationType;
import com.smartqueue.notificationservice.exception.ResourceNotFoundException;
import com.smartqueue.notificationservice.model.Notification;
import com.smartqueue.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository repository;

    // ── Save & Send ───────────────────────────────────────────
    @Transactional
    public Notification createAndSend(
            UUID userId,
            String userEmail,
            String title,
            String message,
            NotificationType type,
            String referenceId) {

        Notification notification = Notification.builder()
                .userId(userId)
                .userEmail(userEmail)
                .title(title)
                .message(message)
                .type(type)
                .status(NotificationStatus.SENT)
                .referenceId(referenceId)
                .sentAt(LocalDateTime.now())
                .build();

        repository.save(notification);
        log.info("Notification saved: {} → {}",
                type, userEmail);
        return notification;
    }

    // ── Get My Notifications ──────────────────────────────────
    public Page<NotificationResponse> getMyNotifications(
            UUID userId, Pageable pageable) {
        return repository
                .findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(this::toResponse);
    }

    // ── Unread Count ──────────────────────────────────────────
    public long getUnreadCount(UUID userId) {
        return repository.countByUserIdAndStatus(
                userId, NotificationStatus.SENT);
    }

    // ── Mark as Read ──────────────────────────────────────────
    @Transactional
    public void markAsRead(UUID id, UUID userId) {
        Notification notification = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Notification not found: " + id));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalStateException(
                    "You can only mark your own notifications");
        }

        notification.setStatus(NotificationStatus.READ);
        notification.setReadAt(LocalDateTime.now());
        repository.save(notification);
    }

    // ── Mark All as Read ──────────────────────────────────────
    @Transactional
    public void markAllAsRead(UUID userId) {
        repository.markAllAsRead(userId);
        log.info("All notifications marked as read for: {}",
                userId);
    }

    // ── Mapper ────────────────────────────────────────────────
    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .status(n.getStatus())
                .referenceId(n.getReferenceId())
                .sentAt(n.getSentAt())
                .readAt(n.getReadAt())
                .createdAt(n.getCreatedAt())
                .build();
    }
}