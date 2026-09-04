import React from 'react';

export default function Footer() {
  return (
    <footer className="footer">
      <div style={{ maxWidth: '1280px', margin: '0 auto' }}>
        <p style={{ fontWeight: 600, color: 'var(--text-secondary)', marginBottom: '0.4rem' }}>
          AdmitPilot — AP EAPCET College Predictor & Exploration Platform
        </p>
        <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)' }}>
          Historical cutoff information is based on AP EAPCET 2025 Phase 1 official records and is intended for guidance only.
          Cutoff predictions do not guarantee admission.
        </p>
        <p style={{ fontSize: '0.75rem', marginTop: '0.8rem', color: 'var(--text-muted)' }}>
          &copy; {new Date().getFullYear()} AdmitPilot. All rights reserved.
        </p>
      </div>
    </footer>
  );
}
