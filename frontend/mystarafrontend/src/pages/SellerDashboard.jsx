import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { themesAPI, productsAPI } from "../utils/api";
import Navigation from "../components/Navigation";
import { Package, Plus, Sparkles } from "lucide-react";
import "./SellerDashboard.css";

const SellerDashboard = () => {
  const navigate = useNavigate();
  const [themes, setThemes] = useState([]);
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const role = localStorage.getItem("role");
    if (role !== "SELLER") {
      navigate("/home");
      return;
    }
    fetchData();
  }, [navigate]);

  const fetchData = async () => {
    try {
      const [themesResponse, productsResponse] = await Promise.all([
        themesAPI.getAll(),
        productsAPI.getAll(),
      ]);
      setThemes(themesResponse.data);
      // Filter products for current seller
      const userId = localStorage.getItem("userId");
      const sellerProducts = productsResponse.data.filter(
        (p) => p.sellerId === userId
      );
      setProducts(sellerProducts);
    } catch (error) {
      console.error("Error fetching data:", error);
    } finally {
      setLoading(false);
    }
  };

  const user = JSON.parse(localStorage.getItem("user") || "{}");

  return (
    <>
      <Navigation />
      <div className="seller-dashboard">
        <div className="dashboard-content">
          <div className="dashboard-hero">
            <h1>Welcome, {user.name || "Seller"}!</h1>
            <p>Manage your products and themes efficiently</p>
          </div>

          <div className="dashboard-stats">
            <div className="stat-card">
              <Package size={32} />
              <div className="stat-info">
                <h3>{products.length}</h3>
                <p>Products</p>
              </div>
            </div>
            <div className="stat-card">
              <Sparkles size={32} />
              <div className="stat-info">
                <h3>{themes.length}</h3>
                <p>Themes</p>
              </div>
            </div>
          </div>

          <div className="dashboard-sections">
            <div className="section">
              <h2>Your Products</h2>
              {loading ? (
                <div className="loading">Loading products...</div>
              ) : products.length > 0 ? (
                <>
                  <div className="products-list">
                    {products.map((product) => (
                      <div key={product.id} className="product-item">
                        <div className="product-header">
                          <h4>{product.name}</h4>
                          <span
                            className={`status ${product.status?.toLowerCase()}`}
                          >
                            {product.status}
                          </span>
                        </div>
                        <p className="product-meta">
                          ₹{product.price} • Stock: {product.stock}
                        </p>
                      </div>
                    ))}
                  </div>

                  <div className="add-product-container">
                    <button
                      onClick={() => navigate("/seller/products")}
                      className="btn btn-primary"
                    >
                      <Plus size={18} />
                      Add More Products
                    </button>
                  </div>
                </>
              ) : (
                <div className="empty-state">
                  <Package size={48} />
                  <p>No products yet. Start by creating your first product!</p>
                  <button
                    onClick={() => navigate("/seller/products")}
                    className="btn btn-primary"
                  >
                    <Plus size={18} />
                    Add Product
                  </button>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </>
  );
};

export default SellerDashboard;