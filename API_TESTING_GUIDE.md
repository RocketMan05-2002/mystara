# API Testing Guide for Postman

## Base URLs
- **Seller Service**: `http://localhost:8081`
- **Buyer Service**: `http://localhost:8082`
- **Cart Service**: `http://localhost:8083`
- **Payment Service**: `http://localhost:8084`
- **Eureka Server**: `http://localhost:8761`

---

## 1. SELLER SERVICE (Port 8081)

### 1.1 Theme Management

#### Create Theme
```
POST http://localhost:8081/api/themes
Content-Type: application/json

{
  "name": "Gaming",
  "description": "Gaming themed products",
  "category": "Entertainment"
}
```

#### Get All Themes
```
GET http://localhost:8081/api/themes
```

#### Get Theme by ID
```
GET http://localhost:8081/api/themes/{themeId}
```

#### Get Themes by Category
```
GET http://localhost:8081/api/themes/category/Gaming
```

#### Update Theme
```
PUT http://localhost:8081/api/themes/{themeId}
Content-Type: application/json

{
  "name": "Gaming Updated",
  "description": "Updated description",
  "category": "Entertainment"
}
```

#### Delete Theme
```
DELETE http://localhost:8081/api/themes/{themeId}
```

---

### 1.2 Product Management

#### Create Product
```
POST http://localhost:8081/api/products
Content-Type: application/json

{
  "sellerId": "seller123",
  "name": "Gaming Mouse",
  "description": "High-performance gaming mouse",
  "price": 49.99,
  "stock": 100,
  "themeId": "{themeId}",
  "images": ["https://example.com/mouse1.jpg"],
  "status": "ACTIVE"
}
```

#### Get All Products
```
GET http://localhost:8081/api/products
```

#### Get Product by ID
```
GET http://localhost:8081/api/products/{productId}
```

#### Get Products by Seller
```
GET http://localhost:8081/api/products/seller/{sellerId}
```

#### Get Products by Theme
```
GET http://localhost:8081/api/products/theme/{themeId}
```

#### Get Products by Seller and Theme
```
GET http://localhost:8081/api/products/seller/{sellerId}/theme/{themeId}
```

#### Update Product
```
PUT http://localhost:8081/api/products/{productId}
X-Seller-Id: seller123
Content-Type: application/json

{
  "name": "Gaming Mouse Pro",
  "description": "Updated description",
  "price": 59.99,
  "stock": 150,
  "themeId": "{themeId}",
  "images": ["https://example.com/mouse2.jpg"],
  "status": "ACTIVE"
}
```

#### Update Stock
```
PATCH http://localhost:8081/api/products/{productId}/stock
X-Seller-Id: seller123
Content-Type: application/json

150
```

#### Reduce Stock (Called by Payment Service)
```
POST http://localhost:8081/api/products/{productId}/reduce-stock
Content-Type: application/json

5
```

#### Delete Product
```
DELETE http://localhost:8081/api/products/{productId}
X-Seller-Id: seller123
```

---

### 1.3 Purchase Request Management (Seller Side)

#### Get All Requests for Seller
```
GET http://localhost:8081/api/requests
X-Seller-Id: seller123
```

#### Get Pending Requests
```
GET http://localhost:8081/api/requests/pending
X-Seller-Id: seller123
```

#### Get Request by ID
```
GET http://localhost:8081/api/requests/{requestId}
```

#### Approve Request (Auto-adds to cart)
```
POST http://localhost:8081/api/requests/{requestId}/approve
X-Seller-Id: seller123
```

#### Reject Request
```
POST http://localhost:8081/api/requests/{requestId}/reject
X-Seller-Id: seller123
Content-Type: application/json

{
  "reason": "Out of stock"
}
```

---

## 2. BUYER SERVICE (Port 8082)

### 2.1 Product Browsing

#### Get All Available Products
```
GET http://localhost:8082/api/products
```

#### Get Product by ID
```
GET http://localhost:8082/api/products/{productId}
```

#### Get Products by Theme
```
GET http://localhost:8082/api/products/theme/{themeId}
```

#### Search Products by Name
```
GET http://localhost:8082/api/products/search?name=gaming
```

---

### 2.2 Theme Browsing

#### Get All Themes
```
GET http://localhost:8082/api/themes
```

#### Get Theme by ID
```
GET http://localhost:8082/api/themes/{themeId}
```

#### Get Themes by Category
```
GET http://localhost:8082/api/themes/category/Gaming
```

---

### 2.3 Purchase Request Management (Buyer Side)

#### Create Purchase Request
```
POST http://localhost:8082/api/requests
X-Buyer-Id: buyer123
Content-Type: application/json

{
  "productId": "{productId}",
  "quantity": 2,
  "message": "Please approve this purchase request"
}
```

#### Get All Requests by Buyer
```
GET http://localhost:8082/api/requests
X-Buyer-Id: buyer123
```

#### Get Pending Requests
```
GET http://localhost:8082/api/requests/pending
X-Buyer-Id: buyer123
```

#### Get Approved Requests
```
GET http://localhost:8082/api/requests/approved
X-Buyer-Id: buyer123
```

#### Get Request by ID
```
GET http://localhost:8082/api/requests/{requestId}
```

#### Cancel Request
```
POST http://localhost:8082/api/requests/{requestId}/cancel
X-Buyer-Id: buyer123
```

---

## 3. CART SERVICE (Port 8083)

### 3.1 Cart Management

#### Get Cart
```
GET http://localhost:8083/api/cart
X-Buyer-Id: buyer123
```

#### Add Approved Request to Cart
```
POST http://localhost:8083/api/cart/approved-request
X-Buyer-Id: buyer123
Content-Type: application/json

{
  "requestId": "{requestId}",
  "productId": "{productId}",
  "productName": "Gaming Mouse",
  "price": 49.99,
  "quantity": 2,
  "themeId": "{themeId}",
  "sellerId": "seller123"
}
```

#### Remove Item from Cart
```
DELETE http://localhost:8083/api/cart/items/{productId}
X-Buyer-Id: buyer123
```

#### Clear Cart
```
DELETE http://localhost:8083/api/cart
X-Buyer-Id: buyer123
```

---

## 4. PAYMENT SERVICE (Port 8084)

### 4.1 Payment Management

#### Create Payment
```
POST http://localhost:8084/api/payments
X-Buyer-Id: buyer123
Content-Type: application/json

{
  "cartId": "{cartId}",
  "items": [
    {
      "productId": "{productId}",
      "productName": "Gaming Mouse",
      "quantity": 2,
      "price": 49.99,
      "sellerId": "seller123"
    }
  ],
  "amount": 99.98,
  "currency": "USD"
}
```

#### Confirm Payment (Reduces stock automatically)
```
POST http://localhost:8084/api/payments/{paymentId}/confirm
Content-Type: application/json

{
  "paymentIntentId": "{stripePaymentIntentId}"
}
```

#### Get All Payments by Buyer
```
GET http://localhost:8084/api/payments
X-Buyer-Id: buyer123
```

#### Get Payment by ID
```
GET http://localhost:8084/api/payments/{paymentId}
```

---

## 5. COMPLETE FLOW EXAMPLE

### Step 1: Seller creates a theme
```
POST http://localhost:8081/api/themes
Content-Type: application/json

{
  "name": "Gaming",
  "description": "Gaming products",
  "category": "Entertainment"
}
```
**Response**: Save the `id` from response as `{themeId}`

---

### Step 2: Seller creates a product
```
POST http://localhost:8081/api/products
Content-Type: application/json

{
  "sellerId": "seller123",
  "name": "Gaming Mouse",
  "description": "High-performance gaming mouse",
  "price": 49.99,
  "stock": 100,
  "themeId": "{themeId}",
  "images": ["https://example.com/mouse.jpg"],
  "status": "ACTIVE"
}
```
**Response**: Save the `id` from response as `{productId}`

---

### Step 3: Buyer browses products by theme
```
GET http://localhost:8082/api/products/theme/{themeId}
```

---

### Step 4: Buyer creates purchase request
```
POST http://localhost:8082/api/requests
X-Buyer-Id: buyer123
Content-Type: application/json

{
  "productId": "{productId}",
  "quantity": 2,
  "message": "I would like to purchase this product"
}
```
**Response**: Save the `id` from response as `{requestId}`

---

### Step 5: Seller views pending requests
```
GET http://localhost:8081/api/requests/pending
X-Seller-Id: seller123
```

---

### Step 6: Seller approves request (Auto-adds to cart)
```
POST http://localhost:8081/api/requests/{requestId}/approve
X-Seller-Id: seller123
```

---

### Step 7: Buyer views cart
```
GET http://localhost:8083/api/cart
X-Buyer-Id: buyer123
```

---

### Step 8: Buyer creates payment
```
POST http://localhost:8084/api/payments
X-Buyer-Id: buyer123
Content-Type: application/json

{
  "cartId": "{cartId}",
  "items": [
    {
      "productId": "{productId}",
      "productName": "Gaming Mouse",
      "quantity": 2,
      "price": 49.99,
      "sellerId": "seller123"
    }
  ],
  "amount": 99.98,
  "currency": "USD"
}
```
**Response**: Save `id` as `{paymentId}` and `stripePaymentIntentId` as `{stripePaymentIntentId}`

---

### Step 9: Buyer confirms payment (Reduces stock)
```
POST http://localhost:8084/api/payments/{paymentId}/confirm
Content-Type: application/json

{
  "paymentIntentId": "{stripePaymentIntentId}"
}
```

---

### Step 10: Verify stock reduction
```
GET http://localhost:8081/api/products/{productId}
```
**Expected**: Stock should be reduced by the quantity purchased

---

## 6. TESTING TIPS

### Headers Required:
- **X-Seller-Id**: For seller operations (use: `seller123`)
- **X-Buyer-Id**: For buyer operations (use: `buyer123`)

### Common IDs to Replace:
- `{themeId}` - Theme ID from Step 1
- `{productId}` - Product ID from Step 2
- `{requestId}` - Request ID from Step 4
- `{cartId}` - Cart ID from Step 7
- `{paymentId}` - Payment ID from Step 8
- `{stripePaymentIntentId}` - Stripe Payment Intent ID from Step 8

### Stripe Testing:
- Use Stripe test keys: `sk_test_...` and `pk_test_...`
- For testing, you can use Stripe's test card: `4242 4242 4242 4242`
- Set environment variables or update `payment/src/main/resources/application.properties`

### MongoDB:
- Ensure MongoDB is running on `localhost:27017`
- Database name: `mystara_seller`

---

## 7. POSTMAN COLLECTION STRUCTURE

Create a Postman collection with these folders:
1. **Seller Service**
   - Theme Management
   - Product Management
   - Purchase Requests (Seller)
2. **Buyer Service**
   - Product Browsing
   - Theme Browsing
   - Purchase Requests (Buyer)
3. **Cart Service**
   - Cart Management
4. **Payment Service**
   - Payment Management
5. **Complete Flow**
   - Step-by-step workflow

---

## 8. ERROR RESPONSES

### Common Error Codes:
- `400 Bad Request` - Invalid request data
- `404 Not Found` - Resource not found
- `500 Internal Server Error` - Server error

### Example Error Response:
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Product not found with id: abc123"
}
```

