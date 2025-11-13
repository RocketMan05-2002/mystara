import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { themesAPI } from '../utils/api';
import ThemeCard from '../components/ThemeCard';
import Navigation from '../components/Navigation';
import { Search } from 'lucide-react';
import { motion } from 'framer-motion';
import './Home.css';

const Home = () => {
  const [themes, setThemes] = useState([]);
  const [searchTerm, setSearchTerm] = useState('');
  const [loading, setLoading] = useState(true);
  const navigate = useNavigate();

  useEffect(() => {
    fetchThemes();
  }, []);

  const fetchThemes = async () => {
    try {
      const response = await themesAPI.getAll();
      setThemes(response.data);
    } catch (error) {
      console.error('Error fetching themes:', error);
    } finally {
      setLoading(false);
    }
  };

  const role = localStorage.getItem('role');

  const filteredThemes = themes.filter(theme =>
    theme.name.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <>
      <Navigation />
      <div className="home-container">
        <motion.div
          className="hero-section glass-box"
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.8 }}
        >
          <h1 className="hero-title">Discover Unique Themes</h1>
          <p className="hero-subtitle">Explore products organized by curated themes</p>
          <div className="search-box">
            <Search size={20} className="search-icon" />
            <input
              type="text"
              placeholder="Search themes..."
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="glass-input"
            />
          </div>
        </motion.div>

        {loading ? (
          <div className="loading">Loading themes...</div>
        ) : (
          <motion.div
            className="themes-grid"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.4, duration: 0.8 }}
          >
            {filteredThemes.length > 0 ? (
              filteredThemes.map((theme) => (
                <ThemeCard
                  key={theme.id}
                  theme={theme}
                  onClick={() => navigate(`/theme/${theme.id}`)}
                />
              ))
            ) : (
              <div className="no-results">No themes found</div>
            )}
          </motion.div>
        )}
      </div>
    </>
  );
};

export default Home;