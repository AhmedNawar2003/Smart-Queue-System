package com.smartqueue.analyticsservice.repository;

import com.smartqueue.analyticsservice.model.DailyReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DailyReportRepository
        extends JpaRepository<DailyReport, UUID> {

    Optional<DailyReport> findByCenterIdAndReportDate(
            UUID centerId, LocalDate date);

    List<DailyReport> findByCenterIdOrderByReportDateDesc(
            UUID centerId);

    List<DailyReport> findByCenterIdAndReportDateBetween(
            UUID centerId, LocalDate from, LocalDate to);

    List<DailyReport> findByReportDate(LocalDate date);

    @Query("SELECT SUM(d.totalServed) " +
            "FROM DailyReport d " +
            "WHERE d.reportDate = :date")
    Long sumTotalServedByDate(LocalDate date);

    @Query("SELECT AVG(d.avgActualWaitMinutes) " +
            "FROM DailyReport d " +
            "WHERE d.reportDate = :date")
    Double avgWaitByDate(LocalDate date);

    @Query("SELECT SUM(d.totalServed) FROM DailyReport d")
    Long sumTotalServedAllTime();

    @Query("SELECT AVG(d.predictionAccuracy) FROM DailyReport d")
    Double avgSystemAccuracy();

    @Query("SELECT COUNT(DISTINCT d.centerId) FROM DailyReport d")
    int countDistinctCenters();
}