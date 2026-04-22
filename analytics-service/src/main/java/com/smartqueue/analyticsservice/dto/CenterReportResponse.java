package com.smartqueue.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CenterReportResponse(
        @JsonProperty("centerId")               UUID centerId,
        @JsonProperty("centerName")             String centerName,
        @JsonProperty("reportDate")             LocalDate reportDate,
        @JsonProperty("totalServed")            int totalServed,
        @JsonProperty("totalCancelled")         int totalCancelled,
        @JsonProperty("totalNoShows")           int totalNoShows,
        @JsonProperty("totalAppointments")      int totalAppointments,
        @JsonProperty("avgActualWaitMinutes")   double avgActualWaitMinutes,
        @JsonProperty("avgEstimatedWaitMinutes") double avgEstimatedWaitMinutes,
        @JsonProperty("predictionAccuracy")     double predictionAccuracy,
        @JsonProperty("peakHour")              int peakHour
) {}