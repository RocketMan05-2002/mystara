import { generateColorPalette } from '../utils/colorPalette';
import { Sparkles, MountainSnow } from 'lucide-react';
import './ThemeCard.css';

const ThemeCard = ({ theme, onClick }) => {
  const colors = generateColorPalette(theme.name);

  return (
    <div
      className="theme-card"
      onClick={onClick}
      style={{
  background: `linear-gradient(145deg, ${colors.primary} 0%, ${colors.dark} 100%)`,
}}
    >
      <div className="theme-icon">
        <MountainSnow size={32} />
      </div>
      <h3 className="theme-name">{theme.name}</h3>
      {theme.description && (
        <p className="theme-description">{theme.description}</p>
      )}
      <div className="theme-footer">
        <span className="theme-products">View Products →</span>
      </div>
    </div>
  );
};

export default ThemeCard;

