package com.smartqueue.api_gateway.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Component
@Order(-2)
@Slf4j
public class GlobalExceptionHandler implements ErrorWebExceptionHandler {

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        HttpStatus status;
        String message;
        String path = exchange.getRequest().getURI().getPath();

        // ── تحديد نوع الـ Exception ──────────────────────────
        if (ex instanceof NotFoundException) {
            status  = HttpStatus.NOT_FOUND;
            message = "Service not found — it may be down or not registered";
            log.warn("Service not found for path: {}", path);

        } else if (ex instanceof ResponseStatusException rse) {
            status  = HttpStatus.valueOf(rse.getStatusCode().value());
            message = rse.getReason() != null ? rse.getReason() : ex.getMessage();
            log.warn("Response status exception: {} for path: {}", status, path);

        } else if (ex instanceof java.net.ConnectException) {
            status  = HttpStatus.SERVICE_UNAVAILABLE;
            message = "Cannot connect to the target service";
            log.error("Connection refused for path: {}", path);

        } else if (ex instanceof java.util.concurrent.TimeoutException) {
            status  = HttpStatus.GATEWAY_TIMEOUT;
            message = "Request timed out — service is taking too long";
            log.error("Timeout for path: {}", path);

        } else if (ex instanceof io.jsonwebtoken.ExpiredJwtException) {
            status  = HttpStatus.UNAUTHORIZED;
            message = "Token has expired — please login again";
            log.warn("Expired JWT for path: {}", path);

        } else if (ex instanceof io.jsonwebtoken.MalformedJwtException) {
            status  = HttpStatus.UNAUTHORIZED;
            message = "Invalid token format";
            log.warn("Malformed JWT for path: {}", path);

        } else if (ex instanceof io.jsonwebtoken.security.SignatureException) {
            status  = HttpStatus.UNAUTHORIZED;
            message = "Token signature is invalid";
            log.warn("Invalid JWT signature for path: {}", path);

        } else if (ex instanceof org.springframework.security.access.AccessDeniedException) {
            status  = HttpStatus.FORBIDDEN;
            message = "You don't have permission to access this resource";
            log.warn("Access denied for path: {}", path);

        } else {
            status  = HttpStatus.INTERNAL_SERVER_ERROR;
            message = "An unexpected error occurred";
            log.error("Unexpected error for path: {}, error: {}", path, ex.getMessage(), ex);
        }

        return writeResponse(exchange, status, message, path);
    }

    private Mono<Void> writeResponse(
            ServerWebExchange exchange,
            HttpStatus status,
            String message,
            String path) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .timestamp(LocalDateTime.now())
                .build();

        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        try {
            byte[] bytes = objectMapper.writeValueAsBytes(errorResponse);
            DataBuffer buffer = exchange.getResponse()
                    .bufferFactory()
                    .wrap(bytes);
            return exchange.getResponse().writeWith(Mono.just(buffer));

        } catch (JsonProcessingException e) {
            log.error("Error serializing error response", e);
            return exchange.getResponse().setComplete();
        }
    }
}