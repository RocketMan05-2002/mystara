import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { productsAPI, themesAPI } from '../utils/api';
import Navigation from '../components/Navigation';
import { Plus, Edit, Trash2, Package, X } from 'lucide-react';
import { toast } from 'react-toastify';
import './SellerProducts.css';

const SellerProducts = () => {
  const navigate = useNavigate();
  const [products, setProducts] = useState([]);
  const [themes, setThemes] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showProductModal, setShowProductModal] = useState(false);
  const [showThemeModal, setShowThemeModal] = useState(false);
  const [editingProduct, setEditingProduct] = useState(null);

  const [productForm, setProductForm] = useState({
    name: '',
    description: '',
    price: '',
    stock: '',
    themeId: '',
    images: '',
    status: 'ACTIVE',
  });

  const [themeForm, setThemeForm] = useState({
    name: '',
    description: '',
    category: '',
  });

  useEffect(() => {
    const role = localStorage.getItem('role');
    if (role !== 'SELLER') {
      navigate('/home');
      return;
    }
    fetchData();
  }, [navigate]);

  const fetchData = async () => {
    try {
      const [productsResponse, themesResponse] = await Promise.all([
        productsAPI.getAll(),
        themesAPI.getAll(),
      ]);
      const userId = localStorage.getItem('userId');
      const sellerProducts = productsResponse.data.filter(p => p.sellerId === userId);
      setProducts(sellerProducts);
      setThemes(themesResponse.data);
    } catch (error) {
      toast.error('Failed to load data');
    } finally {
      setLoading(false);
    }
  };

  const handleAddProduct = () => {
    setEditingProduct(null);
    setProductForm({
      name: '',
      description: '',
      price: '',
      stock: '',
      themeId: '',
      images: '',
      status: 'ACTIVE',
    });
    setShowProductModal(true);
  };

  const handleEditProduct = (product) => {
    setEditingProduct(product);
    setProductForm({
      name: product.name || '',
      description: product.description || '',
      price: product.price?.toString() || '',
      stock: product.stock?.toString() || '',
      themeId: product.themeId || '',
      images: product.images?.join(', ') || '',
      status: product.status || 'ACTIVE',
    });
    setShowProductModal(true);
  };

  const handleDeleteProduct = async (id) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    try {
      await productsAPI.delete(id);
      toast.success('Product deleted successfully');
      fetchData();
    } catch {
      toast.error('Failed to delete product');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const sellerId = localStorage.getItem('userId');
      const productData = {
        ...productForm,
        sellerId,
        price: parseFloat(productForm.price),
        stock: parseInt(productForm.stock),
        images: productForm.images
          ? productForm.images.split(',').map(url => url.trim()).filter(Boolean)
          : [],
      };

      if (editingProduct) {
        await productsAPI.update(editingProduct.id, productData);
        toast.success('Product updated successfully');
      } else {
        await productsAPI.create(productData);
        toast.success('Product created successfully');
      }

      setShowProductModal(false);
      fetchData();
    } catch {
      toast.error('Failed to save product');
    }
  };

  const handleCreateTheme = async (e) => {
    e.preventDefault();
    try {
      const response = await themesAPI.create(themeForm);
      setThemes([...themes, response.data]);
      toast.success('Theme created successfully');
      setShowThemeModal(false);
      setThemeForm({ name: '', description: '', category: '' });
    } catch {
      toast.error('Failed to create theme');
    }
  };

  if (loading) {
    return (
      <>
        <Navigation />
        <div className="seller-products-container">
          <div className="loading">Loading products...</div>
        </div>
      </>
    );
  }

  return (
    <>
      <Navigation />
      <div className="seller-products-container">
        <div className="products-header">
          <h1>Manage Products</h1>
          <button onClick={handleAddProduct} className="btn btn-primary">
            <Plus size={18} /> Add Product
          </button>
        </div>

        {products.length > 0 ? (
          <div className="products-grid">
            {products.map((product) => (
              <div key={product.id} className="product-card">
                {/* {product.images?.length > 0 && (
                  <div className="product-image">
                    <img src={product.images[0]} alt={product.name} />
                  </div>
                )} */}
                <div className="product-info2">
                  <h3>{product.name}</h3>
                  <p className="product-description">{product.description}</p>
                  <div className="product-details">
                    <span className="price">${product.price}</span>
                    <span className="stock">Stock: {product.stock}</span>
                  </div>
                  <div className="product-actions">
                    <button
                      onClick={() => handleEditProduct(product)}
                      className="btn-edit"
                    >
                      <Edit size={16} /> Edit
                    </button>
                    <button
                      onClick={() => handleDeleteProduct(product.id)}
                      className="btn-delete"
                    >
                      <Trash2 size={16} /> Delete
                    </button>
                  </div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div className="empty-state">
            <Package size={48} />
            <p>No products yet. Start by creating a product!</p>
            <button onClick={handleAddProduct} className="btn btn-primary">
              <Plus size={18} /> Add Product
            </button>
          </div>
        )}

        {/* Product Modal */}
        {showProductModal && (
          <div className="modal-overlay" onClick={() => setShowProductModal(false)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h2>{editingProduct ? 'Edit Product' : 'Add Product'}</h2>
                <button
                  className="modal-close"
                  onClick={() => setShowProductModal(false)}
                >
                  <X size={24} />
                </button>
              </div>
              <form onSubmit={handleSubmit}>
                <div className="form-group">
                  <label>Product Name *</label>
                  <input
                    type="text"
                    value={productForm.name}
                    onChange={(e) => setProductForm({ ...productForm, name: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Description</label>
                  <textarea
                    rows="3"
                    value={productForm.description}
                    onChange={(e) => setProductForm({ ...productForm, description: e.target.value })}
                  />
                </div>
                <div className="form-row">
                  <div className="form-group">
                    <label>Price *</label>
                    <input
                      type="number"
                      min="0"
                      value={productForm.price}
                      onChange={(e) => setProductForm({ ...productForm, price: e.target.value })}
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label>Stock *</label>
                    <input
                      type="number"
                      min="0"
                      value={productForm.stock}
                      onChange={(e) => setProductForm({ ...productForm, stock: e.target.value })}
                      required
                    />
                  </div>
                </div>

                <div className="form-group">
                  <label>Theme *</label>
                  <div className="theme-row">
                    <select
                      value={productForm.themeId}
                      onChange={(e) => setProductForm({ ...productForm, themeId: e.target.value })}
                      required
                    >
                      <option value="">Select Theme</option>
                      {themes.map((theme) => (
                        <option key={theme.id} value={theme.id}>
                          {theme.name}
                        </option>
                      ))}
                    </select>
                    <button
                      type="button"
                      className="btn-small"
                      onClick={() => setShowThemeModal(true)}
                    >
                      <Plus size={14} /> New Theme
                    </button>
                  </div>
                </div>

                <div className="form-group">
                  <label>Image URLs (comma separated)</label>
                  <input
                    type="text"
                    value={productForm.images}
                    onChange={(e) => setProductForm({ ...productForm, images: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label>Status</label>
                  <select
                    value={productForm.status}
                    onChange={(e) => setProductForm({ ...productForm, status: e.target.value })}
                  >
                    <option value="ACTIVE">Active</option>
                    <option value="INACTIVE">Inactive</option>
                    <option value="OUT_OF_STOCK">Out of Stock</option>
                  </select>
                </div>

                <div className="form-actions">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => setShowProductModal(false)}
                  >
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary">
                    {editingProduct ? 'Update' : 'Create'} Product
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}

        {/* Theme Modal */}
        {showThemeModal && (
          <div className="modal-overlay" onClick={() => setShowThemeModal(false)}>
            <div className="modal-content" onClick={(e) => e.stopPropagation()}>
              <div className="modal-header">
                <h2>Create Theme</h2>
                <button
                  className="modal-close"
                  onClick={() => setShowThemeModal(false)}
                >
                  <X size={24} />
                </button>
              </div>
              <form onSubmit={handleCreateTheme}>
                <div className="form-group">
                  <label>Theme Name *</label>
                  <input
                    type="text"
                    value={themeForm.name}
                    onChange={(e) => setThemeForm({ ...themeForm, name: e.target.value })}
                    required
                  />
                </div>
                <div className="form-group">
                  <label>Description</label>
                  <textarea
                    rows="3"
                    value={themeForm.description}
                    onChange={(e) => setThemeForm({ ...themeForm, description: e.target.value })}
                  />
                </div>
                <div className="form-group">
                  <label>Category</label>
                  <input
                    type="text"
                    value={themeForm.category}
                    onChange={(e) => setThemeForm({ ...themeForm, category: e.target.value })}
                  />
                </div>
                <div className="form-actions">
                  <button
                    type="button"
                    className="btn btn-secondary"
                    onClick={() => setShowThemeModal(false)}
                  >
                    Cancel
                  </button>
                  <button type="submit" className="btn btn-primary">
                    Create Theme
                  </button>
                </div>
              </form>
            </div>
          </div>
        )}
      </div>
    </>
  );
};

export default SellerProducts;