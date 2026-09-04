import React, { useState } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { Compass, GraduationCap, Building2, Info, Menu, X } from 'lucide-react';

export default function Navbar() {
  const location = useLocation();
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);
  const [aboutModalOpen, setAboutModalOpen] = useState(false);

  return (
    <header className="navbar">
      <div className="navbar-inner">
        <Link to="/" className="brand-logo" onClick={() => setMobileMenuOpen(false)}>
          <Compass className="brand-icon" />
          <span>Admit<span style={{ color: 'var(--primary)' }}>Pilot</span></span>
        </Link>

        <button
          className="mobile-menu-btn"
          onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
          aria-label="Toggle navigation menu"
        >
          {mobileMenuOpen ? <X style={{ width: '1.4rem', height: '1.4rem' }} /> : <Menu style={{ width: '1.4rem', height: '1.4rem' }} />}
        </button>

        <nav>
          <ul className={`nav-links ${mobileMenuOpen ? 'open' : ''}`}>
            <li>
              <Link
                to="/"
                className={`nav-link ${location.pathname === '/' ? 'active' : ''}`}
                onClick={() => setMobileMenuOpen(false)}
              >
                <GraduationCap style={{ width: '1.1rem', height: '1.1rem' }} />
                Predictor
              </Link>
            </li>
            <li>
              <Link
                to="/colleges"
                className={`nav-link ${location.pathname.startsWith('/colleges') ? 'active' : ''}`}
                onClick={() => setMobileMenuOpen(false)}
              >
                <Building2 style={{ width: '1.1rem', height: '1.1rem' }} />
                Colleges
              </Link>
            </li>
            <li>
              <button
                type="button"
                className="nav-link"
                onClick={() => {
                  setAboutModalOpen(true);
                  setMobileMenuOpen(false);
                }}
              >
                <Info style={{ width: '1.1rem', height: '1.1rem' }} />
                About
              </button>
            </li>
          </ul>
        </nav>
      </div>

      {/* About Modal */}
      {aboutModalOpen && (
        <div className="modal-overlay" onClick={() => setAboutModalOpen(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()}>
            <button className="modal-close" onClick={() => setAboutModalOpen(false)}>
              <X style={{ width: '1.1rem', height: '1.1rem' }} />
            </button>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem', marginBottom: '1rem' }}>
              <Compass style={{ width: '2rem', height: '2rem', color: 'var(--primary)' }} />
              <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>About AdmitPilot</h2>
            </div>
            <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', marginBottom: '1rem', lineHeight: 1.6 }}>
              <strong>AdmitPilot</strong> is an AP EAPCET (EAMCET) college prediction and exploration platform designed to help students discover engineering colleges and branches based on official historical Phase 1 closing ranks.
            </p>
            <div style={{ background: 'var(--bg-dark)', padding: '1rem 1.25rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', marginBottom: '1.25rem', fontSize: '0.9rem', color: 'var(--text-secondary)' }}>
              <ul style={{ paddingLeft: '1.2rem', display: 'flex', flexDirection: 'column', gap: '0.4rem' }}>
                <li>Contains <strong>274 colleges</strong> across Andhra Pradesh.</li>
                <li>Covers <strong>1,509 branches</strong> and <strong>28,183 historical cutoff records</strong>.</li>
                <li>Includes verified official college profiles, website links, NIRF rankings, and placement statistics.</li>
              </ul>
            </div>
            <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)', lineHeight: 1.5 }}>
              * Disclaimer: Historical cutoffs are provided for guidance purposes only. Prediction results do not guarantee admission.
            </p>
            <div style={{ marginTop: '1.5rem', textAlign: 'right' }}>
              <button className="btn btn-primary btn-sm" onClick={() => setAboutModalOpen(false)}>
                Got It
              </button>
            </div>
          </div>
        </div>
      )}
    </header>
  );
}
