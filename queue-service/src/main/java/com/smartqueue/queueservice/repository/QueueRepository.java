package com.smartqueue.queueservice.repository;

import com.smartqueue.queueservice.enums.QueueStatus;
import com.smartqueue.queueservice.model.Queue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QueueRepository extends JpaRepository<Queue, UUID> {

    Optional<Queue> findByCenterIdAndDate(UUID centerId, LocalDate date);

    Optional<Queue> findByCenterIdAndStatus(
            UUID centerId, QueueStatus status);

    List<Queue> findByDate(LocalDate date);
}