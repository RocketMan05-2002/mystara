import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../utils/api';
import { User, Store, LogIn, UserPlus, Earth } from 'lucide-react';
import { toast } from 'react-toastify';
import { motion, AnimatePresence } from 'framer-motion';
import './LandingPage.css';

const LandingPage = () => {
  const [isBuyer, setIsBuyer] = useState(true);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const [showRegisterModal, setShowRegisterModal] = useState(false);
  const [loginData, setLoginData] = useState({ email: '', password: '' });
  const [registerData, setRegisterData] = useState({
    name: '',
    email: '',
    password: '',
    phone: '',
    address: '',
    businessName: ''
  });
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  // ---------- LOGIN HANDLER ----------
  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await authAPI.login(isBuyer, loginData.email, loginData.password);
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.buyer || response.data.seller));
      localStorage.setItem('role', isBuyer ? 'BUYER' : 'SELLER');
      localStorage.setItem('userId', response.data.buyer?.id || response.data.seller?.id);

      setShowLoginModal(false);
      setLoginData({ email: '', password: '' });
      toast.success('Logged in successfully');
      navigate(isBuyer ? '/home' : '/seller/dashboard');
    } catch (error) {
      toast.error(error.response?.data?.error || 'Login failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // ---------- REGISTER HANDLER ----------
  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const response = await authAPI.register(isBuyer, registerData);
      localStorage.setItem('token', response.data.token);
      localStorage.setItem('user', JSON.stringify(response.data.buyer || response.data.seller));
      localStorage.setItem('role', isBuyer ? 'BUYER' : 'SELLER');
      localStorage.setItem('userId', response.data.buyer?.id || response.data.seller?.id);

      setShowRegisterModal(false);
      setRegisterData({
        name: '',
        email: '',
        password: '',
        phone: '',
        address: '',
        businessName: ''
      });
      toast.success('Registration successful');
      navigate(isBuyer ? '/home' : '/seller/dashboard');
    } catch (error) {
      toast.error(error.response?.data?.error || 'Registration failed. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  // ---------- MAIN RETURN ----------
  return (
    <div className="landing-page">
      {/* HERO SECTION */}
      <motion.section
        className="landing-hero"
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        transition={{ duration: 1 }}
      >
        <motion.div
          className="hero-glass-card"
          initial={{ y: -40, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          transition={{ duration: 1 }}
        >
          <div className="logo">
            <Earth size={50} color="#a88bff" />
            <h1>Mystara</h1>
          </div>
          <p className="tagline">Discover Unique Products by Theme</p>
          <p className="description">
            A marketplace where sellers showcase products organized by unique themes, 
            and buyers explore curated collections tailored to their interests.
          </p>

          <div className="role-selector">
            <motion.button
              whileTap={{ scale: 0.95 }}
              className={`role-btn ${isBuyer ? 'active' : ''}`}
              onClick={() => setIsBuyer(true)}
              type="button"
            >
              <User size={20} />
              I'm a Buyer
            </motion.button>
            <motion.button
              whileTap={{ scale: 0.95 }}
              className={`role-btn ${!isBuyer ? 'active' : ''}`}
              onClick={() => setIsBuyer(false)}
              type="button"
            >
              <Store size={20} />
              I'm a Seller
            </motion.button>
          </div>

          <motion.div
            className="auth-buttons"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.6 }}
          >
            <motion.button
              whileHover={{ scale: 1.05 }}
              className="btn btn-primary"
              onClick={() => {
                setShowLoginModal(true);
                setShowRegisterModal(false);
              }}
              type="button"
            >
              <LogIn size={18} />
              Login
            </motion.button>

            <motion.button
              whileHover={{ scale: 1.05 }}
              className="btn btn-secondary"
              onClick={() => {
                setShowRegisterModal(true);
                setShowLoginModal(false);
              }}
              type="button"
            >
              <UserPlus size={18} />
              Register
            </motion.button>
          </motion.div>
        </motion.div>
      </motion.section>

      {/* LOGIN MODAL */}
      <AnimatePresence>
        {showLoginModal && (
          <motion.div
            key="login-modal"
            className="modal-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setShowLoginModal(false)}
          >
            <motion.div
              className="modal-content glass"
              onClick={(e) => e.stopPropagation()}
              initial={{ y: -100, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: 50, opacity: 0 }}
              transition={{ type: 'spring', stiffness: 200 }}
            >
              <h2>Login as {isBuyer ? 'Buyer' : 'Seller'}</h2>
              <form onSubmit={handleLogin} autoComplete="on">
                <div className="form-group">
                  <label>Email</label>
                  <input
                    type="email"
                    value={loginData.email ?? ''}
                    onChange={(e) =>
                      setLoginData((prev) => ({ ...prev, email: e.target.value }))
                    }
                    required
                    placeholder="you@domain.com"
                    autoComplete="email"
                  />
                </div>

                <div className="form-group">
                  <label>Password</label>
                  <input
                    type="password"
                    value={loginData.password ?? ''}
                    onChange={(e) =>
                      setLoginData((prev) => ({ ...prev, password: e.target.value }))
                    }
                    required
                    placeholder="••••••••"
                    autoComplete="current-password"
                  />
                </div>

                <motion.button
                  whileHover={{ scale: 1.03 }}
                  className="btn btn-primary full"
                  type="submit"
                  disabled={loading}
                >
                  {loading ? 'Logging in...' : 'Login'}
                </motion.button>
              </form>
              <button className="modal-close" onClick={() => setShowLoginModal(false)} type="button">
                ×
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      {/* REGISTER MODAL */}
      <AnimatePresence>
        {showRegisterModal && (
          <motion.div
            key="register-modal"
            className="modal-overlay"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setShowRegisterModal(false)}
          >
            <motion.div
              className="modal-content glass"
              onClick={(e) => e.stopPropagation()}
              initial={{ y: -100, opacity: 0 }}
              animate={{ y: 0, opacity: 1 }}
              exit={{ y: 50, opacity: 0 }}
              transition={{ type: 'spring', stiffness: 200 }}
            >
              <h2>Register as {isBuyer ? 'Buyer' : 'Seller'}</h2>
              <form onSubmit={handleRegister} autoComplete="on">
                <div className="form-group">
                  <label>Name</label>
                  <input
                    type="text"
                    value={registerData.name ?? ''}
                    onChange={(e) =>
                      setRegisterData((prev) => ({ ...prev, name: e.target.value }))
                    }
                    required
                    placeholder="Full name"
                    autoComplete="name"
                  />
                </div>

                <div className="form-group">
                  <label>Email</label>
                  <input
                    type="email"
                    value={registerData.email ?? ''}
                    onChange={(e) =>
                      setRegisterData((prev) => ({ ...prev, email: e.target.value }))
                    }
                    required
                    placeholder="you@domain.com"
                    autoComplete="email"
                  />
                </div>

                <div className="form-group">
                  <label>Password</label>
                  <input
                    type="password"
                    value={registerData.password ?? ''}
                    onChange={(e) =>
                      setRegisterData((prev) => ({ ...prev, password: e.target.value }))
                    }
                    required
                    placeholder="Choose a secure password"
                    autoComplete="new-password"
                  />
                </div>

                <div className="form-group">
                  <label>Phone</label>
                  <input
                    type="tel"
                    value={registerData.phone ?? ''}
                    onChange={(e) =>
                      setRegisterData((prev) => ({ ...prev, phone: e.target.value }))
                    }
                    placeholder="Optional"
                    autoComplete="tel"
                  />
                </div>

                <div className="form-group">
                  <label>Address</label>
                  <input
                    type="text"
                    value={registerData.address ?? ''}
                    onChange={(e) =>
                      setRegisterData((prev) => ({ ...prev, address: e.target.value }))
                    }
                    placeholder="Optional"
                    autoComplete="street-address"
                  />
                </div>

                {!isBuyer && (
                  <div className="form-group">
                    <label>Business Name</label>
                    <input
                      type="text"
                      value={registerData.businessName ?? ''}
                      onChange={(e) =>
                        setRegisterData((prev) => ({ ...prev, businessName: e.target.value }))
                      }
                      placeholder="Optional"
                      autoComplete="organization"
                    />
                  </div>
                )}

                <motion.button
                  whileHover={{ scale: 1.03 }}
                  className="btn btn-primary full"
                  type="submit"
                  disabled={loading}
                >
                  {loading ? 'Registering...' : 'Register'}
                </motion.button>
              </form>
              <button className="modal-close" onClick={() => setShowRegisterModal(false)} type="button">
                ×
              </button>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default LandingPage;