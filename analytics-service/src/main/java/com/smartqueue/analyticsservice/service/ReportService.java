package com.smartqueue.analyticsservice.service;

import com.smartqueue.analyticsservice.model.DailyReport;
import com.smartqueue.analyticsservice.model.WaitTimeRecord;
import com.smartqueue.analyticsservice.repository.DailyReportRepository;
import com.smartqueue.analyticsservice.repository.WaitTimeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {

    private final DailyReportRepository    reportRepository;
    private final WaitTimeRecordRepository waitTimeRepository;

    // ── Record Wait Time ──────────────────────────────────────
    @Transactional
    public void recordWaitTime(UUID centerId,
                               String centerName,
                               UUID queueId,
                               UUID userId,
                               int ticketNumber,
                               int actualWaitMinutes,
                               int estimatedWaitMinutes) {

        WaitTimeRecord record = WaitTimeRecord.builder()
                .centerId(centerId)
                .centerName(centerName)
                .queueId(queueId)
                .userId(userId)
                .ticketNumber(ticketNumber)
                .actualWaitMinutes(actualWaitMinutes)
                .estimatedWaitMinutes(estimatedWaitMinutes)
                .recordDate(LocalDate.now())
                .build();

        waitTimeRepository.save(record);
        updateDailyReport(centerId, centerName);
        log.info("Wait time recorded for center: {}",
                centerName);
    }

    // ── Update Daily Report ───────────────────────────────────
    @Transactional
    public void updateDailyReport(UUID centerId,
                                  String centerName) {
        LocalDate today = LocalDate.now();

        DailyReport report = reportRepository
                .findByCenterIdAndReportDate(centerId, today)
                .orElse(DailyReport.builder()
                        .centerId(centerId)
                        .centerName(centerName)
                        .reportDate(today)
                        .build());

        // احسب المتوسطات من الـ records
        Double avgActual = waitTimeRepository
                .avgActualWaitByCenter(centerId, today);
        Double avgEstimated = waitTimeRepository
                .avgEstimatedWaitByCenter(centerId, today);

        long totalRecords = waitTimeRepository
                .findByCenterIdAndRecordDate(centerId, today)
                .size();

        double accuracy = 0;
        if (avgActual != null && avgEstimated != null
                && avgActual > 0) {
            double diff = Math.abs(avgActual - avgEstimated);
            accuracy = Math.max(0,
                    100 - (diff / avgActual * 100));
        }

        report.setTotalServed((int) totalRecords);
        report.setAvgActualWaitMinutes(
                avgActual != null ? avgActual : 0);
        report.setAvgEstimatedWaitMinutes(
                avgEstimated != null ? avgEstimated : 0);
        report.setPredictionAccuracy(accuracy);

        reportRepository.save(report);
    }

    // ── Increment Cancelled ───────────────────────────────────
    @Transactional
    public void incrementCancelled(UUID centerId,
                                   String centerName) {
        LocalDate today = LocalDate.now();
        DailyReport report = reportRepository
                .findByCenterIdAndReportDate(centerId, today)
                .orElse(DailyReport.builder()
                        .centerId(centerId)
                        .centerName(centerName)
                        .reportDate(today)
                        .build());

        report.setTotalCancelled(
                report.getTotalCancelled() + 1);
        reportRepository.save(report);
    }

    // ── Increment Appointments ────────────────────────────────
    @Transactional
    public void incrementAppointments(UUID centerId,
                                      String centerName) {
        LocalDate today = LocalDate.now();
        DailyReport report = reportRepository
                .findByCenterIdAndReportDate(centerId, today)
                .orElse(DailyReport.builder()
                        .centerId(centerId)
                        .centerName(centerName)
                        .reportDate(today)
                        .build());

        report.setTotalAppointments(
                report.getTotalAppointments() + 1);
        reportRepository.save(report);
    }
}