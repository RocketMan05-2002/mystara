import { useEffect, useMemo, useState } from 'react';
import { requestsAPI } from '../utils/api';
import {
  Package,
  Clock,
  CheckCircle2,
  XCircle,
  MessageCircle,
  Users,
} from 'lucide-react';
import Navigation from '../components/Navigation';
import { toast } from 'react-toastify';
import './SellerRequests.css';

const statusMeta = {
  PENDING: {
    label: 'Pending',
    icon: <Clock size={18} />,
    className: 'pending',
  },
  APPROVED: {
    label: 'Approved',
    icon: <CheckCircle2 size={18} />,
    className: 'approved',
  },
  REJECTED: {
    label: 'Rejected',
    icon: <XCircle size={18} />,
    className: 'rejected',
  },
};

const SellerRequests = () => {
  const [requests, setRequests] = useState([]);
  const [allRequests, setAllRequests] = useState([]);
  const [filter, setFilter] = useState('all');
  const [loading, setLoading] = useState(true);
  const [actionLoading, setActionLoading] = useState(null);
  const [rejectModal, setRejectModal] = useState({
    open: false,
    request: null,
    reason: '',
  });

  useEffect(() => {
    fetchRequests();
  }, []);

  useEffect(() => {
    if (filter === 'all') setRequests(allRequests);
    else setRequests(allRequests.filter((r) => r.status === filter.toUpperCase()));
  }, [filter, allRequests]);

  const fetchRequests = async () => {
    try {
      setLoading(true);
      const response = await requestsAPI.getBySeller();
      setAllRequests(response.data || []);
    } catch (err) {
      toast.error(err.response?.data?.error || 'Failed to load requests');
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    setActionLoading(id);
    try {
      await requestsAPI.approve(id);
      toast.success('Request approved and added to buyer cart');
      await fetchRequests();
    } catch (err) {
      toast.error(err.response?.data?.error || 'Failed to approve request');
    } finally {
      setActionLoading(null);
    }
  };

  const openRejectModal = (request) => {
    setRejectModal({ open: true, request, reason: '' });
  };

  const closeRejectModal = () => {
    setRejectModal({ open: false, request: null, reason: '' });
  };

  const handleRejectSubmit = async (e) => {
    e.preventDefault();
    if (!rejectModal.request) return;

    setActionLoading(rejectModal.request.id);
    try {
      await requestsAPI.reject(rejectModal.request.id, rejectModal.reason.trim() || undefined);
      toast.info('Request rejected');
      closeRejectModal();
      await fetchRequests();
    } catch (err) {
      toast.error(err.response?.data?.error || 'Failed to reject request');
    } finally {
      setActionLoading(null);
    }
  };

  const pendingCount = useMemo(
    () => allRequests.filter((r) => r.status === 'PENDING').length,
    [allRequests]
  );
  const approvedCount = useMemo(
    () => allRequests.filter((r) => r.status === 'APPROVED').length,
    [allRequests]
  );
  const rejectedCount = useMemo(
    () => allRequests.filter((r) => r.status === 'REJECTED').length,
    [allRequests]
  );

  return (
    <>
      <Navigation />
      <div className="seller-requests-container">
        <header className="requests-header">
          <div>
            <h1>Buyer Requests</h1>
            <p>Review, approve, or reject incoming purchase requests.</p>
          </div>
          <div className="requests-stats">
            <div className="stat-card">
              <Clock size={20} />
              <div>
                <span>Pending</span>
                <strong>{pendingCount}</strong>
              </div>
            </div>
            <div className="stat-card">
              <CheckCircle2 size={20} />
              <div>
                <span>Approved</span>
                <strong>{approvedCount}</strong>
              </div>
            </div>
            <div className="stat-card">
              <XCircle size={20} />
              <div>
                <span>Rejected</span>
                <strong>{rejectedCount}</strong>
              </div>
            </div>
          </div>
        </header>

        <div className="filter-tabs">
          {['all', 'pending', 'approved', 'rejected'].map((f) => (
            <button
              key={f}
              className={filter === f ? 'active' : ''}
              onClick={() => setFilter(f)}
            >
              {f.charAt(0).toUpperCase() + f.slice(1)} (
              {f === 'all'
                ? allRequests.length
                : f === 'pending'
                ? pendingCount
                : f === 'approved'
                ? approvedCount
                : rejectedCount}
              )
            </button>
          ))}
        </div>

        {loading ? (
          <div className="loading">Loading requests...</div>
        ) : requests.length === 0 ? (
          <div className="empty-state">
            <Users size={56} />
            <h3>No requests in this view</h3>
            <p>When buyers show interest in your products, their requests will appear here.</p>
          </div>
        ) : (
          <div className="requests-list">
            {requests.map((req) => {
              const meta = statusMeta[req.status] || statusMeta.PENDING;
              return (
                <div key={req.id} className="request-card">
                  <div className="request-top">
                    <div className={`status-badge ${meta.className}`}>
                      {meta.icon}
                      <span>{meta.label}</span>
                    </div>
                    <div className="request-date">
                      {new Date(req.createdAt).toLocaleString()}
                    </div>
                  </div>

                  <div className="request-main">
                    <div className="request-info">
                      <h3>{req.productName}</h3>
                      <p>Theme: <strong>{req.themeId || 'N/A'}</strong></p>
                    </div>
                    <div className="request-quantity">
                      <span>Qty</span>
                      <strong>{req.quantity}</strong>
                    </div>
                    <div className="request-price">
                      <span>Total</span>
                      <strong>${(req.price * req.quantity).toFixed(2)}</strong>
                    </div>
                  </div>

                  <div className="request-footer">
                    <div className="buyer-info">
                      <Package size={18} />
                      <div>
                        <span>Buyer ID</span>
                        <strong>{req.buyerId}</strong>
                      </div>
                    </div>

                    {req.message && (
                      <div className="request-message">
                        <MessageCircle size={18} />
                        <p>{req.message}</p>
                      </div>
                    )}

                    {req.status === 'PENDING' && (
                      <div className="action-buttons">
                        <button
                          className="approve-btn"
                          onClick={() => handleApprove(req.id)}
                          disabled={actionLoading === req.id}
                        >
                          {actionLoading === req.id ? 'Approving...' : 'Approve'}
                        </button>
                        <button
                          className="reject-btn"
                          onClick={() => openRejectModal(req)}
                          disabled={actionLoading === req.id}
                        >
                          Reject
                        </button>
                      </div>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {rejectModal.open && (
        <div className="modal-overlay" onClick={closeRejectModal}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <div className="modal-header">
              <h2>Reject Request</h2>
              <button className="modal-close" onClick={closeRejectModal}>
                ✕
              </button>
            </div>
            <form onSubmit={handleRejectSubmit}>
              <div className="form-group">
                <label>Reason (optional)</label>
                <textarea
                  value={rejectModal.reason}
                  onChange={(e) =>
                    setRejectModal((prev) => ({ ...prev, reason: e.target.value }))
                  }
                  placeholder="Let the buyer know why the request was rejected..."
                  rows={4}
                />
              </div>
              <div className="form-actions">
                <button type="button" className="btn-secondary" onClick={closeRejectModal}>
                  Cancel
                </button>
                <button
                  type="submit"
                  className="btn-danger"
                  disabled={actionLoading === rejectModal.request?.id}
                >
                  {actionLoading === rejectModal.request?.id
                    ? 'Rejecting...'
                    : 'Confirm Reject'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </>
  );
};

export default SellerRequests;