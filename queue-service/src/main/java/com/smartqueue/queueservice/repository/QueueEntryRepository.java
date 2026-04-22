package com.smartqueue.queueservice.repository;

import com.smartqueue.queueservice.model.QueueEntry;
import com.smartqueue.queueservice.enums.EntryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueueEntryRepository extends JpaRepository<QueueEntry, UUID> {

    Optional<QueueEntry> findByQueueIdAndUserId(UUID queueId, UUID userId);

    Optional<QueueEntry> findByQueueIdAndTicketNumber(
            UUID queueId, int ticketNumber);

    @Query("SELECT COUNT(e) FROM QueueEntry e " +
            "WHERE e.queue.id = :queueId " +
            "AND e.status = 'WAITING' " +
            "AND e.ticketNumber < :ticketNumber")
    int countWaitingAhead(UUID queueId, int ticketNumber);

    @Query("SELECT COUNT(e) FROM QueueEntry e " +
            "WHERE e.queue.id = :queueId " +
            "AND e.status = 'WAITING'")
    int countWaiting(UUID queueId);

    List<QueueEntry> findByQueueIdAndStatusOrderByTicketNumber(
            UUID queueId, EntryStatus status);

    List<QueueEntry> findByQueueIdOrderByTicketNumber(UUID queueId);
}