import axios from 'axios';

const API_BASE_URL = 'http://localhost:8089';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request interceptor to add JWT token and user headers
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  const userId = localStorage.getItem('userId');
  const role = localStorage.getItem('role');

  if (token) {
    config.headers['Authorization'] = `Bearer ${token}`;
  }

  if (role === 'BUYER' && userId) {
    if (config.url?.includes('/api/buyer/') || config.url?.includes('/api/cart') || config.url?.includes('/api/payments')) {
      config.headers['X-Buyer-Id'] = userId;
    }
  } else if (role === 'SELLER' && userId) {
    if (config.url?.includes('/api/') && !config.url?.includes('/api/buyer/')) {
      config.headers['X-Seller-Id'] = userId;
    }
  }
  return config;
});

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Handle 401 Unauthorized - redirect to login
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      localStorage.removeItem('role');
      localStorage.removeItem('userId');
      window.location.href = '/';
      return Promise.reject(error);
    }
    
    // Handle network errors
    if (!error.response) {
      error.message = 'Network error. Please check your connection and try again.';
      return Promise.reject(error);
    }
    
    // Handle 500 Internal Server Error
    if (error.response?.status === 500) {
      error.message = 'Server error. Please try again later.';
      return Promise.reject(error);
    }
    
    // Handle 404 Not Found
    if (error.response?.status === 404) {
      error.message = error.response?.data?.message || 'Resource not found.';
      return Promise.reject(error);
    }
    
    // Handle 400 Bad Request
    if (error.response?.status === 400) {
      error.message = error.response?.data?.message || error.response?.data?.error || 'Invalid request. Please check your input.';
      return Promise.reject(error);
    }
    
    return Promise.reject(error);
  }
);

// Auth API
export const authAPI = {
  login: (isBuyer, email, password) => {
    const service = isBuyer ? 'buyer' : 'seller';
    return api.post(`/api/${service}/auth/login`, { email, password });
  },
  register: (isBuyer, data) => {
    const service = isBuyer ? 'buyer' : 'seller';
    return api.post(`/api/${service}/auth/register`, data);
  },
};

// Themes API
export const themesAPI = {
  getAll: () => api.get('/api/themes'),
  getById: (id) => api.get(`/api/themes/${id}`),
  create: (data) => api.post('/api/themes', data),
  update: (id, data) => api.put(`/api/themes/${id}`, data),
  delete: (id) => api.delete(`/api/themes/${id}`),
};

// Products API
export const productsAPI = {
  getAll: () => api.get('/api/products'),
  getByTheme: (themeId) => api.get(`/api/products?themeId=${themeId}`),
  getById: (id) => api.get(`/api/products/${id}`),
  create: (data) => api.post('/api/products', data),
  update: (id, data) => api.put(`/api/products/${id}`, data),
  delete: (id) => api.delete(`/api/products/${id}`),
  updateStock: (id, stock) => api.patch(`/api/products/${id}/stock`, stock),
};

// Purchase Requests API
export const requestsAPI = {
  // Buyer endpoints (go through /api/buyer to hit buyer service via gateway)
  create: (data) => api.post('/api/buyer/requests', data),
  getByBuyer: () => api.get('/api/buyer/requests'),
  getPending: () => api.get('/api/buyer/requests/pending'),
  getApproved: () => api.get('/api/buyer/requests/approved'),
  cancel: (id) => api.post(`/api/buyer/requests/${id}/cancel`),
  // Seller endpoints
  getBySeller: () => api.get('/api/requests'),
  approve: (id) => api.post(`/api/requests/${id}/approve`),
  reject: (id, reason) => api.post(`/api/requests/${id}/reject`, { reason }),
};

// Cart API
// export const cartAPI = {
//   getCart: () => api.get('/api/cart'),
//   addItem: (data) => api.post('/api/cart/items', data),
//   updateItem: (itemId, quantity) => api.put(`/api/cart/items/${itemId}`, { quantity }),
//   removeItem: (itemId) => api.delete(`/api/cart/items/${itemId}`),
//   clear: () => api.delete('/api/cart'),
// };

// Payment API
export const paymentAPI = {
  create: (data) => api.post('/api/payments', data),
  confirm: (paymentId, razorpayPaymentId) => api.post(`/api/payments/${paymentId}/confirm`, { razorpayPaymentId }),
  getByBuyer: () => api.get('/api/payments'),
  getById: (id) => api.get(`/api/payments/${id}`),
};
export const cartAPI = {
  // ✅ Fetch current cart
  getCart: () => api.get('/api/cart'),

  // ✅ Add approved request (from backend mapping)
  addApprovedRequest: (data) =>
    api.post('/api/cart/approved-request', data),

  // ✅ Update item quantity
  updateItem: (productId, quantity) =>
    api.put(`/api/cart/items/${productId}?quantity=${quantity}`),

  // ✅ Remove item
  removeItem: (productId) =>
    api.delete(`/api/cart/items/${productId}`),

  // ✅ Clear entire cart
  clear: () => api.delete('/api/cart'),
};

export default api;

