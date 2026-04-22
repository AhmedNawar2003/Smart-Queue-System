package com.smartqueue.appointmentservice.repository;

import com.smartqueue.appointmentservice.model.AppointmentLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentLogRepository
        extends JpaRepository<AppointmentLog, UUID> {

    List<AppointmentLog> findByAppointmentIdOrderByTimestampDesc(
            UUID appointmentId);
}