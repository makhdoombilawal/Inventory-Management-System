# Enterprise Multi-Tenant Inventory Management System

A robust, secure, and horizontally scalable Inventory Management System for vehicle dealers, built with a strictly-isolated, enterprise multi-tenant architecture.

## 🚀 Project Overview

This application manages vehicle inventories across multiple independent organizations (tenants). Each tenant's data is isolated at the database level and secured via a high-performance JWT-based authentication system.

### Core Features:
- **Strict Multi-Tenancy**: Zero cross-tenant data leakage using Hibernate filters and centralized tenant identity resolution.
- **Enterprise Security**: JWT-based authentication with `AuthUser` principal-level security context.
- **Role-Based Access (RBAC)**: Supports `GLOBAL_ADMIN`, `ADMIN`, `DEALER`, and `USER` roles.
- **Dynamic Tenant Provisioning**: Automatic creation of a unique tenant ID and organizational record upon first user registration.
- **Rate Limiting**: Per-tenant API rate limiting to protect system resources.

---

## 🛠️ How to Run

### Prerequisites:
- **Java 17 or higher**
- **Maven 3.8+**
- **Redis** (Optional, for caching)

### Setup & Launch:
1.  **Clone the Repository**:
    ```bash
    git clone [repository-url]
    cd "Inventory Management System"
    ```
2.  **Start the Application**:
    Run the provided automation script to set up environment variables and launch the Spring Boot server:
    ```bash
    .\mvnw spring-boot:run
    ```

3.  **Run QA Tests**:
    The system includes an exhaustive Java-based QA suite and a Python-based isolation audit:
    ```bash
    .\RUN-QA-TEST.bat
    python run_qa.py
    ```

---

## 📖 API Documentation

Access the interactive Swagger UI at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### 1. Authentication (`/api/auth`)

#### **Login**
Authenticates a user and returns a JWT token.
- **POST** `/api/auth/login`
- **Body**:
  ```json
  {
    "email": "admin@system.com",
    "password": "Admin@123"
  }
  ```
- **Example Curl**:
  ```bash
  curl -X POST http://localhost:8080/api/auth/login \
       -H "Content-Type: application/json" \
       -d '{"email": "admin@system.com", "password": "Admin@123"}'
  ```

---

### 2. User Management (`/api/users`)

#### **User Registration**
Registers a new user and creates a new tenant if `tenantId` is omitted.
- **POST** `/api/users/register`
- **Body**:
  ```json
  {
    "email": "newuser@test.com",
    "password": "Password@123"
  }
  ```

#### **Get Profile**
Retrieves the profile of the currently authenticated user.
- **GET** `/api/users/profile`
- **Headers**: `Authorization: Bearer <TOKEN>`

---

### 3. Dealer Management (`/api/dealers`)

#### **Create Dealer**
- **POST** `/api/dealers`
- **Body**:
  ```json
  {
    "name": "Acme Motors",
    "email": "contact@acme.com",
    "subscriptionType": "BASIC",
    "contactPerson": "John Doe",
    "phoneNumber": "123456789"
  }
  ```

#### **Get All Dealers**
- **GET** `/api/dealers`

---

### 4. Vehicle Inventory (`/api/vehicles`)

#### **Create Vehicle**
- **POST** `/api/vehicles`
- **Body**:
  ```json
  {
    "dealerId": 1,
    "model": "X5",
    "manufacturer": "BMW",
    "year": "2024",
    "price": 75000,
    "status": "AVAILABLE",
    "vin": "VIN123456789"
  }
  ```

#### **Search Vehicles**
- **POST** `/api/vehicles/search`
- **Body**:
  ```json
  {
    "model": "X5",
    "priceMax": 100000
  }
  ```

---

### 5. Inventory Management (`/api/inventory`)

#### **Update Stock Level**
- **PUT** `/api/inventory/stock`
- **Body**:
  ```json
  {
    "dealerId": 1,
    "vehicleId": 1,
    "quantity": 10
  }
  ```

---

### 6. Order Management (`/api/orders`)

#### **Create Order**
- **POST** `/api/orders`
- **Body**:
  ```json
  {
    "dealerId": 1,
    "items": [
      { "vehicleId": 1, "quantity": 1 }
    ]
  }
  ```

#### **Update Order Status**
- **PUT** `/api/orders/status`
- **Body**:
  ```json
  {
    "id": 1,
    "status": "PAID"
  }
  ```

---

### 7. Administrative APIs (`/api/admin`)
*Requires GLOBAL_ADMIN role (Tenant ID: 0)*

#### **System Statistics**
- **GET** `/api/admin/dealers/total`
- **GET** `/api/admin/dealers/countBySubscription`

#### **Create Administrative User**
- **POST** `/api/admin/users`
- **Body**:
  ```json
  {
    "email": "tenantadmin@acme.com",
    "password": "Admin@123",
    "role": "ADMIN",
    "tenantId": 1
  }
  ```
