# JWT Authentication Architecture

## Overview

JWT authentication is implemented with a **centralized filter at the Gateway** and **token generation at individual services** (Buyer and Seller).

---

## Architecture Flow

```
Frontend → API Gateway (JWT Filter) → Microservices
                ↓
         Validates Token
                ↓
         Extracts User Info
                ↓
         Adds Headers to Request
                ↓
         Routes to Service
```

---

## Token Generation

### Location: Individual Services (Buyer/Seller)

**Buyer Service:**
- `POST /api/auth/register` - Generates JWT token for buyer
- `POST /api/auth/login` - Generates JWT token for buyer

**Seller Service:**
- `POST /api/auth/register` - Generates JWT token for seller
- `POST /api/auth/login` - Generates JWT token for seller

**Token Contains:**
- `userId` - User ID
- `role` - BUYER or SELLER
- `email` - User email
- `expiration` - Token expiration time

---

## Token Validation

### Location: API Gateway

**JwtAuthenticationFilter** validates all incoming requests (except public endpoints):

1. **Public Endpoints** (No token required):
   - `/api/auth/**` - Authentication endpoints
   - `/api/buyer/auth/**` - Buyer authentication
   - `/api/seller/auth/**` - Seller authentication
   - `/api/products` - Public product browsing
   - `/api/themes` - Public theme browsing
   - `/api/buyer/products` - Public product browsing
   - `/api/buyer/themes` - Public theme browsing

2. **Protected Endpoints** (Token required):
   - All other endpoints require valid JWT token

---

## Gateway Filter Process

1. **Check if Public Endpoint**
   - If public → Allow request to pass through

2. **Extract Token**
   - Get `Authorization: Bearer <token>` header
   - If missing → Return 401 Unauthorized

3. **Validate Token**
   - Check token signature
   - Check token expiration
   - If invalid → Return 401 Unauthorized

4. **Extract User Information**
   - Extract `userId`, `role`, `email` from token

5. **Add Headers to Request**
   - `X-User-Id` - User ID
   - `X-User-Role` - BUYER or SELLER
   - `X-User-Email` - User email
   - `X-Buyer-Id` - If role is BUYER
   - `X-Seller-Id` - If role is SELLER

6. **Forward Request**
   - Forward request with headers to downstream service

---

## Service Configuration

### Buyer Service
- **Security**: Permits all requests (validation at gateway)
- **Headers Accepted**: `X-Buyer-Id`, `X-User-Id`
- **Token Generation**: Yes (for login/register)

### Seller Service
- **Security**: Permits all requests (validation at gateway)
- **Headers Accepted**: `X-Seller-Id`, `X-User-Id`
- **Token Generation**: Yes (for login/register)

---

## JWT Configuration

All services (Gateway, Buyer, Seller) must use the **same JWT secret**:

```properties
jwt.secret=mySecretKeyForJWTTokenGeneration12345678901234567890
jwt.expiration=86400000  # 24 hours in milliseconds
```

**Important**: The secret must be at least 32 characters for HS256 algorithm.

---

## Request Flow Example

### 1. User Login
```
Frontend → POST /api/buyer/auth/login
         → Gateway (public endpoint, passes through)
         → Buyer Service
         → Validates credentials
         → Generates JWT token
         → Returns token to frontend
```

### 2. Authenticated Request
```
Frontend → GET /api/buyer/requests
         → Header: Authorization: Bearer <token>
         → Gateway JWT Filter:
            - Validates token ✓
            - Extracts userId, role
            - Adds X-Buyer-Id header
         → Buyer Service
         → Uses X-Buyer-Id from header
         → Returns response
```

### 3. Invalid Token
```
Frontend → GET /api/buyer/requests
         → Header: Authorization: Bearer <invalid-token>
         → Gateway JWT Filter:
            - Validates token ✗
            - Returns 401 Unauthorized
         → Request never reaches service
```

---

## Headers Passed to Services

### For Buyers:
- `X-User-Id`: User ID
- `X-User-Role`: BUYER
- `X-User-Email`: User email
- `X-Buyer-Id`: User ID (same as X-User-Id)

### For Sellers:
- `X-User-Id`: User ID
- `X-User-Role`: SELLER
- `X-User-Email`: User email
- `X-Seller-Id`: User ID (same as X-User-Id)

---

## Frontend Integration

### Storing Token
```javascript
localStorage.setItem('token', response.data.token);
```

### Sending Token
```javascript
// Automatically added by axios interceptor
headers: {
  'Authorization': `Bearer ${token}`
}
```

### Token Expiration
- Token expires after 24 hours (default)
- Frontend should handle 401 responses
- Redirect to login page on token expiration

---

## Security Benefits

1. **Centralized Validation**: All token validation at gateway
2. **Service Isolation**: Services don't need to validate tokens
3. **Consistent Security**: Single point of security enforcement
4. **Performance**: Token validation happens once at gateway
5. **Scalability**: Services can scale without security concerns

---

## Testing

### Test Public Endpoint (No Token)
```bash
curl http://localhost:8080/api/products
# Should work without token
```

### Test Protected Endpoint (With Token)
```bash
curl -H "Authorization: Bearer <token>" http://localhost:8080/api/buyer/requests
# Should work with valid token
```

### Test Protected Endpoint (Without Token)
```bash
curl http://localhost:8080/api/buyer/requests
# Should return 401 Unauthorized
```

---

## Troubleshooting

### Token Validation Fails
- Check JWT secret matches across all services
- Verify token hasn't expired
- Check token format (Bearer <token>)

### Headers Not Reaching Service
- Verify gateway filter is running
- Check gateway logs for filter execution
- Ensure filter order is correct (-100)

### 401 Unauthorized on Valid Token
- Check JWT secret configuration
- Verify token was generated with same secret
- Check token expiration time

---

## Configuration Files

### Gateway
- `application.properties`: JWT secret and expiration
- `JwtAuthenticationFilter.java`: Token validation logic
- `JwtUtil.java`: JWT utility methods

### Buyer/Seller Services
- `application.properties`: JWT secret (must match gateway)
- `JwtUtil.java`: Token generation utility
- `AuthService.java`: Login/Register with token generation
- `SecurityConfig.java`: Permits all (validation at gateway)

