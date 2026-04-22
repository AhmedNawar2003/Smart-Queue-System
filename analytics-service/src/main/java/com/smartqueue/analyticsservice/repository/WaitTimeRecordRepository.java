package com.smartqueue.analyticsservice.repository;

import com.smartqueue.analyticsservice.model.WaitTimeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface WaitTimeRecordRepository
        extends JpaRepository<WaitTimeRecord, UUID> {

    List<WaitTimeRecord> findByCenterIdAndRecordDate(
            UUID centerId, LocalDate date);

    List<WaitTimeRecord> findByCenterIdAndRecordDateBetween(
            UUID centerId, LocalDate from, LocalDate to);

    @Query("SELECT AVG(w.actualWaitMinutes) " +
            "FROM WaitTimeRecord w " +
            "WHERE w.centerId = :centerId " +
            "AND w.recordDate = :date")
    Double avgActualWaitByCenter(UUID centerId, LocalDate date);

    @Query("SELECT AVG(w.estimatedWaitMinutes) " +
            "FROM WaitTimeRecord w " +
            "WHERE w.centerId = :centerId " +
            "AND w.recordDate = :date")
    Double avgEstimatedWaitByCenter(
            UUID centerId, LocalDate date);

    @Query("SELECT COUNT(w) FROM WaitTimeRecord w " +
            "WHERE w.recordDate = :date")
    long countByDate(LocalDate date);

    @Query("SELECT AVG(w.actualWaitMinutes) " +
            "FROM WaitTimeRecord w " +
            "WHERE w.recordDate = :date")
    Double avgWaitByDate(LocalDate date);
}