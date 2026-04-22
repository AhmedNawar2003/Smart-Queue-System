package com.smartqueue.notificationservice.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender  mailSender;
    private final TemplateEngine  templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Async
    public void sendEmail(String to,
                          String subject,
                          String templateName,
                          Map<String, Object> variables) {
        try {
            Context context = new Context();
            context.setVariables(variables);
            String htmlContent = templateEngine
                    .process(templateName, context);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("Email sent to: {} — subject: {}",
                    to, subject);

        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}",
                    to, e.getMessage());
        }
    }

    // ── Shortcut methods ─────────────────────────────────────

    @Async
    public void sendAppointmentConfirmation(
            String to,
            String userName,
            String centerName,
            String serviceType,
            String scheduledAt) {

        sendEmail(to,
                "✅ تأكيد الحجز — " + centerName,
                "appointment-confirmation",
                Map.of(
                        "userName",    userName,
                        "centerName",  centerName,
                        "serviceType", serviceType,
                        "scheduledAt", scheduledAt
                ));
    }

    @Async
    public void sendTurnSoonNotification(
            String to,
            String userName,
            int ticketNumber,
            int etaMinutes) {

        sendEmail(to,
                "🔔 دورك قريب — رقم " + ticketNumber,
                "turn-soon",
                Map.of(
                        "userName",     userName,
                        "ticketNumber", ticketNumber,
                        "etaMinutes",   etaMinutes
                ));
    }

    @Async
    public void sendTicketIssuedNotification(
            String to,
            String userName,
            int ticketNumber,
            int etaMinutes) {

        sendEmail(to,
                "🎫 تذكرتك رقم " + ticketNumber,
                "ticket-issued",
                Map.of(
                        "userName",     userName,
                        "ticketNumber", ticketNumber,
                        "etaMinutes",   etaMinutes
                ));
    }
}