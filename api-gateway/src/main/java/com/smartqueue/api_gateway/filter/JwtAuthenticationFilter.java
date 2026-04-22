package com.smartqueue.api_gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@Slf4j
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    @Value("${jwt.secret}")
    private String secretKey;

    private static final List<String> PUBLIC_PATHS = List.of(
            "/api/v1/auth/login",
            "/api/v1/auth/register",
            "/api/v1/auth/refresh",
            "/api/v1/auth/oauth2",
            "/oauth2/",
            "/login/oauth2",
            "/actuator",
            "/fallback",
            // ── Swagger ──────────────────────────────────────────
            "/swagger-ui",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars",
            // ── Per-service docs ──────────────────────────────────
            "/auth-service/v3/api-docs",
            "/user-service/v3/api-docs",
            "/queue-service/v3/api-docs",
            "/appointment-service/v3/api-docs",
            "/notification-service/v3/api-docs",
            "/analytics-service/v3/api-docs"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange,
                             GatewayFilterChain chain) {

        String path = exchange.getRequest().getURI().getPath();

        // ── Public paths — bypass ────────────────────────────
        boolean isPublic = PUBLIC_PATHS.stream()
                .anyMatch(path::startsWith);
        if (isPublic) {
            return chain.filter(exchange);
        }

        // ── Check Authorization header ───────────────────────
        String authHeader = exchange.getRequest()
                .getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return buildErrorResponse(exchange,
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);

        try {
            // ── Validate token ───────────────────────────────
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String email = claims.getSubject();
            String role  = claims.get("role", String.class);

            log.debug("Authenticated request — user: {}, role: {}, path: {}",
                    email, role, path);

            // ── Forward user info to downstream services ─────
            ServerWebExchange modifiedExchange = exchange.mutate()
                    .request(r -> r
                            .header("X-Auth-Token", token)
                            .header("X-User-Email", email)
                            .header("X-User-Role",  role)
                    )
                    .build();

            return chain.filter(modifiedExchange);

        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return buildErrorResponse(exchange,
                    HttpStatus.UNAUTHORIZED, "Token has expired");

        } catch (io.jsonwebtoken.MalformedJwtException |
                 io.jsonwebtoken.security.SignatureException e) {
            return buildErrorResponse(exchange,
                    HttpStatus.UNAUTHORIZED, "Invalid token");

        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
            return buildErrorResponse(exchange,
                    HttpStatus.UNAUTHORIZED, "Token validation failed");
        }
    }

    private Mono<Void> buildErrorResponse(ServerWebExchange exchange,
                                          HttpStatus status,
                                          String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders()
                .setContentType(MediaType.APPLICATION_JSON);

        String body = """
            {
              "status": %d,
              "error": "%s",
              "message": "%s",
              "timestamp": "%s"
            }
            """.formatted(
                status.value(),
                status.getReasonPhrase(),
                message,
                java.time.LocalDateTime.now()
        );

        var buffer = exchange.getResponse()
                .bufferFactory()
                .wrap(body.getBytes(StandardCharsets.UTF_8));

        return exchange.getResponse().writeWith(Mono.just(buffer));
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public int getOrder() {
        return -1;
    }
}