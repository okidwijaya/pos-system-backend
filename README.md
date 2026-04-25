# 🧾 POS System Backend (Spring Boot)

Backend system untuk Point of Sale (POS) dengan fitur lengkap:
- Cart & Checkout
- Order Management
- Payment (Manual + Gateway-ready)
- Auth (JWT)
- Stock Management (Locking)

---

## 🚀 Tech Stack

- Java 17
- Spring Boot
- Spring Security (JWT)
- PostgreSQL
- JPA / Hibernate
- (Planned) Redis

---

## 📦 Features

### 🔐 Auth
- Register / Login
- JWT Authentication
- Refresh Token
- Forgot Password (email-based)

---

### 🛒 Cart System
- Add / Update / Remove item
- Auto calculate total
- User-isolated cart (multi cashier safe)

---

### 📦 Order System
- Checkout with transaction
- Stock locking (prevent oversell)
- Idempotency (anti double order)
- Order history (pagination + filter)

---

### 💳 Payment System
- Create payment (mock)
- Manual payment (cash / transfer)
- Verify / Reject (admin)
- Webhook handler (Midtrans/Xendit ready)
- Auto expire + stock rollback

---

## 🔒 Key Architecture

- Modular structure (feature-based)
- Transactional consistency
- Idempotency handling
- Separation of concern (Controller / Service / Repository)

---

## 🔄 Payment Flow

1. Checkout → create Order
2. Create Payment
3. Payment status:
    - PENDING
    - PAID
    - FAILED / EXPIRED
4. Webhook / Manual verification
5. Order updated accordingly

---

## 🧠 Advanced Concepts Used

- Database locking (`FOR UPDATE`)
- Idempotency key (prevent duplicate order)
- Transaction boundary
- Payment state machine
- Scheduler (expire payment)
- Role-based authorization

---

## 📌 API Example

### Checkout

POST /api/orders/checkout

Headers:
Idempotency-Key: abc-123

---

### Verify Payment

POST /api/payments/{paymentId}/verify

---

## 🛠️ How to Run

```bash
./mvnw spring-boot:run