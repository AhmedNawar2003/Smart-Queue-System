package com.smartqueue.analyticsservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record SystemSummaryResponse(
        @JsonProperty("totalCenters")          int totalCenters,
        @JsonProperty("totalServedToday")      int totalServedToday,
        @JsonProperty("totalAppointmentsToday") int totalAppointmentsToday,
        @JsonProperty("avgWaitMinutesToday")   double avgWaitMinutesToday,
        @JsonProperty("totalServedAllTime")    long totalServedAllTime,
        @JsonProperty("systemAccuracy")        double systemAccuracy
) {}