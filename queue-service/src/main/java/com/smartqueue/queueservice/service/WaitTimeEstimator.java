package com.smartqueue.queueservice.service;

import org.springframework.stereotype.Component;

@Component
public class WaitTimeEstimator {

    // حساب وقت الانتظار المتوقع
    public int estimate(int waitingAhead, int avgServiceMinutes) {
        if (avgServiceMinutes <= 0) avgServiceMinutes = 5;
        return waitingAhead * avgServiceMinutes;
    }

    // تحديث المتوسط باستخدام Exponential Moving Average
    public int updateAverage(int currentAvg, int actualMinutes) {
        double alpha = 0.3;
        return (int) Math.round(
                alpha * actualMinutes + (1 - alpha) * currentAvg);
    }
}