# API Gateway Guide

## Overview
The API Gateway is the single entry point for all frontend requests. It routes requests to the appropriate microservices and handles CORS for frontend access.

**Gateway URL**: `http://localhost:8080`

---

## Gateway Routes

### 1. Seller Service Routes
All routes are prefixed with `/api/` and routed to the seller service:

- **Products**: `http://localhost:8080/api/products/**`
- **Themes**: `http://localhost:8080/api/themes/**`
- **Purchase Requests (Seller)**: `http://localhost:8080/api/requests/**`

**Service Name**: `seller` (registered in Eureka)

---

### 2. Buyer Service Routes
All routes are prefixed with `/api/buyer/` and rewritten to `/api/` before routing:

- **Products (Browsing)**: `http://localhost:8080/api/buyer/products/**` → Routes to buyer service
- **Themes (Browsing)**: `http://localhost:8080/api/buyer/themes/**` → Routes to buyer service
- **Purchase Requests (Buyer)**: `http://localhost:8080/api/buyer/requests/**` → Routes to buyer service

**Service Name**: `buyer` (registered in Eureka)

---

### 3. Cart Service Routes
- **Cart Management**: `http://localhost:8080/api/cart/**`

**Service Name**: `cart-service` (registered in Eureka)

---

### 4. Payment Service Routes
- **Payments**: `http://localhost:8080/api/payments/**`

**Service Name**: `payment-service` (registered in Eureka)

---

## CORS Configuration

The gateway is configured to allow **all origins** for frontend access:

- **Allowed Origins**: `*` (all origins)
- **Allowed Methods**: `GET, POST, PUT, DELETE, PATCH, OPTIONS`
- **Allowed Headers**: `*` (all headers)
- **Allow Credentials**: `true`
- **Max Age**: `3600` seconds

This allows any frontend application to make requests to the gateway without CORS issues.

---

## Frontend Integration Examples

### React/Angular/Vue Example

```javascript
// Base URL for all API calls
const API_BASE_URL = 'http://localhost:8080';

// Example: Get all products
fetch(`${API_BASE_URL}/api/products`, {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json',
  }
})
.then(response => response.json())
.then(data => console.log(data));

// Example: Create purchase request
fetch(`${API_BASE_URL}/api/buyer/requests`, {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'X-Buyer-Id': 'buyer123'
  },
  body: JSON.stringify({
    productId: 'product123',
    quantity: 2,
    message: 'Please approve'
  })
})
.then(response => response.json())
.then(data => console.log(data));

// Example: Get cart
fetch(`${API_BASE_URL}/api/cart`, {
  method: 'GET',
  headers: {
    'X-Buyer-Id': 'buyer123'
  }
})
.then(response => response.json())
.then(data => console.log(data));
```

---

## Complete API Endpoints via Gateway

### Seller Service (via Gateway)
```
GET    http://localhost:8080/api/products
POST   http://localhost:8080/api/products
GET    http://localhost:8080/api/products/{id}
PUT    http://localhost:8080/api/products/{id}
DELETE http://localhost:8080/api/products/{id}

GET    http://localhost:8080/api/themes
POST   http://localhost:8080/api/themes
GET    http://localhost:8080/api/themes/{id}

GET    http://localhost:8080/api/requests
POST   http://localhost:8080/api/requests/{id}/approve
POST   http://localhost:8080/api/requests/{id}/reject
```

### Buyer Service (via Gateway)
```
GET    http://localhost:8080/api/buyer/products
GET    http://localhost:8080/api/buyer/products/{id}
GET    http://localhost:8080/api/buyer/products/theme/{themeId}
GET    http://localhost:8080/api/buyer/products/search?name=...

GET    http://localhost:8080/api/buyer/themes
GET    http://localhost:8080/api/buyer/themes/{id}

POST   http://localhost:8080/api/buyer/requests
GET    http://localhost:8080/api/buyer/requests
GET    http://localhost:8080/api/buyer/requests/pending
GET    http://localhost:8080/api/buyer/requests/approved
POST   http://localhost:8080/api/buyer/requests/{id}/cancel
```

### Cart Service (via Gateway)
```
GET    http://localhost:8080/api/cart
POST   http://localhost:8080/api/cart/approved-request
DELETE http://localhost:8080/api/cart
DELETE http://localhost:8080/api/cart/items/{productId}
```

### Payment Service (via Gateway)
```
POST   http://localhost:8080/api/payments
POST   http://localhost:8080/api/payments/{id}/confirm
GET    http://localhost:8080/api/payments
GET    http://localhost:8080/api/payments/{id}
```

---

## Service Discovery

The gateway uses **Eureka** for service discovery. All services must be registered with Eureka at:
- **Eureka Server**: `http://localhost:8761`

The gateway automatically discovers services using their registered names:
- `seller` - Seller Service
- `buyer` - Buyer Service
- `cart-service` - Cart Service
- `payment-service` - Payment Service

---

## Load Balancing

The gateway uses **Spring Cloud LoadBalancer** for load balancing. If multiple instances of a service are running, the gateway will distribute requests across them.

---

## Health Checks

Gateway health endpoint:
```
GET http://localhost:8080/actuator/health
```

Gateway routes endpoint:
```
GET http://localhost:8080/actuator/gateway/routes
```

---

## Configuration

### Gateway Port
- **Default**: `8080`
- **Configurable**: Update `server.port` in `application.properties`

### Eureka Configuration
- **Eureka Server**: `http://localhost:8761/eureka/`
- **Service Discovery**: Enabled
- **Auto Registration**: Enabled

---

## Troubleshooting

### Service Not Found
- Ensure the service is registered with Eureka
- Check service name matches the route configuration
- Verify Eureka server is running

### CORS Issues
- Gateway is configured to allow all origins
- If issues persist, check browser console for specific errors
- Verify headers are being sent correctly

### Connection Refused
- Ensure all services are running
- Check service ports are not conflicting
- Verify Eureka server is accessible

---

## Security Notes

⚠️ **Current Configuration**: CORS allows all origins (`*`)

For production:
1. Replace `*` with specific frontend domain(s)
2. Add authentication/authorization
3. Implement rate limiting
4. Add request validation
5. Enable HTTPS

---

## Example Frontend Configuration

### Axios Example
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json',
  }
});

// Add buyer ID to all requests
api.interceptors.request.use(config => {
  config.headers['X-Buyer-Id'] = 'buyer123';
  return config;
});

export default api;
```

### Fetch Example
```javascript
const API_BASE = 'http://localhost:8080';

const apiCall = async (endpoint, options = {}) => {
  const response = await fetch(`${API_BASE}${endpoint}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      'X-Buyer-Id': 'buyer123',
      ...options.headers,
    },
  });
  return response.json();
};
```

