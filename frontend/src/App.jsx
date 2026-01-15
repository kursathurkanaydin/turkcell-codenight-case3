import React from 'react';
import { BrowserRouter as Router, Routes, Route, NavLink } from 'react-router-dom';
import { Container } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import './App.css';

import Dashboard from './pages/Dashboard';
import Events from './pages/Events';
import Rules from './pages/Rules';
import Cases from './pages/Cases';
import Profiles from './pages/Profiles';
import Decisions from './pages/Decisions';

function App() {
  return (
    <Router>
      <div className="app-container">
        {/* Neon Navbar */}
        <nav className="neon-navbar">
          <div className="navbar-brand">
            <span className="brand-icon">&#9776;</span>
            <span className="brand-text">TRUSTSHIELD</span>
            <span className="brand-badge">TURKCELL</span>
          </div>

          <div className="navbar-links">
            <NavLink to="/" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
              <span className="nav-icon">&#9632;</span>
              Dashboard
            </NavLink>
            <NavLink to="/events" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
              <span className="nav-icon">&#9889;</span>
              Events
            </NavLink>
            <NavLink to="/rules" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
              <span className="nav-icon">&#9881;</span>
              Rules
            </NavLink>
            <NavLink to="/profiles" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
              <span className="nav-icon">&#9787;</span>
              Profiles
            </NavLink>
            <NavLink to="/cases" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
              <span className="nav-icon">&#9888;</span>
              Cases
            </NavLink>
            <NavLink to="/decisions" className={({isActive}) => isActive ? 'nav-link active' : 'nav-link'}>
              <span className="nav-icon">&#10003;</span>
              Decisions
            </NavLink>
          </div>
        </nav>

        {/* Main Content */}
        <main className="main-content">
          <Container fluid className="px-4 py-4">
            <Routes>
              <Route path="/" element={<Dashboard />} />
              <Route path="/events" element={<Events />} />
              <Route path="/rules" element={<Rules />} />
              <Route path="/cases" element={<Cases />} />
              <Route path="/profiles" element={<Profiles />} />
              <Route path="/decisions" element={<Decisions />} />
            </Routes>
          </Container>
        </main>

        {/* Footer */}
        <footer className="neon-footer">
          <div className="footer-content">
            <span className="footer-text">&copy; 2026 Turkcell TrustShield</span>
            <span className="footer-divider">|</span>
            <span className="footer-status">
              <span className="status-dot"></span>
              System Online
            </span>
          </div>
        </footer>
      </div>
    </Router>
  );
}

export default App;
