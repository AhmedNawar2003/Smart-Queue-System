package com.smartqueue.appointmentservice.service;

import com.smartqueue.appointmentservice.client.QueueServiceClient;
import com.smartqueue.appointmentservice.dto.AppointmentResponse;
import com.smartqueue.appointmentservice.dto.CreateAppointmentRequest;
import com.smartqueue.appointmentservice.dto.RescheduleRequest;
import com.smartqueue.appointmentservice.enums.AppointmentStatus;
import com.smartqueue.appointmentservice.event.AppointmentEventProducer;
import com.smartqueue.appointmentservice.exception.ResourceNotFoundException;
import com.smartqueue.appointmentservice.model.Appointment;
import com.smartqueue.appointmentservice.model.AppointmentLog;
import com.smartqueue.appointmentservice.repository.AppointmentLogRepository;
import com.smartqueue.appointmentservice.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository    appointmentRepository;
    private final AppointmentLogRepository logRepository;
    private final AppointmentEventProducer eventProducer;
    private final QueueServiceClient       queueServiceClient;

    // ── Book Appointment ──────────────────────────────────────
    @Transactional
    public AppointmentResponse book(
            CreateAppointmentRequest request,
            UUID userId,
            String userEmail,
            String userFullName) {

        // تحقق مش عنده appointment pending لنفس الـ center
        if (appointmentRepository
                .existsByUserIdAndCenterIdAndStatus(
                        userId, request.getCenterId(),
                        AppointmentStatus.PENDING)) {
            throw new IllegalStateException(
                    "You already have a pending appointment " +
                            "at this center");
        }

        Appointment appointment = Appointment.builder()
                .userId(userId)
                .userEmail(userEmail)
                .userFullName(userFullName)
                .centerId(request.getCenterId())
                .centerName(request.getCenterName())
                .serviceType(request.getServiceType())
                .scheduledAt(request.getScheduledAt())
                .notes(request.getNotes())
                .queueId(request.getQueueId())
                .ticketNumber(request.getTicketNumber())
                .status(AppointmentStatus.PENDING)
                .build();

        appointmentRepository.save(appointment);

        // سجّل في الـ log
        saveLog(appointment, "CREATED", userEmail,
                "Appointment booked for " + request.getCenterName());

        // Kafka event
        eventProducer.publishCreated(
                appointment.getId(), userId, userEmail,
                request.getCenterName(), request.getServiceType(),
                request.getScheduledAt());

        log.info("Appointment booked: {} for {}",
                appointment.getId(), userEmail);

        return toResponse(appointment);
    }

    // ── Get My Appointments ───────────────────────────────────
    public Page<AppointmentResponse> getMyAppointments(
            UUID userId, Pageable pageable) {
        return appointmentRepository
                .findByUserId(userId, pageable)
                .map(this::toResponse);
    }

    // ── Get by ID ─────────────────────────────────────────────
    public AppointmentResponse getById(UUID id, UUID userId,
                                       String role) {
        Appointment appointment = findAppointment(id);

        // User بيشوف بتاعه بس — Admin/Staff بيشوفوا أي حاجة
        if (!role.contains("ADMIN") &&
                !role.contains("STAFF") &&
                !appointment.getUserId().equals(userId)) {
            throw new IllegalStateException(
                    "You can only view your own appointments");
        }

        return toResponse(appointment);
    }

    // ── Get Center Appointments ───────────────────────────────
    public List<AppointmentResponse> getCenterAppointments(
            UUID centerId,
            LocalDateTime from,
            LocalDateTime to) {
        return appointmentRepository
                .findByCenterIdAndScheduledAtBetween(
                        centerId, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── Reschedule ────────────────────────────────────────────
    @Transactional
    public AppointmentResponse reschedule(
            UUID id, UUID userId,
            RescheduleRequest request) {

        Appointment appointment = findAppointment(id);

        // تأكد إنه صاحب الـ appointment
        if (!appointment.getUserId().equals(userId)) {
            throw new IllegalStateException(
                    "You can only reschedule your own appointments");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELLED ||
                appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Cannot reschedule a " +
                            appointment.getStatus() + " appointment");
        }

        LocalDateTime oldTime = appointment.getScheduledAt();
        appointment.setScheduledAt(request.getNewScheduledAt());
        appointment.setStatus(AppointmentStatus.RESCHEDULED);
        appointmentRepository.save(appointment);

        saveLog(appointment, "RESCHEDULED",
                appointment.getUserEmail(),
                "From " + oldTime + " to " +
                        request.getNewScheduledAt() +
                        (request.getReason() != null ?
                                " — " + request.getReason() : ""));

        eventProducer.publishRescheduled(
                id, userId, appointment.getUserEmail(),
                request.getNewScheduledAt());

        log.info("Appointment {} rescheduled", id);
        return toResponse(appointment);
    }

    // ── Cancel ────────────────────────────────────────────────
    @Transactional
    public void cancel(UUID id, UUID userId, String role) {
        Appointment appointment = findAppointment(id);

        // User بيكنسل بتاعه بس — Admin يكنسل أي حاجة
        if (!role.contains("ADMIN") &&
                !appointment.getUserId().equals(userId)) {
            throw new IllegalStateException(
                    "You can only cancel your own appointments");
        }

        if (appointment.getStatus() ==
                AppointmentStatus.COMPLETED) {
            throw new IllegalStateException(
                    "Cannot cancel a completed appointment");
        }

        if (appointment.getStatus() ==
                AppointmentStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Appointment is already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(appointment);

        saveLog(appointment, "CANCELLED",
                appointment.getUserEmail(),
                "Appointment cancelled");

        eventProducer.publishCancelled(
                id, userId, appointment.getUserEmail(),
                "Cancelled by user");

        log.info("Appointment {} cancelled", id);
    }

    // ── Get Logs ──────────────────────────────────────────────
    public List<AppointmentLog> getLogs(UUID id) {
        findAppointment(id);
        return logRepository
                .findByAppointmentIdOrderByTimestampDesc(id);
    }

    // ── Helpers ───────────────────────────────────────────────
    private Appointment findAppointment(UUID id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Appointment not found: " + id));
    }

    private void saveLog(Appointment appointment,
                         String action,
                         String actor,
                         String details) {
        logRepository.save(AppointmentLog.builder()
                .appointment(appointment)
                .action(action)
                .actor(actor)
                .details(details)
                .build());
    }

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .userId(a.getUserId())
                .userEmail(a.getUserEmail())
                .userFullName(a.getUserFullName())
                .centerId(a.getCenterId())
                .centerName(a.getCenterName())
                .serviceType(a.getServiceType())
                .status(a.getStatus())
                .scheduledAt(a.getScheduledAt())
                .completedAt(a.getCompletedAt())
                .queueId(a.getQueueId())
                .ticketNumber(a.getTicketNumber())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}