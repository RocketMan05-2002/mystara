import { useState, useEffect } from 'react';
import { requestsAPI } from '../utils/api';
import { Package, Clock, CheckCircle, XCircle } from 'lucide-react';
import Navigation from '../components/Navigation';
import { toast } from 'react-toastify';
import './BuyerRequests.css';

const BuyerRequests = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('all'); // all, pending, approved, rejected

  useEffect(() => {
    fetchRequests();
  }, [filter]);

  const fetchRequests = async () => {
    try {
      let response;
      if (filter === 'pending') {
        response = await requestsAPI.getPending();
      } else if (filter === 'approved') {
        response = await requestsAPI.getApproved();
      } else {
        response = await requestsAPI.getByBuyer();
      }
      setRequests(response.data);
    } catch (error) {
      console.error('Error fetching requests:', error);
      toast.error(error.response?.data?.error || 'Failed to load requests');
    } finally {
      setLoading(false);
    }
  };

  const handleCancel = async (id) => {
    if (!window.confirm('Are you sure you want to cancel this request?')) return;
    try {
      await requestsAPI.cancel(id);
      toast.success('Request cancelled');
      fetchRequests();
    } catch (error) {
      toast.error(error.response?.data?.error || 'Failed to cancel request');
    }
  };

  const getStatusIcon = (status) => {
    switch (status) {
      case 'PENDING':
        return <Clock size={20} className="status-icon pending" />;
      case 'APPROVED':
        return <CheckCircle size={20} className="status-icon approved" />;
      case 'REJECTED':
        return <XCircle size={20} className="status-icon rejected" />;
      default:
        return <Package size={20} className="status-icon" />;
    }
  };

  return (
    <>
      <Navigation />
      <div className="buyer-requests-container">
        <div className="requests-header">
          <h1>My Purchase Requests</h1>
          <div className="filter-tabs">
            <button
              className={filter === 'all' ? 'active' : ''}
              onClick={() => setFilter('all')}
            >
              All
            </button>
            <button
              className={filter === 'pending' ? 'active' : ''}
              onClick={() => setFilter('pending')}
            >
              Pending
            </button>
            <button
              className={filter === 'approved' ? 'active' : ''}
              onClick={() => setFilter('approved')}
            >
              Approved
            </button>
            <button
              className={filter === 'rejected' ? 'active' : ''}
              onClick={() => setFilter('rejected')}
            >
              Rejected
            </button>
          </div>
        </div>

        {loading ? (
          <div className="loading">Loading requests...</div>
        ) : requests.length > 0 ? (
          <div className="requests-list">
            {requests.map((request) => (
              <div key={request.id} className="request-card">
                <div className="request-header">
                  <div className="request-info">
                    {getStatusIcon(request.status)}
                    <div>
                      <h3>{request.productName}</h3>
                      <p className="request-date">
                        {new Date(request.createdAt).toLocaleDateString()}
                      </p>
                    </div>
                  </div>
                  <span className={`status-badge ${request.status?.toLowerCase()}`}>
                    {request.status}
                  </span>
                </div>

                <div className="request-details">
                  <div className="detail-row">
                    <span>Quantity:</span>
                    <span>{request.quantity}</span>
                  </div>
                  <div className="detail-row">
                    <span>Price:</span>
                    <span>₹{request.price}</span>
                  </div>
                  <div className="detail-row">
                    <span>Total:</span>
                    <span className="total-amount">
                      ₹{(request.price * request.quantity).toFixed(2)}
                    </span>
                  </div>
                  {request.message && (
                    <div className="request-message">
                      <strong>Message:</strong> {request.message}
                    </div>
                  )}
                </div>

                {request.status === 'PENDING' && (
                  <button
                    onClick={() => handleCancel(request.id)}
                    className="cancel-btn"
                  >
                    Cancel Request
                  </button>
                )}
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <Package size={48} className="empty-icon" />
            <p>No requests found</p>
          </div>
        )}
      </div>
    </>
  );
};

export default BuyerRequests;