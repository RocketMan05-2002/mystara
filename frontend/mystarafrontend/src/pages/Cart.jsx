import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartAPI } from '../utils/api';
import { ShoppingCart, Trash2, Plus, Minus, ArrowRight } from 'lucide-react';
import Navigation from '../components/Navigation';
import { toast } from 'react-toastify';
import './Cart.css';

const Cart = () => {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [updating, setUpdating] = useState(null);

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const response = await cartAPI.getCart();
      setCart(response.data);
    } catch (error) {
      console.error('Error fetching cart:', error);
      if (error.response?.status === 404) {
        setCart(null);
      } else {
        toast.error(
          error.response?.data?.message ||
            error.message ||
            'Failed to load cart. Please try again.'
        );
      }
    } finally {
      setLoading(false);
    }
  };

  const updateQuantity = async (productId, newQuantity) => {
    if (newQuantity < 1) {
      await removeItem(productId);
      return;
    }

    setUpdating(productId);
    try {
      await cartAPI.updateItem(productId, newQuantity);
      toast.success('Quantity updated successfully');
      await fetchCart();
    } catch (error) {
      console.error('Error updating quantity:', error);
      const errorMessage =
        error.response?.data?.message ||
        error.response?.data?.error ||
        error.message ||
        'Failed to update quantity. Please try again.';
      toast.error(errorMessage);
    } finally {
      setUpdating(null);
    }
  };

  const removeItem = async (productId) => {
    setUpdating(productId);
    try {
      await cartAPI.removeItem(productId);
      toast.success('Item removed from cart');
      await fetchCart();
    } catch (error) {
      console.error('Error removing item:', error);
      const errorMessage =
        error.response?.data?.error ||
        error.response?.data?.message ||
        error.message ||
        'Failed to remove item. Please try again.';
      toast.error(errorMessage);
    } finally {
      setUpdating(null);
    }
  };

  const calculateTotal = () => {
    if (!cart || !cart.items) return 0;
    return cart.items.reduce((total, item) => total + item.price * item.quantity, 0);
  };

  const handleCheckout = () => {
    if (!cart || !cart.items || cart.items.length === 0) {
      toast.info('Your cart is empty');
      return;
    }
    navigate('/checkout');
  };

  if (loading) {
    return (
      <>
        <Navigation />
        <div className="cart-container">
          <div className="loading">Loading cart...</div>
        </div>
      </>
    );
  }

  if (!cart || !cart.items || cart.items.length === 0) {
    return (
      <>
        <Navigation />
        <div className="cart-container">
          <div className="empty-cart">
            <ShoppingCart size={64} className="empty-icon" />
            <h2>Your cart is empty</h2>
            <p>Add some products to your cart to get started.</p>
            <button onClick={() => navigate('/home')} className="btn-primary">
              Browse Products
            </button>
          </div>
        </div>
      </>
    );
  }

  return (
    <>
      <Navigation />
      <div className="cart-container">
        <div className="cart-header">
          <h1>Shopping Cart</h1>
          <p>
            {cart.items.length} {cart.items.length === 1 ? 'item' : 'items'}
          </p>
        </div>

        <div className="cart-content">
          <div className="cart-items">
            {cart.items.map((item, index) => (
              <div key={item.productId || index} className="cart-item">
                {item.images && item.images.length > 0 && (
                  <div className="item-image">
                    <img src={item.images[0]} alt={item.productName} />
                  </div>
                )}
                <div className="item-details">
                  <h3>{item.productName}</h3>
                  <p className="item-price">₹{item.price}</p>
                  <div className="item-actions">
                    <div className="quantity-controls">
                      <button
                        onClick={() =>
                          updateQuantity(item.productId, item.quantity - 1)
                        }
                        disabled={updating === item.productId || item.quantity <= 1}
                        className="qty-btn"
                      >
                        <Minus size={16} />
                      </button>
                      <span className="quantity">{item.quantity}</span>
                      <button
                        onClick={() =>
                          updateQuantity(item.productId, item.quantity + 1)
                        }
                        disabled={updating === item.productId}
                        className="qty-btn"
                      >
                        <Plus size={16} />
                      </button>
                    </div>
                    <button
                      onClick={() => removeItem(item.productId)}
                      disabled={updating === item.productId}
                      className="remove-btn"
                    >
                      <Trash2 size={18} />
                      Remove
                    </button>
                  </div>
                </div>
                <div className="item-total">
                  <p>₹{(item.price * item.quantity).toFixed(2)}</p>
                </div>
              </div>
            ))}
          </div>

          <div className="cart-summary">
            <div className="summary-card">
              <h2>Order Summary</h2>
              <div className="summary-row">
                <span>Subtotal</span>
                <span>₹{calculateTotal().toFixed(2)}</span>
              </div>
              <div className="summary-row">
                <span>Shipping</span>
                <span>Free</span>
              </div>
              <div className="summary-row total">
                <span>Total</span>
                <span>₹{calculateTotal().toFixed(2)}</span>
              </div>
              <button onClick={handleCheckout} className="checkout-btn">
                Proceed to Checkout
                <ArrowRight size={18} />
              </button>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Cart;