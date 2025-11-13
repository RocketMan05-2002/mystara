# Database Architecture for Mystara Microservices

## Database Separation Strategy

Each microservice now has its own MongoDB database for better separation of concerns and scalability.

---

## Database Configuration

### 1. Seller Service Database: `mystara_seller`
**Connection**: `mongodb+srv://mystara:mystara@cluster0.4hocdmk.mongodb.net/mystara_seller?appName=Cluster0`

**Collections**:
- `products` - Product listings by sellers
- `themes` - Product themes/categories
- `purchase_requests` - Purchase requests (shared with seller for approval)

**Purpose**: 
- Manages all product and theme data
- Handles purchase request approvals

---

### 2. Buyer Service Database: `mystara_seller` (Shared)
**Connection**: `mongodb+srv://mystara:mystara@cluster0.4hocdmk.mongodb.net/mystara_seller?appName=Cluster0`

**Collections Accessed**:
- `products` - Read-only access to products
- `themes` - Read-only access to themes
- `purchase_requests` - Read/write access to purchase requests

**Note**: 
- Currently, buyer service shares seller database for products, themes, and purchase_requests
- **Future Enhancement**: 
  - Create separate `mystara_buyer` database for buyer-specific data
  - Refactor buyer service to call Seller Service API for products/themes instead of direct database access
  - Move purchase_requests to buyer database or keep shared for seller approval workflow

---

### 3. Cart Service Database: `mystara_cart`
**Connection**: `mongodb+srv://mystara:mystara@cluster0.4hocdmk.mongodb.net/mystara_cart?appName=Cluster0`

**Collections**:
- `carts` - Shopping carts for buyers

**Purpose**: 
- Manages shopping cart data independently

---

### 4. Payment Service Database: `mystara_payment`
**Connection**: `mongodb+srv://mystara:mystara@cluster0.4hocdmk.mongodb.net/mystara_payment?appName=Cluster0`

**Collections**:
- `payments` - Payment transactions

**Purpose**: 
- Manages payment records independently

---

## Data Flow

### Current Architecture:
```
Seller Service (mystara_seller DB)
├── products (read/write)
├── themes (read/write)
└── purchase_requests (read/write)

Buyer Service (mystara_buyer DB)
├── purchase_requests (read/write)
└── [Reads products/themes from mystara_seller DB]

Cart Service (mystara_cart DB)
└── carts (read/write)

Payment Service (mystara_payment DB)
└── payments (read/write)
```

### Recommended Future Architecture:
```
Seller Service (mystara_seller DB)
├── products (read/write)
├── themes (read/write)
└── purchase_requests (read/write)

Buyer Service (mystara_buyer DB)
├── purchase_requests (read/write)
└── [Calls Seller Service API for products/themes]

Cart Service (mystara_cart DB)
└── carts (read/write)

Payment Service (mystara_payment DB)
└── payments (read/write)
```

---

## Benefits of Separate Databases

1. **Data Isolation**: Each service owns its data
2. **Scalability**: Can scale databases independently
3. **Security**: Better access control per service
4. **Maintenance**: Easier to backup and maintain
5. **Performance**: Optimized indexes per service

---

## Migration Notes

### Current State:
- Buyer service directly accesses seller database for products/themes
- This works but creates tight coupling

### Future Improvements:
1. Refactor buyer service to use RestTemplate/Feign to call seller API
2. Implement API Gateway for service communication
3. Add caching layer for frequently accessed products/themes
4. Consider event-driven architecture for data synchronization

---

## Collection Summary

| Service | Database | Collections |
|---------|----------|-------------|
| Seller | mystara_seller | products, themes, purchase_requests |
| Buyer | mystara_seller (shared) | products (read), themes (read), purchase_requests (read/write) |
| Cart | mystara_cart | carts |
| Payment | mystara_payment | payments |

---

## Connection Strings

All services connect to the same MongoDB Atlas cluster but use different databases:

```
mongodb+srv://mystara:mystara@cluster0.4hocdmk.mongodb.net/{database_name}?appName=Cluster0
```

Replace `{database_name}` with:
- `mystara_seller` for Seller Service and Buyer Service (shared)
- `mystara_cart` for Cart Service
- `mystara_payment` for Payment Service

