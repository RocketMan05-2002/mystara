import { useLocation, useNavigate } from "react-router-dom";
import { CheckCircle } from "lucide-react";
import Navigation from "../components/Navigation";
import "./PaymentSuccess.css";

const PaymentSuccess = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const paymentId = location.state?.paymentId;

  return (
    <>
      <Navigation />
      <div className="payment-success-container">
        <div className="success-card">
          <div className="icon-wrapper">
            <CheckCircle size={70} className="success-icon" />
          </div>

          <h1 className="success-title">Payment Successful!</h1>
          <p className="success-message">
            Thank you for your purchase. Your order has been confirmed.
          </p>

          {paymentId && (
            <p className="payment-id">
              <strong>Payment ID:</strong> {paymentId}
            </p>
          )}

          <div className="success-actions">
            <button
              onClick={() => navigate("/home")}
              className="btn btn-primary"
            >
              Continue Shopping
            </button>
            <button
              onClick={() => navigate("/requests")}
              className="btn btn-secondary"
            >
              View Orders
            </button>
          </div>
        </div>
      </div>
    </>
  );
};

export default PaymentSuccess;