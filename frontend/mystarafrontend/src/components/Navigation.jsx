import { Link, useLocation, useNavigate } from 'react-router-dom';
import { ShoppingCart, Package, User, Home, Store, LogOut } from 'lucide-react';
import './Navigation.css';

const Navigation = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const role = localStorage.getItem('role');
  const user = JSON.parse(localStorage.getItem('user') || '{}');

  const handleLogout = () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    localStorage.removeItem('role');
    localStorage.removeItem('userId');
    navigate('/');
  };

  if (location.pathname === '/') {
    return null; // Don't show navigation on landing page
  }

  return (
    <nav className="main-navigation glass-nav">
      <div className="nav-container">
        <Link to={role === 'SELLER' ? '/seller/dashboard' : '/home'} className="nav-logo">
          <span>Mystara</span>
        </Link>
        
        <div className="nav-links">
          {role === 'BUYER' ? (
            <>
              <Link to="/home" className={location.pathname === '/home' ? 'active' : ''}>
                <Home size={18} />
                <span>Home</span>
              </Link>
              <Link to="/cart" className={location.pathname === '/cart' ? 'active' : ''}>
                <ShoppingCart size={18} />
                <span>Cart</span>
              </Link>
              <Link to="/requests" className={location.pathname === '/requests' ? 'active' : ''}>
                <Package size={18} />
                <span>Requests</span>
              </Link>
            </>
          ) : (
            <>
              <Link to="/seller/dashboard" className={location.pathname === '/seller/dashboard' ? 'active' : ''}>
                <Store size={18} />
                <span>Dashboard</span>
              </Link>
              <Link to="/seller/products" className={location.pathname === '/seller/products' ? 'active' : ''}>
                <Package size={18} />
                <span>Products</span>
              </Link>
              <Link to="/seller/requests" className={location.pathname === '/seller/requests' ? 'active' : ''}>
                <Package size={18} />
                <span>Requests</span>
              </Link>
            </>
          )}
        </div>

        <div className="nav-user">
          <div className="user-info">
            <User size={18} />
            <span>{user.name || 'User'}</span>
          </div>
          <button onClick={handleLogout} className="logout-btn">
            <LogOut size={18} />
            <span className="logout-text">Logout</span>
          </button>
        </div>
      </div>
    </nav>
  );
};

export default Navigation;