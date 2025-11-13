import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { productsAPI, themesAPI, requestsAPI } from '../utils/api';
import ProductCard from '../components/ProductCard';
import Navigation from '../components/Navigation';
import { ArrowLeft } from 'lucide-react';
import { toast } from 'react-toastify';
import './ThemeProducts.css';

const ThemeProducts = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [theme, setTheme] = useState(null);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [requesting, setRequesting] = useState(null);

  useEffect(() => {
    fetchThemeAndProducts();
  }, [id]);

  const fetchThemeAndProducts = async () => {
    try {
      const [themeResponse, productsResponse] = await Promise.all([
        themesAPI.getById(id),
        productsAPI.getByTheme(id),
      ]);
      setTheme(themeResponse.data);
      setProducts(productsResponse.data);
    } catch (error) {
      console.error('Error fetching data:', error);
      toast.error(error.response?.data?.error || 'Failed to load theme details');
    } finally {
      setLoading(false);
    }
  };

  const handleRequest = async (productId, quantity) => {
    setRequesting(productId);
    try {
      await requestsAPI.create({
        productId,
        quantity,
        message: 'Purchase request',
      });
      toast.success('Purchase request sent to seller');
    } catch (error) {
      toast.error(error.response?.data?.error || 'Failed to send request');
    } finally {
      setRequesting(null);
    }
  };

  if (loading) {
    return (
      <div className="theme-products-loading">
        <div className="halo-loader" />
        <p>Loading products...</p>
      </div>
    );
  }

  if (!theme) {
    return (
      <div className="theme-products-error">
        <p>Theme not found</p>
      </div>
    );
  }

  return (
    <>
      <Navigation />
      <div className="theme-products-wrapper">
        <div className="theme-header-glass">
          <button className="back-btn-glass" onClick={() => navigate('/home')}>
            <ArrowLeft size={18} />
            <span>Back</span>
          </button>
          <div className="theme-info-section">
            <h1 className="theme-title">{theme.name}</h1>
            {theme.description && (
              <p className="theme-description">{theme.description}</p>
            )}
          </div>
        </div>

        <div className="products-glass-section">
          {products.length > 0 ? (
            <div className="products-grid-halo">
              {products.map((product) => (
                <ProductCard
                  key={product.id}
                  product={product}
                  onRequest={handleRequest}
                  requesting={requesting === product.id}
                />
              ))}
            </div>
          ) : (
            <div className="no-products-halo">
              <p>No products available in this theme.</p>
            </div>
          )}
        </div>
      </div>
    </>
  );
};

export default ThemeProducts;