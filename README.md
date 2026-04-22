<div align="center">

[![Typing SVG](https://readme-typing-svg.demolab.com?font=JetBrains+Mono&weight=700&size=22&pause=1000&color=00D4FF&center=true&vCenter=true&width=800&lines=🎫+Smart+Queue+%26+Appointment+System;Production-Grade+Microservices+%7C+Java+21;Event-Driven+%7C+Real-Time+%7C+Cloud-Native;Spring+Boot+4+%7C+Kafka+%7C+JWT+%7C+Docker)](https://git.io/typing-svg)

<br/>

# 🎫 Smart Queue & Appointment Management System

### `Java 21` · `Spring Boot 4` · `Kafka` · `JWT` · `WebSocket` · `Docker` · `Cloud-Native`

**A production-grade, event-driven microservices platform** that solves a real-world problem in Egypt — eliminating long physical queues in hospitals, government offices, and service centers.
Built with **Java 21**, **Spring Boot 4**, and modern **Cloud-Native** principles.

<br/>

![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-Cache-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-Real--Time-009688?style=for-the-badge)
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-critical?style=for-the-badge)
![Zipkin](https://img.shields.io/badge/Zipkin-Tracing-FE7139?style=for-the-badge)
![Status](https://img.shields.io/badge/Status-Production%20Ready-00C851?style=for-the-badge)

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Problem Statement](#-problem-statement)
- [System Architecture](#️-system-architecture)
- [Services Breakdown](#-services-breakdown)
- [Security Model](#-security-model)
- [Event-Driven Flow](#-event-driven-flow)
- [Real-Time Queue Tracking](#-real-time-queue-tracking)
- [Tech Stack](#️-tech-stack)
- [Design Highlights](#-design-highlights)
- [Database Design](#️-database-design)
- [API Endpoints](#-api-endpoints)
- [Observability](#-observability)
- [Getting Started](#-getting-started)
- [Future Enhancements](#-future-enhancements)

---

## 🔍 Overview

**Smart Queue** is a fully distributed, event-driven microservices platform that allows citizens to:

- 📱 **Book appointments remotely** — no need to physically go to the center
- 🎫 **Get a queue ticket instantly** — with an estimated wait time
- 📡 **Track their position in real-time** — via WebSocket live updates
- 🔔 **Receive smart notifications** — email alerts when their turn is near

| Capability | Implementation |
|---|---|
| 🔀 **API Routing & Security** | Spring Cloud Gateway + JWT Filter |
| 🔍 **Service Discovery** | Netflix Eureka |
| ⚙️ **Centralized Config** | Spring Cloud Config Server |
| 🔐 **Authentication** | JWT + OAuth2 (Google Login) |
| 📨 **Async Messaging** | Apache Kafka |
| 📡 **Real-Time Updates** | WebSocket (STOMP over SockJS) |
| 🔗 **Inter-service Calls** | OpenFeign + Load Balancer |
| 🛡️ **Fault Tolerance** | Resilience4j Circuit Breaker |
| ⚡ **Caching** | Redis + Caffeine |
| 📊 **Distributed Tracing** | Zipkin + Micrometer |
| 🗄️ **Data Isolation** | Database-per-Service (PostgreSQL) |
| 🐳 **Containerization** | Docker & Docker Compose |

---

## 🇪🇬 Problem Statement

Long physical queues are a major pain point across Egypt in:

| Sector | Examples |
|---|---|
| 🏥 **Healthcare** | Hospitals, clinics, pharmacies |
| 🏛️ **Government** | Shahr Aqari, Mograat Al Dawla, Passport offices |
| 🏢 **Service Centers** | Banks, telecom centers, utility offices |

**Smart Queue** digitizes the entire experience — citizens book online, get a ticket number, track their position from anywhere, and only go to the center when their turn is near.

---

## 🏛️ System Architecture

<p align="center">
  <img src="/Smart Queue System.png" width="100%" alt="SmartEvent System Architecture"/>
</p>

```
                    ┌─────────────────────────────┐
                    │         Client Apps          │
                    │  (Web / Mobile / Admin Panel) │
                    └──────────────┬──────────────┘
                                   │ HTTPS
                    ┌──────────────▼──────────────┐
                    │         API Gateway          │
                    │  Port: 8080                  │
                    │  JWT Validation · Routing    │
                    │  Rate Limiting · CORS        │
                    │  Circuit Breaker Fallback    │
                    └──────┬───────────────┬───────┘
                           │               │
          ┌────────────────┼───────────────┼────────────────┐
          │                │               │                │
┌─────────▼──────┐ ┌───────▼──────┐ ┌─────▼──────┐ ┌──────▼──────┐
│  Auth Service  │ │ User Service │ │   Queue    │ │ Appointment │
│  Port: 8081    │ │  Port: 8082  │ │  Service   │ │   Service   │
│                │ │              │ │ Port: 8083 │ │  Port: 8084 │
│  JWT · OAuth2  │ │  Profiles    │ │            │ │             │
│  Google Login  │ │  Roles       │ │  WebSocket │ │  Reschedule │
└────────────────┘ └──────────────┘ └─────┬──────┘ └──────┬──────┘
                                          │               │
                    ┌─────────────────────▼───────────────▼──────┐
                    │              Apache Kafka                    │
                    │         Event Bus — Async Messaging          │
                    │                                              │
                    │  queue.updated  ·  queue.ticket-issued       │
                    │  queue.turn-soon  ·  appointment.created     │
                    │  appointment.cancelled  ·  appointment.rescheduled │
                    └──────────────────┬───────────────────────────┘
                                       │
                    ┌──────────────────┼───────────────────────────┐
                    │                  │                            │
          ┌─────────▼──────┐  ┌────────▼───────┐        ┌─────────▼──────┐
          │  Notification  │  │   Analytics    │        │     Redis      │
          │    Service     │  │    Service     │        │     Cache      │
          │  Port: 8085    │  │   Port: 8086   │        │  Port: 6379    │
          │                │  │                │        │                │
          │  Email · Push  │  │  Reports ·     │        │  Queue State   │
          │  Kafka Consumer│  │  Wait Times    │        │  Sessions      │
          └────────────────┘  └────────────────┘        └────────────────┘

    ┌───────────────┐     ┌──────────────────┐     ┌────────────────┐
    │ Config Server │     │   Eureka Server  │     │    Zipkin      │
    │  Port: 8888   │     │   Port: 8761     │     │  Port: 9411    │
    └───────────────┘     └──────────────────┘     └────────────────┘
```

---

## 🧩 Services Breakdown

### 🔧 Infrastructure Services

| Service | Port | Description |
|---|---|---|
| **API Gateway** | `8080` | Single entry point — JWT validation, routing, rate limiting, circuit breaker |
| **Eureka Server** | `8761` | Dynamic service registration & discovery |
| **Config Server** | `8888` | Centralized YAML configuration for all services |
| **Zipkin** | `9411` | Distributed request tracing & latency monitoring |

---

### 💼 Business Services

| Service | Port | Responsibilities |
|---|---|---|
| **Auth Service** | `8081` | Registration, login, JWT generation, OAuth2 Google, role management |
| **User Service** | `8082` | User profiles, governorate, national ID, OpenFeign integration |
| **Queue Service** | `8083` | Create queues, issue tickets, call next, skip, real-time WebSocket |
| **Appointment Service** | `8084` | Book, reschedule, cancel appointments, audit logs |
| **Notification Service** | `8085` | Email via Gmail SMTP, Kafka consumer, Thymeleaf templates |
| **Analytics Service** | `8086` | Daily reports, wait time accuracy, prediction algorithms |

---

## 🔐 Security Model

### Authentication Strategy

- **JWT** tokens issued by Auth Service, validated at the **API Gateway**
- **OAuth2** Google Login — auto-creates user profile on first login
- Tokens propagated downstream via `Authorization` header
- Fully **stateless** — no server-side sessions

### Role-Based Access Control (RBAC)

| Role | Registration | Permissions |
|---|---|---|
| `USER` | Public `/api/v1/auth/register` | Join queues, book appointments, view own data |
| `STAFF` | `/api/v1/auth/register-admin` + secret key | Call next, skip tickets, manage center queues |
| `ADMIN` | `/api/v1/auth/register-admin` + secret key | Full access — users, analytics, reports |

### JWT Payload

```json
{
  "sub": "user@example.com",
  "role": "ROLE_USER",
  "iat": 1714500000,
  "exp": 1714586400
}
```

---

## 📨 Event-Driven Flow

All async operations go through **Apache Kafka** with 6 dedicated topics:

```
                    USER JOINS QUEUE
                          │
                          ▼
              ┌───────────────────────┐
              │     Queue Service     │
              │  Issues ticket #N     │
              └───────────┬───────────┘
                          │ publishes
                          ▼
              ┌───────────────────────┐
              │  queue.ticket-issued  │  ← Kafka Topic
              └───────────┬───────────┘
                          │ consumed by
          ┌───────────────┴───────────────┐
          │                               │
┌─────────▼──────────┐         ┌──────────▼──────────┐
│ Notification Svc   │         │  Analytics Svc      │
│ → Email: ticket    │         │ → Record wait time  │
│   confirmation     │         │ → Update daily stats│
└────────────────────┘         └─────────────────────┘

                    STAFF CALLS NEXT
                          │
                          ▼
              ┌───────────────────────┐
              │     Queue Service     │
              │  Advances position    │
              └───────────┬───────────┘
                          │
          ┌───────────────┴───────────────┐
          │                               │
          ▼                               ▼
  queue.updated                    queue.turn-soon
  (broadcast via WS)             (2 tickets before)
          │                               │
  Analytics records              Notification Svc
  metrics                        → Email alert
```

### Kafka Topics

| Topic | Producer | Consumer(s) |
|---|---|---|
| `queue.ticket-issued` | Queue Service | Notification, Analytics |
| `queue.updated` | Queue Service | Analytics |
| `queue.turn-soon` | Queue Service | Notification |
| `appointment.created` | Appointment Service | Notification, Analytics |
| `appointment.cancelled` | Appointment Service | Notification, Analytics |
| `appointment.rescheduled` | Appointment Service | Notification |

---

## 📡 Real-Time Queue Tracking

WebSocket integration using **STOMP over SockJS**:

```
Client connects to:
  ws://localhost:8083/ws

Client subscribes to:
  /topic/queue/{queueId}

Server broadcasts on every callNext():
  {
    "queueId": "uuid",
    "currentPosition": 5,
    "waitingCount": 12
  }

Client receives live update → refreshes ticket position instantly
```

---

## 🛠️ Tech Stack

### ⚙️ Backend Core
![Java](https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2025.x-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

### 🔐 Security
![Spring Security](https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-JJWT%200.12-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)
![OAuth2](https://img.shields.io/badge/OAuth2-Google%20Login-4285F4?style=for-the-badge&logo=google&logoColor=white)

### 📨 Messaging & Communication
![Kafka](https://img.shields.io/badge/Apache%20Kafka-7.6-231F20?style=for-the-badge&logo=apachekafka&logoColor=white)
![WebSocket](https://img.shields.io/badge/WebSocket-STOMP%2FSockJS-009688?style=for-the-badge)
![OpenFeign](https://img.shields.io/badge/OpenFeign-REST%20Client-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![Resilience4j](https://img.shields.io/badge/Resilience4j-Circuit%20Breaker-FF6347?style=for-the-badge)

### 🗄️ Data
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)
![Hibernate](https://img.shields.io/badge/JPA%20%2F%20Hibernate-59666C?style=for-the-badge&logo=hibernate&logoColor=white)

### 📊 Observability
![Zipkin](https://img.shields.io/badge/Zipkin-Tracing-FE7139?style=for-the-badge)
![Micrometer](https://img.shields.io/badge/Micrometer-Metrics-1DB954?style=for-the-badge)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI%203-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)

### 📧 Notifications
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-Templates-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Gmail](https://img.shields.io/badge/Gmail%20SMTP-Email%20Alerts-EA4335?style=for-the-badge&logo=gmail&logoColor=white)

### 🐳 DevOps
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white)
![Docker Compose](https://img.shields.io/badge/Docker%20Compose-Multi--Service-2496ED?style=for-the-badge&logo=docker&logoColor=white)

---

## 🎯 Design Highlights

| Area | Implementation | Benefit |
|---|---|---|
| **Architecture** | Microservices (9 services) | Independent deployment & horizontal scaling |
| **Data Management** | Database-per-Service | Data isolation, bounded contexts |
| **Async Communication** | Kafka Event Bus | Loose coupling, resilient processing |
| **Real-Time** | WebSocket STOMP | Live queue position without polling |
| **API Layer** | Spring Cloud Gateway | Centralized security, rate limiting, fallback |
| **Service Discovery** | Netflix Eureka | Dynamic registration, zero-config routing |
| **Configuration** | Spring Cloud Config | Centralized YAML, environment profiles |
| **Resilience** | Resilience4j Circuit Breaker | Graceful degradation, auto-recovery |
| **Security** | JWT + OAuth2 + RBAC | Stateless auth, Google login, role enforcement |
| **Caching** | Redis / Caffeine | Sub-10s queue position cache |
| **Observability** | Zipkin + Micrometer | End-to-end tracing, latency monitoring |
| **Containerization** | Docker Compose | One-command environment setup |

---

## 🗃️ Database Design

Each service owns its database — **no shared tables, no cross-service JOINs**.

| Service | Database | Key Tables |
|---|---|---|
| Auth Service | `auth_db` | `users`, `refresh_tokens`, `oauth_accounts` |
| User Service | `user_db` | `user_profiles`, `user_preferences` |
| Queue Service | `queue_db` | `queues`, `queue_entries` |
| Appointment Service | `appointment_db` | `appointments`, `appointment_logs` |
| Notification Service | `notification_db` | `notifications` |
| Analytics Service | `analytics_db` | `wait_time_records`, `daily_reports` |

### Queue Entry Lifecycle

```
WAITING → CALLED → SERVING → SERVED
                ↓
             SKIPPED
WAITING → CANCELLED
```

### Appointment Lifecycle

```
PENDING → CONFIRMED → COMPLETED
        ↓
     RESCHEDULED → CONFIRMED
        ↓
     CANCELLED
```

---

## 📄 API Endpoints

### Auth Service — `/api/v1/auth`

| Method | Endpoint | Description | Auth |
|---|---|---|---|
| `POST` | `/register` | Register new user | Public |
| `POST` | `/login` | Login → JWT | Public |
| `POST` | `/register-admin` | Register STAFF/ADMIN with secret | Secret key |
| `GET` | `/oauth2/google` | Google OAuth2 redirect info | Public |

### Queue Service — `/api/v1/queues`

| Method | Endpoint | Description | Role |
|---|---|---|---|
| `POST` | `/` | Create today's queue | STAFF/ADMIN |
| `GET` | `/{id}` | Queue details | All |
| `GET` | `/center/{centerId}/today` | Today's queue for center | All |
| `POST` | `/{id}/join` | Join queue → get ticket | USER |
| `GET` | `/{id}/position/{ticket}` | Current position + ETA | All |
| `POST` | `/{id}/next` | Call next ticket | STAFF/ADMIN |
| `POST` | `/{id}/skip/{ticket}` | Skip absent user | STAFF/ADMIN |
| `DELETE` | `/{id}/cancel` | Cancel your ticket | USER |
| `PATCH` | `/{id}/close` | Close the queue | STAFF/ADMIN |
| `GET` | `/{id}/entries` | All tickets list | STAFF/ADMIN |
| `WS` | `/ws → /topic/queue/{id}` | Real-time broadcast | All |

### Appointment Service — `/api/v1/appointments`

| Method | Endpoint | Description | Role |
|---|---|---|---|
| `POST` | `/` | Book appointment | USER |
| `GET` | `/me` | My appointments | USER |
| `GET` | `/{id}` | Appointment details | USER/ADMIN |
| `PUT` | `/{id}/reschedule` | Reschedule | USER |
| `DELETE` | `/{id}` | Cancel | USER/ADMIN |
| `GET` | `/center/{id}` | Center appointments | STAFF/ADMIN |
| `GET` | `/{id}/logs` | Audit trail | STAFF/ADMIN |

### Analytics Service — `/api/v1/analytics`

| Method | Endpoint | Description | Role |
|---|---|---|---|
| `GET` | `/summary` | System-wide KPIs | ADMIN |
| `GET` | `/centers/{id}/report` | Daily report | ADMIN/STAFF |
| `GET` | `/centers/{id}/history` | Historical stats | ADMIN/STAFF |
| `GET` | `/centers/{id}/wait-times` | Wait time trends | ADMIN/STAFF |

---

## 📊 Observability

| Tool | Purpose | URL |
|---|---|---|
| **Zipkin** | Distributed traces — latency, spans, dependencies | http://localhost:9411 |
| **Eureka Dashboard** | Live service registry | http://localhost:8761 |
| **Swagger UI** | Interactive API docs per service | `http://localhost:{port}/swagger-ui.html` |
| **Actuator** | Health, metrics, circuit breaker status | `http://localhost:{port}/actuator` |

### Swagger Per Service

| Service | URL |
|---|---|
| Auth | http://localhost:8081/swagger-ui.html |
| User | http://localhost:8082/swagger-ui.html |
| Queue | http://localhost:8083/swagger-ui.html |
| Appointment | http://localhost:8084/swagger-ui.html |
| Notification | http://localhost:8085/swagger-ui.html |
| Analytics | http://localhost:8086/swagger-ui.html |
| **Gateway (Unified)** | **http://localhost:8080/swagger-ui.html** |

---

## 🚀 Getting Started

### ✅ Prerequisites

| Tool | Version |
|---|---|
| JDK | 21+ |
| Maven | 3.8+ |
| Docker Desktop | Latest |
| Git | Latest |

### 📥 Clone

```bash
git clone https://github.com/AhmedNawar2003/smart-queue-system.git
cd smart-queue-system
```

### 🔨 Build All Services

```bash
# Windows
build-all.bat

# Linux / Mac
chmod +x build-all.sh && ./build-all.sh
```

### 🐳 Run with Docker Compose

```bash
docker-compose up -d
```

### 🔢 Manual Startup Order

| Step | Service | Port | Why |
|---|---|---|---|
| 1 | **Eureka Server** | `8761` | Services must register first |
| 2 | **Config Server** | `8888` | All services pull config from it |
| 3 | **Kafka + Zookeeper** | `9092` | Required for event-driven services |
| 4 | **Redis** | `6379` | Required by Queue Service caching |
| 5 | **PostgreSQL DBs** | `5432–5437` | One DB per service |
| 6 | **Auth Service** | `8081` | Must be up before other services |
| 7 | **Business Services** | `8082–8086` | User, Queue, Appointment, Notification, Analytics |
| 8 | **API Gateway** | `8080` | Last — routes to all registered services |

### 🧪 Quick Test

```bash
# 1. Register a user
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"fullName":"Ahmed Nawar","email":"ahmed@test.com","password":"12345678"}'

# 2. Login → get JWT
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ahmed@test.com","password":"12345678"}'

# 3. Join a queue
curl -X POST http://localhost:8080/api/v1/queues/{queueId}/join \
  -H "Authorization: Bearer {token}"
```

---

## 🗺️ Ports Summary

| Service | Port |
|---|---|
| API Gateway | `8080` |
| Auth Service | `8081` |
| User Service | `8082` |
| Queue Service | `8083` |
| Appointment Service | `8084` |
| Notification Service | `8085` |
| Analytics Service | `8086` |
| Eureka Server | `8761` |
| Config Server | `8888` |
| Zipkin | `9411` |
| Redis | `6379` |
| Kafka | `9092` |
| auth_db | `5432` |
| user_db | `5433` |
| queue_db | `5434` |
| appointment_db | `5435` |
| notification_db | `5436` |
| analytics_db | `5437` |

---

## 📈 Future Enhancements

- [ ] **Next.js Frontend** — Full dashboard for users and admins with real-time queue tracking, booking wizard, and analytics charts
- [ ] **Mobile App** — React Native app for booking and queue tracking on the go
- [ ] **SMS Notifications** — Vonage/Twilio integration for Arabic SMS alerts
- [ ] **AI Wait Time Prediction** — ML model replacing the linear EMA estimator
- [ ] **Kubernetes Deployment** — Helm charts for production-grade orchestration
- [ ] **CI/CD Pipeline** — GitHub Actions for automated build, test & Docker push
- [ ] **Service Mesh** — Istio for advanced traffic management and mTLS
- [ ] **Multi-tenancy** — Support multiple governorates with isolated data
- [ ] **QR Code Check-in** — Scan ticket QR at the center for instant check-in
- [ ] **Rate Limiting** — Per-user API throttling via Redis at Gateway level
- [ ] **Saga Pattern** — Distributed transaction management for complex flows
- [ ] **Push Notifications** — Firebase FCM for mobile real-time alerts

---

## 👨‍💻 Author

<div align="center">

**Ahmed Nawar** — Backend Engineer · Java & Spring Boot Specialist

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Ahmed%20Nawar-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ahmed-nawar-246513243)
[![GitHub](https://img.shields.io/badge/GitHub-AhmedNawar2003-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/AhmedNawar2003)
[![Email](https://img.shields.io/badge/Email-nawarahmed652%40gmail.com-D14836?style=for-the-badge&logo=gmail&logoColor=white)](mailto:nawarahmed652@gmail.com)

<br/>

⭐ **If you find this project useful, please give it a star — it means a lot!**

</div>
