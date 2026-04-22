package com.smartqueue.queueservice.service;

import com.smartqueue.queueservice.dto.*;
import com.smartqueue.queueservice.event.QueueEventProducer;
import com.smartqueue.queueservice.exception.ResourceNotFoundException;
import com.smartqueue.queueservice.model.Queue;
import com.smartqueue.queueservice.enums.QueueStatus;
import com.smartqueue.queueservice.model.QueueEntry;
import com.smartqueue.queueservice.enums.EntryStatus;
import com.smartqueue.queueservice.repository.QueueEntryRepository;
import com.smartqueue.queueservice.repository.QueueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class QueueService {

    private final QueueRepository queueRepository;
    private final QueueEntryRepository entryRepository;
    private final WaitTimeEstimator estimator;
    private final QueueEventProducer eventProducer;
    private final SimpMessagingTemplate messagingTemplate;

    // ── Create Queue ─────────────────────────────────────────
    @Transactional
    public QueueResponse createQueue(CreateQueueRequest request) {

        // تأكد مش موجود queue لنفس الـ center النهارده
        queueRepository.findByCenterIdAndDate(
                        request.getCenterId(), LocalDate.now())
                .ifPresent(q -> {
                    throw new IllegalStateException(
                            "Queue already exists for this center today");
                });

        Queue queue = Queue.builder()
                .centerId(request.getCenterId())
                .centerName(request.getCenterName())
                .date(LocalDate.now())
                .status(QueueStatus.OPEN)
                .currentPosition(0)
                .totalIssued(0)
                .avgServiceMinutes(request.getAvgServiceMinutes())
                .build();

        queueRepository.save(queue);
        log.info("Queue created for center: {}", request.getCenterName());
        return toQueueResponse(queue, 0);
    }

    // ── Get Queue ─────────────────────────────────────────────
    public QueueResponse getQueue(UUID queueId) {
        Queue queue = findQueue(queueId);
        int waiting = entryRepository.countWaiting(queueId);
        return toQueueResponse(queue, waiting);
    }

    // ── Get Today's Queue by Center ───────────────────────────
    public QueueResponse getTodayQueue(UUID centerId) {
        Queue queue = queueRepository
                .findByCenterIdAndDate(centerId, LocalDate.now())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No queue found for this center today"));
        int waiting = entryRepository.countWaiting(queue.getId());
        return toQueueResponse(queue, waiting);
    }

    // ── Join Queue ────────────────────────────────────────────
    @Transactional
    @CacheEvict(value = "queuePosition", key = "#queueId")
    public JoinQueueResponse joinQueue(UUID queueId,
                                       UUID userId,
                                       String userEmail,
                                       String userFullName) {
        Queue queue = findQueue(queueId);

        if (queue.getStatus() != QueueStatus.OPEN) {
            throw new IllegalStateException("Queue is not open");
        }

        // منع التكرار
        entryRepository.findByQueueIdAndUserId(queueId, userId)
                .ifPresent(e -> {
                    throw new IllegalStateException(
                            "You are already in this queue — ticket #"
                                    + e.getTicketNumber());
                });

        int ticketNo = queue.getTotalIssued() + 1;
        queue.setTotalIssued(ticketNo);
        queueRepository.save(queue);

        int waitingAhead = ticketNo - queue.getCurrentPosition() - 1;
        int eta = estimator.estimate(
                waitingAhead, queue.getAvgServiceMinutes());

        QueueEntry entry = QueueEntry.builder()
                .queue(queue)
                .userId(userId)
                .userEmail(userEmail)
                .userFullName(userFullName)
                .ticketNumber(ticketNo)
                .status(EntryStatus.WAITING)
                .build();

        entryRepository.save(entry);

        // أرسل Kafka event
        eventProducer.publishTicketIssued(
                userId, userEmail, queueId, ticketNo, eta);

        log.info("User {} joined queue {} — ticket #{}",
                userEmail, queueId, ticketNo);

        return JoinQueueResponse.builder()
                .entryId(entry.getId())
                .queueId(queueId)
                .ticketNumber(ticketNo)
                .waitingAhead(waitingAhead)
                .estimatedWaitMinutes(eta)
                .qrCodeData("QUEUE:%s:TICKET:%d"
                        .formatted(queueId, ticketNo))
                .build();
    }

    // ── Get Position ──────────────────────────────────────────
    @Cacheable(value = "queuePosition",
            key = "#queueId + ':' + #ticketNumber")
    public QueuePositionResponse getPosition(UUID queueId,
                                             int ticketNumber) {
        Queue queue = findQueue(queueId);

        QueueEntry entry = entryRepository
                .findByQueueIdAndTicketNumber(queueId, ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket #" + ticketNumber + " not found"));

        int waitingAhead = entryRepository
                .countWaitingAhead(queueId, ticketNumber);
        int eta = estimator.estimate(
                waitingAhead, queue.getAvgServiceMinutes());

        return QueuePositionResponse.builder()
                .queueId(queueId)
                .ticketNumber(ticketNumber)
                .currentPosition(queue.getCurrentPosition())
                .waitingAhead(waitingAhead)
                .estimatedWaitMinutes(eta)
                .status(entry.getStatus())
                .build();
    }

    // ── Call Next ─────────────────────────────────────────────
    @Transactional
    @CacheEvict(value = "queuePosition", allEntries = true)
    public void callNext(UUID queueId) {
        Queue queue = findQueue(queueId);

        if (queue.getStatus() != QueueStatus.OPEN) {
            throw new IllegalStateException("Queue is not open");
        }

        // Mark current as SERVED
        entryRepository.findByQueueIdAndTicketNumber(
                        queueId, queue.getCurrentPosition())
                .ifPresent(e -> {
                    e.setStatus(EntryStatus.SERVED);
                    e.setServedAt(LocalDateTime.now());
                    entryRepository.save(e);
                });

        int nextTicket = queue.getCurrentPosition() + 1;
        queue.setCurrentPosition(nextTicket);
        queueRepository.save(queue);

        // Mark next as CALLED
        entryRepository.findByQueueIdAndTicketNumber(
                        queueId, nextTicket)
                .ifPresent(e -> {
                    e.setStatus(EntryStatus.CALLED);
                    e.setCalledAt(LocalDateTime.now());
                    entryRepository.save(e);
                });

        // Kafka event
        eventProducer.publishQueueUpdated(queueId, nextTicket,  queue.getCenterId(),
                queue.getCenterName()  );
        // Notify user 2 tickets ahead
        entryRepository
                .findByQueueIdAndStatusOrderByTicketNumber(
                        queueId, EntryStatus.WAITING)
                .stream()
                .filter(e -> e.getTicketNumber() == nextTicket + 2)
                .findFirst()
                .ifPresent(e -> {
                    int eta = estimator.estimate(
                            2, queue.getAvgServiceMinutes());
                    eventProducer.publishTurnSoon(
                            e.getUserId(), e.getUserEmail(),
                            queueId, e.getTicketNumber(), eta);
                });

        // WebSocket broadcast
        messagingTemplate.convertAndSend(
                "/topic/queue/" + queueId,
                new QueueUpdateMessage(queueId, nextTicket,
                        entryRepository.countWaiting(queueId)));

        log.info("Called ticket #{} in queue {}", nextTicket, queueId);
    }

    // ── Skip Ticket ───────────────────────────────────────────
    @Transactional
    @CacheEvict(value = "queuePosition", allEntries = true)
    public void skipTicket(UUID queueId, int ticketNumber) {
        QueueEntry entry = entryRepository
                .findByQueueIdAndTicketNumber(queueId, ticketNumber)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ticket #" + ticketNumber + " not found"));

        entry.setStatus(EntryStatus.SKIPPED);
        entryRepository.save(entry);
        log.info("Ticket #{} skipped in queue {}", ticketNumber, queueId);
        callNext(queueId);
    }

    // ── Cancel Ticket ─────────────────────────────────────────
    @Transactional
    @CacheEvict(value = "queuePosition", allEntries = true)
    public void cancelTicket(UUID queueId, UUID userId) {
        QueueEntry entry = entryRepository
                .findByQueueIdAndUserId(queueId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You are not in this queue"));

        if (entry.getStatus() != EntryStatus.WAITING) {
            throw new IllegalStateException(
                    "Cannot cancel — status is " + entry.getStatus());
        }

        entry.setStatus(EntryStatus.CANCELLED);
        entryRepository.save(entry);
        log.info("User {} cancelled ticket #{}", userId, entry.getTicketNumber());
    }

    // ── Close Queue ───────────────────────────────────────────
    @Transactional
    public QueueResponse closeQueue(UUID queueId) {
        Queue queue = findQueue(queueId);
        queue.setStatus(QueueStatus.CLOSED);
        queue.setClosedAt(LocalDateTime.now());
        queueRepository.save(queue);
        log.info("Queue {} closed", queueId);
        return toQueueResponse(queue, 0);
    }

    // ── Get All Entries ───────────────────────────────────────
    public List<QueueEntry> getQueueEntries(UUID queueId) {
        findQueue(queueId);
        return entryRepository
                .findByQueueIdOrderByTicketNumber(queueId);
    }

    // ── Helpers ───────────────────────────────────────────────
    private Queue findQueue(UUID queueId) {
        return queueRepository.findById(queueId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Queue not found: " + queueId));
    }

    private QueueResponse toQueueResponse(Queue q, int waitingCount) {
        return QueueResponse.builder()
                .id(q.getId())
                .centerId(q.getCenterId())
                .centerName(q.getCenterName())
                .date(q.getDate())
                .status(q.getStatus())
                .currentPosition(q.getCurrentPosition())
                .totalIssued(q.getTotalIssued())
                .avgServiceMinutes(q.getAvgServiceMinutes())
                .waitingCount(waitingCount)
                .openedAt(q.getOpenedAt())
                .build();
    }

    public record QueueUpdateMessage(
            UUID queueId,
            int currentPosition,
            int waitingCount) {}
}