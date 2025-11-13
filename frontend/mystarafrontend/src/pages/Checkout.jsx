import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { cartAPI, paymentAPI } from '../utils/api';
import { toast } from 'react-toastify';
import { ArrowLeft } from 'lucide-react';
import Navigation from '../components/Navigation';
import './Checkout.css';

const Checkout = () => {
  const navigate = useNavigate();
  const [cart, setCart] = useState(null);
  const [loading, setLoading] = useState(true);
  const [processing, setProcessing] = useState(false);

  useEffect(() => {
    fetchCart();
  }, []);

  const fetchCart = async () => {
    try {
      setLoading(true);
      const response = await cartAPI.getCart();
      if (!response.data || !response.data.items?.length) {
        toast.info('Your cart is empty');
        navigate('/cart');
      } else {
        setCart(response.data);
      }
    } catch (error) {
      console.error('Error fetching cart:', error);
      const errorMessage =
        error.response?.data?.message ||
        error.message ||
        'Failed to fetch cart. Please try again.';
      toast.error(errorMessage);
      navigate('/cart');
    } finally {
      setLoading(false);
    }
  };

  const calculateTotal = () => {
    if (!cart || !cart.items) return 0;
    return cart.items.reduce(
      (total, item) => total + item.price * item.quantity,
      0
    );
  };

  const loadRazorpay = () => {
    return new Promise((resolve) => {
      const script = document.createElement('script');
      script.src = 'https://checkout.razorpay.com/v1/checkout.js';
      script.onload = () => resolve(true);
      script.onerror = () => resolve(false);
      document.body.appendChild(script);
    });
  };

  const handlePayment = async () => {
    setProcessing(true);
    try {
      const totalAmount = calculateTotal();
      if (totalAmount <= 0) {
        toast.info('Your cart is empty');
        setProcessing(false);
        return;
      }

      const items = cart.items.map((item) => ({
        productId: item.productId,
        productName: item.productName,
        quantity: item.quantity,
        price: item.price,
      }));

      const buyerId = localStorage.getItem('userId');
      if (!buyerId) {
        toast.error('Please login to continue');
        navigate('/');
        return;
      }

      const paymentResponse = await paymentAPI.create({
        cartId: cart.id,
        buyerId,
        items,
        amount: totalAmount,
        currency: 'INR',
      });

      const payment = paymentResponse.data;
      if (!payment || payment.status === 'FAILED') {
        throw new Error(payment?.errorMessage || 'Failed to create payment order');
      }

      const res = await loadRazorpay();
      if (!res) {
        toast.error('Razorpay SDK failed to load.');
        setProcessing(false);
        return;
      }

      const razorpayKey = import.meta.env.VITE_RAZORPAY_KEY_ID;
      if (!razorpayKey) {
        toast.error('Razorpay configuration missing.');
        setProcessing(false);
        return;
      }

      const options = {
        key: razorpayKey,
        amount: Math.round(payment.amount * 100),
        currency: 'INR',
        name: 'Mystara Payments',
        description: 'Order Payment',
        order_id: payment.razorpayOrderId,
        handler: async function (response) {
          try {
            const confirmResponse = await paymentAPI.confirm(
              payment.id,
              response.razorpay_payment_id
            );
            const confirmed = confirmResponse.data;

            if (confirmed.status === 'SUCCESS') {
              toast.success('Payment successful!');
              await cartAPI.clear();
              navigate('/payment-success', { state: { paymentId: confirmed.id } });
            } else {
              toast.error(confirmed.errorMessage || 'Payment failed on server');
            }
          } catch (err) {
            console.error('Error confirming payment:', err);
            toast.error(
              err.response?.data?.error ||
                err.response?.data?.message ||
                err.message ||
                'Payment verification failed'
            );
          }
        },
        prefill: {
          name: 'Test User',
          email: 'testuser@example.com',
          contact: '9999999999',
        },
        notes: { cart_id: cart.id },
        theme: { color: '#14B8A6' },
        modal: {
          ondismiss: function () {
            toast.info('Payment cancelled');
            setProcessing(false);
          },
        },
      };

      const paymentObject = new window.Razorpay(options);
      paymentObject.on('payment.failed', function (response) {
        toast.error('Payment failed: ' + (response.error.description || 'Unknown error'));
        setProcessing(false);
      });
      paymentObject.open();
    } catch (error) {
      console.error('Payment error:', error);
      toast.error(
        error.response?.data?.error ||
          error.response?.data?.message ||
          error.message ||
          'Payment failed. Please try again.'
      );
    } finally {
      setProcessing(false);
    }
  };

  if (loading) {
    return (
      <>
        <Navigation />
        <div className="checkout-container">
          <div className="loading">Loading checkout...</div>
        </div>
      </>
    );
  }

  return (
    <>
      {/* <Navigation /> */}
      <div className="checkout-container">
        <button onClick={() => navigate('/cart')} className="back-btn">
          <ArrowLeft size={20} />
          Back to Cart
        </button>

        <div className="checkout-content">
          <div className="checkout-form-section">
            <h1>Checkout</h1>
            <button
              className={`pay-btn ${processing ? 'disabled' : ''}`}
              onClick={handlePayment}
              disabled={processing}
            >
              {processing ? 'Processing...' : `Pay ₹${calculateTotal().toFixed(2)}`}
            </button>
          </div>

          <div className="order-summary-section">
            <div className="summary-card">
              <h2>Order Summary</h2>
              <div className="order-items">
                {cart.items.map((item) => (
                  <div key={item.id || item.productId} className="order-item">
                    <div className="order-item-info">
                      <h4>{item.productName}</h4>
                      <p>Qty: {item.quantity}</p>
                    </div>
                    <p className="order-item-price">
                      ₹{(item.price * item.quantity).toFixed(2)}
                    </p>
                  </div>
                ))}
              </div>
              <div className="summary-totals">
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
              </div>
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default Checkout;