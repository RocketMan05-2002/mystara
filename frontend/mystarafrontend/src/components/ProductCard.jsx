import { useState } from "react";
import { ShoppingCart } from "lucide-react";
import "./ProductCard.css";

const ProductCard = ({ product, onRequest, requesting }) => {
  const [quantity, setQuantity] = useState(1);
  const [showRequestForm, setShowRequestForm] = useState(false);

  const handleRequestClick = () => {
    if (product.status === "ACTIVE" && product.stock > 0) {
      setShowRequestForm(true);
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (quantity > 0 && quantity <= product.stock) {
      onRequest(product.id, quantity);
      setShowRequestForm(false);
      setQuantity(1);
    }
  };

  return (
    <div className="product-card">
      {product.images?.length > 0 && (
        <div className="product-image">
          <img src={product.images[0]} alt={product.name} />
        </div>
      )}

      <div className="product-info">
        <h3 className="product-name">{product.name}</h3>
        {product.description && (
          <p className="product-description">{product.description}</p>
        )}

        <div className="product-details">
          <span className="product-price">${product.price}</span>
          <span
            className={`product-stock ${
              product.stock > 0 ? "in-stock" : "out-of-stock"
            }`}
          >
            {product.stock > 0
              ? `${product.stock} in stock`
              : "Out of stock"}
          </span>
        </div>

        {product.status === "ACTIVE" && product.stock > 0 && (
          <button
            className="request-btn"
            onClick={handleRequestClick}
            disabled={requesting}
          >
            <ShoppingCart size={18} />
            {requesting ? "Requesting..." : "Request to Buy"}
          </button>
        )}
      </div>

      {showRequestForm && (
        <div
          className="request-modal-overlay"
          onClick={() => setShowRequestForm(false)}
        >
          <div
            className="request-modal"
            onClick={(e) => e.stopPropagation()}
          >
            <h3>Request to Buy</h3>
            <form onSubmit={handleSubmit}>
              <div className="form-group">
                <label>Quantity (Max: {product.stock})</label>
                <input
                  type="number"
                  min="1"
                  max={product.stock}
                  value={quantity}
                  onChange={(e) =>
                    setQuantity(parseInt(e.target.value) || 1)
                  }
                  required
                />
              </div>

              <div className="form-actions">
                <button
                  type="button"
                  className="cancel-btn"
                  onClick={() => setShowRequestForm(false)}
                >
                  Cancel
                </button>
                <button type="submit" className="submit-btn">
                  Send Request
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductCard;