package com.smartqueue.notificationservice.repository;

import com.smartqueue.notificationservice.enums.NotificationStatus;
import com.smartqueue.notificationservice.model.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationRepository
        extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(
            UUID userId, Pageable pageable);

    long countByUserIdAndStatus(
            UUID userId, NotificationStatus status);

    @Modifying
    @Query("UPDATE Notification n SET n.status = 'READ', " +
            "n.readAt = CURRENT_TIMESTAMP " +
            "WHERE n.userId = :userId " +
            "AND n.status = 'SENT'")
    void markAllAsRead(UUID userId);
}