package com.smartqueue.analyticsservice.controller;

import com.smartqueue.analyticsservice.dto.CenterReportResponse;
import com.smartqueue.analyticsservice.dto.DailyStatsResponse;
import com.smartqueue.analyticsservice.dto.SystemSummaryResponse;
import com.smartqueue.analyticsservice.service.AnalyticsService;
import com.smartqueue.analyticsservice.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics",
        description = "Reports and statistics — Admin/Staff only")
@SecurityRequirement(name = "Bearer Auth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final ReportService reportService;
    // ── System Summary ────────────────────────────────────────
    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "System-wide summary — Admin only")
    public ResponseEntity<SystemSummaryResponse> getSummary() {
        return ResponseEntity.ok(
                analyticsService.getSystemSummary());
    }

    // ── Center Report ─────────────────────────────────────────
    @GetMapping("/centers/{centerId}/report")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @Operation(summary = "Daily report for a center")
    public ResponseEntity<CenterReportResponse> getCenterReport(
            @PathVariable UUID centerId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date) {

        LocalDate reportDate =
                date != null ? date : LocalDate.now();
        return ResponseEntity.ok(
                analyticsService.getCenterReport(
                        centerId, reportDate));
    }

    // ── Center History ────────────────────────────────────────
    @GetMapping("/centers/{centerId}/history")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @Operation(summary = "Center history — last N days")
    public ResponseEntity<List<DailyStatsResponse>>
    getCenterHistory(
            @PathVariable UUID centerId,
            @RequestParam(defaultValue = "7")  int days) {

        LocalDate to   = LocalDate.now();
        LocalDate from = to.minusDays(days);
        return ResponseEntity.ok(
                analyticsService.getCenterHistory(
                        centerId, from, to));
    }

    // ── Wait Times ────────────────────────────────────────────
    @GetMapping("/centers/{centerId}/wait-times")
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @Operation(summary = "Historical wait times for a center")
    public ResponseEntity<List<DailyStatsResponse>>
    getWaitTimes(
            @PathVariable UUID centerId,
            @RequestParam(defaultValue = "30") int days) {

        LocalDate to   = LocalDate.now();
        LocalDate from = to.minusDays(days);
        return ResponseEntity.ok(
                analyticsService.getWaitTimes(
                        centerId, from, to));
    }
}