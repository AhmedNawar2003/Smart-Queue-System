package com.smartqueue.analyticsservice.service;

import com.smartqueue.analyticsservice.dto.CenterReportResponse;
import com.smartqueue.analyticsservice.dto.DailyStatsResponse;
import com.smartqueue.analyticsservice.dto.SystemSummaryResponse;
import com.smartqueue.analyticsservice.exception.ResourceNotFoundException;
import com.smartqueue.analyticsservice.model.DailyReport;
import com.smartqueue.analyticsservice.repository.DailyReportRepository;
import com.smartqueue.analyticsservice.repository.WaitTimeRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final DailyReportRepository    reportRepository;
    private final WaitTimeRecordRepository waitTimeRepository;

    // ── Center Report ─────────────────────────────────────────
    public CenterReportResponse getCenterReport(
            UUID centerId, LocalDate date) {

        DailyReport report = reportRepository
                .findByCenterIdAndReportDate(centerId, date)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No report found for center: "
                                        + centerId + " on " + date));

        return toReportResponse(report);
    }

    // ── Center History ────────────────────────────────────────
    public List<DailyStatsResponse> getCenterHistory(
            UUID centerId,
            LocalDate from,
            LocalDate to) {

        return reportRepository
                .findByCenterIdAndReportDateBetween(
                        centerId, from, to)
                .stream()
                .map(this::toDailyStats)
                .toList();
    }

    // ── Wait Times ────────────────────────────────────────────
    public List<DailyStatsResponse> getWaitTimes(
            UUID centerId,
            LocalDate from,
            LocalDate to) {

        return waitTimeRepository
                .findByCenterIdAndRecordDateBetween(
                        centerId, from, to)
                .stream()
                .map(w -> DailyStatsResponse.builder()
                        .date(w.getRecordDate())
                        .avgWaitMinutes(w.getActualWaitMinutes())
                        .build())
                .toList();
    }

    // ── System Summary ────────────────────────────────────────
    public SystemSummaryResponse getSystemSummary() {
        LocalDate today = LocalDate.now();

        Long totalServedToday =
                reportRepository.sumTotalServedByDate(today);
        Double avgWaitToday =
                reportRepository.avgWaitByDate(today);
        Long totalServedAllTime =
                reportRepository.sumTotalServedAllTime();
        Double systemAccuracy =
                reportRepository.avgSystemAccuracy();
        int totalCenters =
                reportRepository.countDistinctCenters();

        List<DailyReport> todayReports =
                reportRepository.findByReportDate(today);
        int totalAppointmentsToday = todayReports.stream()
                .mapToInt(DailyReport::getTotalAppointments)
                .sum();

        return SystemSummaryResponse.builder()
                .totalCenters(totalCenters)
                .totalServedToday(
                        totalServedToday != null ?
                                totalServedToday.intValue() : 0)
                .totalAppointmentsToday(totalAppointmentsToday)
                .avgWaitMinutesToday(
                        avgWaitToday != null ? avgWaitToday : 0)
                .totalServedAllTime(
                        totalServedAllTime != null ?
                                totalServedAllTime : 0)
                .systemAccuracy(
                        systemAccuracy != null ? systemAccuracy : 0)
                .build();
    }

    // ── Mappers ───────────────────────────────────────────────
    private CenterReportResponse toReportResponse(
            DailyReport r) {
        return CenterReportResponse.builder()
                .centerId(r.getCenterId())
                .centerName(r.getCenterName())
                .reportDate(r.getReportDate())
                .totalServed(r.getTotalServed())
                .totalCancelled(r.getTotalCancelled())
                .totalNoShows(r.getTotalNoShows())
                .totalAppointments(r.getTotalAppointments())
                .avgActualWaitMinutes(r.getAvgActualWaitMinutes())
                .avgEstimatedWaitMinutes(
                        r.getAvgEstimatedWaitMinutes())
                .predictionAccuracy(r.getPredictionAccuracy())
                .peakHour(r.getPeakHour())
                .build();
    }

    private DailyStatsResponse toDailyStats(DailyReport r) {
        return DailyStatsResponse.builder()
                .date(r.getReportDate())
                .totalServed(r.getTotalServed())
                .totalCancelled(r.getTotalCancelled())
                .avgWaitMinutes(r.getAvgActualWaitMinutes())
                .predictionAccuracy(r.getPredictionAccuracy())
                .build();
    }
}