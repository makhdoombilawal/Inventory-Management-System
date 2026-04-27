# 🚀 Enterprise Multi-Tenant Inventory Management System

**Production-ready backend system for vehicle dealer and inventory management** with secure authentication, strict tenant isolation, and comprehensive REST APIs.

Built with **Clean Architecture**, **Modular Monolith** design, and **Enterprise Security**.

---

## 📋 Project Overview

A robust, secure, and scalable **Inventory Management System** for vehicle dealers with:

✅ **Multi-Tenant Architecture** - Strict tenant isolation at database level  
✅ **JWT Authentication** - Secure token-based user authentication  
✅ **Role-Based Access Control (RBAC)** - 4 roles: GLOBAL_ADMIN, ADMIN, DEALER, USER  
✅ **Swagger UI with Authorization** - Interactive API documentation with Bearer token support  
✅ **MySQL Relational Database** - With foreign keys and referential integrity  
✅ **Clean Architecture** - Layered design: Controller → Service → Repository → Database  
✅ **Enterprise Security** - AES encryption for sensitive data, password hashing, rate limiting  
✅ **Comprehensive Error Handling** - Standardized error responses with HTTP status codes  
✅ **Production-Ready** - Logging, caching, validation, and transaction management  

---

## 🏗️ Architecture

### **Technology Stack**
- **Java 17+** with Spring Boot 3.2.0
- **Spring Security** with JWT (JJWT)
- **Spring Data JPA** for data access
- **MySQL 8.0** relational database
- **Springdoc OpenAPI** (Swagger)
- **Redis** for caching (optional)
- **Lombok** for reducing boilerplate
- **MapStruct** for DTO mapping
- **Validation API** (Jakarta)

### **Design Patterns**
- **Modular Monolith** - Organized by domain modules
- **Clean Architecture** - Clear separation of concerns
- **Repository Pattern** - Abstraction over data access
- **Dependency Injection** - Spring-managed beans
- **Global Exception Handling** - Centralized error management
- **DTO Pattern** - Data transfer objects for APIs
- **Strategy Pattern** - Pluggable authentication

### **Project Structure**
```
com.inventoryapp/
├── auth/
│   ├── controller/          # Authentication endpoints
│   ├── service/             # Authentication business logic
│   ├── entity/              # User and Role entities
│   ├── repository/          # User/Role data access
│   ├── dto/                 # Login/Register DTOs
│   ├── security/
│   │   ├── jwt/            # JWT token generation/validation
│   │   ├── filter/         # JWT authentication filter
│   │   └── config/         # Security configuration
│   └── security/AuthUser    # Security principal
├── dealer/
│   ├── controller/          # Dealer management endpoints
│   ├── service/             # Dealer business logic
│   ├── entity/Dealer        # Dealer entity
│   ├── repository/          # Dealer data access (tenant-scoped)
│   └── dto/                 # Dealer DTOs
├── vehicle/
│   ├── controller/          # Vehicle management endpoints
│   ├── service/             # Vehicle business logic
│   ├── entity/Vehicle       # Vehicle entity with Dealer FK
│   ├── repository/          # Vehicle data access (with filters)
│   └── dto/                 # Vehicle DTOs
├── inventory/
│   ├── controller/          # Inventory endpoints
│   ├── service/             # Inventory logic
│   ├── entity/Inventory     # Inventory tracking
│   └── repository/          # Inventory queries
├── order/
│   ├── controller/          # Order management
│   ├── service/             # Order processing
│   ├── entity/              # Order and OrderItem entities
│   ├── repository/          # Order queries
│   └── dto/                 # Order DTOs
├── admin/
│   ├── controller/          # Admin-only operations
│   ├── service/             # Admin logic (global scope)
│   └── dto/                 # Admin response DTOs
├── common/
│   ├── domain/
│   │   └── BaseEntity       # Base class with ID, tenantId, timestamps
│   ├── tenant/
│   │   ├── TenantContext    # Thread-local tenant holder
│   │   ├── TenantFilter     # Tenant extraction filter
│   │   ├── entity/Tenant    # Tenant entity
│   │   └── repository/      # Tenant queries
│   ├── security/
│   │   ├── AesUtil          # AES encryption/decryption
│   │   └── PasswordEncoder  # Password hashing
│   ├── exception/           # Custom exception classes
│   ├── response/
│   │   ├── ApiResponse      # Standard response wrapper
│   │   └── ResponseUtil     # Response building helpers
│   ├── swagger/OpenAPIConfig # Swagger configuration
│   ├── ratelimiter/         # Rate limiting service
│   └── cache/               # Caching configuration
└── InventoryManagementApplication  # Main entry point
```

---

## 🔐 Core Features

### **1. Multi-Tenant Architecture**
- **Tenant Isolation**: All queries filtered by `tenant_id`
- **Header-Based Identity**: `X-Tenant-Id` HTTP header for tenant context
- **JWT Integration**: Tenant ID stored in JWT token
- **TenantContext**: Thread-local holder for tenant information
- **Automatic Filtering**: BaseEntity's `@PrePersist` auto-assigns tenant ID

### **2. JWT Authentication System**
- **Token Generation**: `JwtTokenProvider` generates secure tokens
- **Claims**: Includes `userId`, `tenantId`, `role`, and `email`
- **Validation**: Token signature and expiration verified
- **Duration**: 24-hour expiration (configurable)
- **Secret Key**: 256-bit HMAC-SHA256 signing

### **3. Security & Encryption**
- **Password Hashing**: BCrypt with salt for password security
- **Email Encryption**: AES-GCM 128-bit encryption for sensitive data
- **Rate Limiting**: Per-tenant, per-user API rate limiting
- **CORS**: Configured for cross-origin requests
- **CSRF**: Disabled for stateless JWT architecture

### **4. Role-Based Access Control**
```
- GLOBAL_ADMIN: System-wide operations, user management
- ADMIN: Tenant-scoped operations (dealer/vehicle management)
- DEALER: Vendor operations (view own inventory)
- USER: Standard user operations
```

### **5. Database Relationships**
```
Tenants
  ├── Users (email unique per tenant)
  ├── Dealers (subscription_type: BASIC/PREMIUM/ENTERPRISE)
  └── Vehicles (belongs to Dealer)
        ├── Inventory (stock tracking)
        └── Orders (order items reference vehicles)
```

---

## ⚙️ Setup & Configuration

### **Prerequisites**
- **Java 17+** (`java -version`)
- **Maven 3.8+** (`mvn -version`)
- **MySQL 8.0+** running on `localhost:3306`
- **Redis 6.0+** (optional, for caching)

### **Database Setup**

1. **Create Database**:
   ```sql
   CREATE DATABASE inventory_db 
     CHARACTER SET utf8mb4 
     COLLATE utf8mb4_unicode_ci;
   ```

2. **Database Credentials** (in `application.yml`):
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/inventory_db
       username: root
       password: root
   ```

3. **Schema Auto-Creation**:
   - Hibernate automatically creates tables on startup
   - SQL: `hibernat e.ddl-auto: update`
   - Manual import: See `src/main/resources/full_schema.sql`

### **Application Setup**

1. **Clone Repository**:
   ```bash
   git clone [repository-url]
   cd "Inventory Management System"
   ```

2. **Build Project**:
   ```bash
   mvn clean install
   ```

3. **Run Application**:
   ```bash
   mvn spring-boot:run
   ```
   
   Or use the provided batch script:
   ```bash
   .\FIXED-JAVA-START.bat
   ```

4. **Verify Startup**:
   - Application starts on `http://localhost:8080`
   - Check logs for "Started InventoryManagementApplication"
   - Access Swagger UI: `http://localhost:8080/swagger-ui.html`

### **Configuration Properties** (`application.yml`)

```yaml
# Server
server:
  port: 8080

# Database
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/inventory_db
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

# JWT
jwt:
  secret: mySecretKeyForJWTTokenGenerationAndValidation12345
  expiration: 86400000  # 24 hours

# Encryption (AES-128)
aes:
  encryption:
    key: Y2hhbmdlbWUxMjM0NTY3OA==

# Rate Limiting
rate-limiting:
  enabled: true
  requests-per-minute: 60
  per-tenant: true
  per-user: true

# Redis (optional)
spring:
  data:
    redis:
      host: localhost
      port: 6379
```

### **Seeded Admin User** (on first run)
```
Email: admin@system.com
Password: Admin@123
Role: GLOBAL_ADMIN
Tenant: system (ID: 0)
```

---

## 🔐 Authentication & Authorization

### **JWT Authentication Flow**

1. **Login Request**:
   ```bash
   POST /api/auth/login
   {
     "email": "admin@system.com",
     "password": "Admin@123"
   }
   ```

2. **Response with JWT Token**:
   ```json
   {
     "status": "success",
     "message": "Login successful",
     "data": {
       "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
       "email": "admin@system.com",
       "role": "GLOBAL_ADMIN"
     }
   }
   ```

3. **Use Token in Headers**:
   ```bash
   Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
   X-Tenant-Id: 1
   ```

4. **Swagger Authorization**:
   - Click "Authorize" button in Swagger UI
   - Enter: `Bearer <your_token>`
   - All endpoints now use this token

### **Role-Based Endpoints**

| Endpoint | Required Role | Access |
|----------|:-------------|--------|
| `POST /api/auth/**` | None | Public |
| `GET /api/dealers/**` | ADMIN, GLOBAL_ADMIN | Tenant-scoped |
| `POST /api/dealers/**` | ADMIN, GLOBAL_ADMIN | Tenant-scoped |
| `GET /api/vehicles/**` | Authenticated | Tenant-scoped |
| `POST /api/vehicles/**` | ADMIN, GLOBAL_ADMIN | Tenant-scoped |
| `GET /api/admin/**` | GLOBAL_ADMIN | System-wide |
| `POST /api/admin/**` | GLOBAL_ADMIN | System-wide |

### **Tenant Isolation**

Every request requires:
1. **JWT Token**: Contains `tenantId`
2. **HTTP Header**: `X-Tenant-Id` (optional, validated against JWT)

If mismatched → **403 Forbidden**

---

## 📚 API Reference

### **1. Authentication** (`/api/auth`)

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@system.com",
  "password": "Admin@123"
}

Response:
{
  "status": "success",
  "message": "Login successful",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "email": "admin@system.com",
    "role": "GLOBAL_ADMIN"
  }
}
```

#### Health Check
```
GET /api/auth/health

Response:
{
  "status": "success",
  "message": "Authentication service is running",
  "data": "OK"
}
```

---

### **2. User Management** (`/api/users`)

#### Get Profile
```
GET /api/users/profile
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1

Response:
{
  "status": "success",
  "message": "User profile retrieved",
  "data": {
    "id": 1,
    "email": "user@example.com",
    "role": "USER",
    "tenantId": 1
  }
}
```

---

### **3. Dealer Management** (`/api/dealers`)

#### Create Dealer
```
POST /api/dealers
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
Content-Type: application/json

{
  "name": "Acme Motors",
  "email": "contact@acme.com",
  "subscriptionType": "PREMIUM",
  "contactPerson": "John Doe",
  "phoneNumber": "+1-555-0123",
  "address": "123 Main St, City"
}

Response: 201 Created
{
  "status": "success",
  "message": "Dealer created successfully",
  "data": {
    "id": 1,
    "name": "Acme Motors",
    "subscriptionType": "PREMIUM",
    "tenantId": 1
  }
}
```

#### Get All Dealers
```
GET /api/dealers
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1

Response: 200 OK
{
  "status": "success",
  "message": "Dealers retrieved successfully",
  "data": [
    {
      "id": 1,
      "name": "Acme Motors",
      "subscriptionType": "PREMIUM",
      ...
    }
  ]
}
```

#### Get Dealer by ID
```
GET /api/dealers/{id}
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
```

#### Update Dealer
```
PUT /api/dealers/{id}
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
Content-Type: application/json

{
  "name": "Acme Motors Updated",
  "subscriptionType": "ENTERPRISE"
}
```

#### Delete Dealer
```
DELETE /api/dealers/{id}
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1

Response: 200 OK
{
  "status": "success",
  "message": "Dealer deleted successfully"
}
```

---

### **4. Vehicle Management** (`/api/vehicles`)

#### Create Vehicle
```
POST /api/vehicles
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
Content-Type: application/json

{
  "dealerId": 1,
  "model": "BMW X5",
  "manufacturer": "BMW",
  "year": "2024",
  "price": 75000.00,
  "status": "AVAILABLE",
  "vin": "WBA123456789ABC12",
  "description": "Luxury SUV"
}

Response: 201 Created
```

#### Search Vehicles (with Filters)
```
POST /api/vehicles/search
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
Content-Type: application/json

{
  "model": "X5",
  "status": "AVAILABLE",
  "priceMin": 50000,
  "priceMax": 100000,
  "subscription": "PREMIUM"
}

Response: 200 OK
{
  "status": "success",
  "data": [...]
}
```

#### Get All Vehicles
```
GET /api/vehicles/all
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
```

#### Get Vehicle by ID
```
GET /api/vehicles/{id}
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
```

#### Update Vehicle
```
PATCH /api/vehicles/{id}
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
Content-Type: application/json

{
  "price": 70000,
  "status": "SOLD"
}
```

#### Delete Vehicle
```
DELETE /api/vehicles/{id}
Authorization: Bearer <TOKEN>
X-Tenant-Id: 1
```

---

### **5. Admin Operations** (`/api/admin`)

#### Create Admin User (GLOBAL_ADMIN only)
```
POST /api/admin/users
Authorization: Bearer <GLOBAL_ADMIN_TOKEN>
X-Tenant-Id: 0
Content-Type: application/json

{
  "email": "newadmin@system.com",
  "password": "Admin@123",
  "role": "ADMIN"
}

Response: 201 Created
```

#### Count Dealers by Subscription (Global)
```
GET /api/admin/dealers/countBySubscription
Authorization: Bearer <GLOBAL_ADMIN_TOKEN>
X-Tenant-Id: 0

Response:
{
  "status": "success",
  "data": {
    "BASIC": { "count": 5 },
    "PREMIUM": { "count": 8 },
    "ENTERPRISE": { "count": 2 }
  }
}
```

#### Get Total Dealers Count (Global)
```
GET /api/admin/dealers/total
Authorization: Bearer <GLOBAL_ADMIN_TOKEN>
X-Tenant-Id: 0

Response:
{
  "status": "success",
  "data": {
    "total": 15,
    "active": 14,
    "inactive": 1
  }
}
```

---

## ❌ Error Handling

All errors follow the standard response format:

```json
{
  "status": "error",
  "message": "Error description",
  "errors": { "fieldName": "field error" }
}
```

### **Common Error Codes**

| Status | Code | Reason |
|--------|:----:|--------|
| 400 | Missing tenant header or invalid request |
| 401 | Invalid/expired JWT token |
| 403 | Forbidden - tenant mismatch or insufficient role |
| 404 | Resource not found |
| 409 | Resource already exists (duplicate) |
| 500 | Internal server error |

### **Error Examples**

**Missing Tenant Header**:
```json
{
  "status": "error",
  "message": "Unauthorized request: Missing tenant identity from JWT"
}
```

**Invalid JWT**:
```json
{
  "status": "error",
  "message": "Invalid credentials"
}
```

**Insufficient Role**:
```json
{
  "status": "error",
  "message": "Access Denied"
}
```

---

## 🧪 Testing

### **1. Swagger UI Testing**

1. Open `http://localhost:8080/swagger-ui.html`
2. Click "Authorize" → Enter `Bearer <TOKEN>`
3. Try endpoints from interactive docs

### **2. QA Automated Tests**

Run comprehensive test suite:

```bash
# Java QA Tests
java -cp target/classes:target/test-classes com.inventoryapp.qa.EnterpriseQATest

# Or via batch file
.\RUN-QA-TEST.bat

# Python Isolation Audit (verifies tenant isolation)
python run_qa.py
```

### **3. cURL Testing**

**Login**:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email": "admin@system.com", "password": "Admin@123"}'
```

**Create Dealer**:
```bash
curl -X POST http://localhost:8080/api/dealers \
  -H "Authorization: Bearer <TOKEN>" \
  -H "X-Tenant-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Dealer",
    "email": "test@dealer.com",
    "subscriptionType": "BASIC",
    "contactPerson": "John",
    "phoneNumber": "1234567890"
  }'
```

**Search Vehicles**:
```bash
curl -X POST http://localhost:8080/api/vehicles/search \
  -H "Authorization: Bearer <TOKEN>" \
  -H "X-Tenant-Id: 1" \
  -H "Content-Type: application/json" \
  -d '{
    "model": "X5",
    "priceMax": 100000
  }'
```

### **4. Postman Collection**

Import endpoints into Postman for convenient testing:

1. **Create Collection**: "Inventory Management API"
2. **Environment Variables**:
   - `{{baseUrl}}`: http://localhost:8080
   - `{{token}}`: <your_jwt_token>
   - `{{tenantId}}`: 1
3. **Authorization** (for all requests):
   - Type: Bearer Token
   - Token: `{{token}}`
4. **Headers** (for all requests):
   - `X-Tenant-Id: {{tenantId}}`

---

## 🔍 Key Implementation Details

### **Tenant Isolation**

1. **Automatic Assignment**: `BaseEntity.@PrePersist()` sets `tenantId` from `TenantContext`
2. **Query Filtering**: All repositories include tenant filtering
3. **JWT Integration**: `JwtAuthenticationFilter` extracts tenant from token
4. **Validation**: `TenantFilter` validates tenant for protected endpoints

### **DTO Mapping**

- **Entity → DTO**: Done in service layer
- **DTO → Entity**: Performed with validation
- **Password**: Never exposed in DTOs (only used for hashing)
- **Email**: Encrypted in database, decrypted in service

### **Transaction Management**

- **@Transactional**: Marks service methods for transaction boundary
- **Rollback**: Automatic on unchecked exceptions
- **Propagation**: REQUIRED (creates transaction if none exists)

### **Security Chain**

```
Request
  ↓
RateLimiterFilter (rate limiting check)
  ↓
JwtAuthenticationFilter (JWT validation → AuthUser principal)
  ↓
TenantFilter (tenant extraction & context setting)
  ↓
SecurityFilterChain (Spring Security authorization)
  ↓
@PreAuthorize (method-level role checking)
  ↓
Controller Handler
```

### **Caching Strategy**

- **Dealers by Tenant**: Cached with key `dealers_all#tenantId`
- **Cache Invalidation**: Manual on create/update/delete
- **Optional Redis**: Enable in `application.yml` for distributed caching

---

## 📊 Database Schema

### **Key Tables**

| Table | Purpose | Tenant Scoped |
|-------|---------|:------------|
| users | User accounts | Yes |
| roles | User roles (system-wide) | No |
| tenants | Tenant organizations | No |
| dealers | Vehicle dealers | Yes |
| vehicles | Inventory items | Yes |
| inventory | Stock levels | Yes |
| vehicle_orders | Customer orders | Yes |
| order_items | Order line items | Yes |

### **Foreign Key Relationships**

```
users.role_id → roles.id
vehicles.dealer_id → dealers.id
inventory.vehicle_id → vehicles.id
inventory.dealer_id → dealers.id
vehicle_orders.dealer_id → dealers.id
order_items.vehicle_id → vehicles.id
order_items.order_id → vehicle_orders.id
```

### **Indexes** (for performance)

- `dealers(tenant_id, subscription_type, active)`
- `vehicles(tenant_id, status, dealer_id)`
- `users(email, tenant_id, active)`
- `vehicle_orders(status, tenant_id, dealer_id)`

---

## 🚀 Deployment

### **Production Configuration**

1. **Environment Variables**:
   ```bash
   export JWT_SECRET=your-super-secure-256-bit-key
   export ENCRYPTION_KEY=your-aes-encryption-key
   export DB_USERNAME=prod_user
   export DB_PASSWORD=prod_password
   export SPRING_PROFILES_ACTIVE=production
   ```

2. **Database**:
   - Use managed MySQL (AWS RDS, Azure Database, etc.)
   - Enable SSL/TLS for connections
   - Regular backups enabled

3. **Application**:
   - Build Docker image: `docker build -t inventory-api:latest .`
   - Deploy to Kubernetes/Docker Compose
   - Configure health check endpoint: `GET /api/auth/health`

4. **Monitoring**:
   - Enable actuator endpoints: `http://localhost:8080/actuator`
   - Monitor application logs and metrics
   - Set up alerts for error rates

---

## 🐛 Troubleshooting

### **Port Already in Use**
```bash
# Find process using port 8080
netstat -ano | findstr :8080
# Kill process (replace PID)
taskkill /PID <PID> /F
```

### **Database Connection Failed**
- Verify MySQL is running: `mysql -u root -p`
- Check credentials in `application.yml`
- Ensure `inventory_db` exists

### **JWT Token Invalid**
- Token may have expired (24-hour expiration)
- Re-login to get fresh token
- Verify JWT secret matches in `application.yml`

### **Tenant Isolation Issues**
- Ensure `X-Tenant-Id` header matches JWT tenant
- Check `TenantContext` is properly set
- Verify all repository queries include `tenantId` filter

---

## 📝 License & Credits

This project is provided as an enterprise-grade reference implementation for:
- Multi-tenant application architecture
- JWT-based authentication
- Spring Boot security patterns
- RESTful API design

---

## ✅ Checklist for Production Deployment

- [ ] Change default admin password
- [ ] Update JWT secret key (production-grade)
- [ ] Enable Redis caching
- [ ] Configure database backups
- [ ] Set up SSL/TLS certificates
- [ ] Enable monitoring & logging
- [ ] Configure rate limiting appropriately
- [ ] Set encryption keys securely
- [ ] Test all endpoints with production database
- [ ] Document any customizations made

---

## 📞 Support & Documentation

- **Swagger API Docs**: `http://localhost:8080/swagger-ui.html`
- **API Schema**: `http://localhost:8080/v3/api-docs`
- **Issues**: File bugs or feature requests
- **Questions**: Refer to project documentation

---

**Version**: 1.0.0  
**Last Updated**: 2026-03-31  
**Status**: Production Ready ✅
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
