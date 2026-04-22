package com.smartqueue.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record DailyStatsResponse(
        @JsonProperty("date")                  LocalDate date,
        @JsonProperty("totalServed")           int totalServed,
        @JsonProperty("totalCancelled")        int totalCancelled,
        @JsonProperty("avgWaitMinutes")        double avgWaitMinutes,
        @JsonProperty("predictionAccuracy")    double predictionAccuracy
) {}