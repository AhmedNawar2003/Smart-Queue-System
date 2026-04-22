package com.smartqueue.appointmentservice.repository;

import com.smartqueue.appointmentservice.enums.AppointmentStatus;
import com.smartqueue.appointmentservice.model.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository
        extends JpaRepository<Appointment, UUID> {

    Page<Appointment> findByUserId(UUID userId, Pageable pageable);

    List<Appointment> findByCenterIdAndScheduledAtBetween(
            UUID centerId,
            LocalDateTime from,
            LocalDateTime to);

    List<Appointment> findByCenterIdAndStatus(
            UUID centerId,
            AppointmentStatus status);

    boolean existsByUserIdAndCenterIdAndStatus(
            UUID userId, UUID centerId,
            AppointmentStatus status);
}