import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Building2, Search, MapPin, ChevronRight } from 'lucide-react';
import { getAllColleges } from '../services/api';

export default function CollegesPage() {
  const [colleges, setColleges] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [searchQuery, setSearchQuery] = useState('');
  const [districtFilter, setDistrictFilter] = useState('ALL');
  const [typeFilter, setTypeFilter] = useState('ALL');

  useEffect(() => {
    async function fetchColleges() {
      setLoading(true);
      setError(null);
      try {
        const data = await getAllColleges();
        setColleges(data);
      } catch (err) {
        setError(err.message || 'Something went wrong while loading colleges. Please try again.');
      } finally {
        setLoading(false);
      }
    }
    fetchColleges();
  }, []);

  const districts = Array.from(
    new Set(colleges.map((c) => c.district).filter(Boolean))
  ).sort();
  const types = Array.from(
    new Set(colleges.map((c) => c.type).filter(Boolean))
  ).sort();

  const filteredColleges = colleges.filter((c) => {
    if (districtFilter !== 'ALL' && c.district !== districtFilter) {
      return false;
    }
    if (typeFilter !== 'ALL' && c.type !== typeFilter) {
      return false;
    }
    if (searchQuery.trim() !== '') {
      const q = searchQuery.toLowerCase().trim();
      return (
        c.collegeName.toLowerCase().includes(q) ||
        c.collegeCode.toLowerCase().includes(q)
      );
    }
    return true;
  });

  if (loading) {
    return (
      <div className="loading-state">
        <div className="spinner"></div>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
          Loading colleges directory...
        </p>
      </div>
    );
  }

  return (
    <div>
      <div style={{ marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '2.4rem', fontWeight: 800, marginBottom: '0.5rem', letterSpacing: '-0.02em' }}>
          Explore AP EAPCET Colleges
        </h1>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
          Browse all {colleges.length} participating engineering colleges across Andhra Pradesh.
        </p>
      </div>

      <div className="filter-bar">
        <div style={{ position: 'relative', flex: 1, minWidth: '260px' }}>
          <Search style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', width: '1rem', height: '1rem', color: 'var(--text-muted)' }} />
          <input
            type="text"
            className="form-input"
            style={{ paddingLeft: '2.4rem', height: '44px' }}
            placeholder="Search by college name or code..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
        </div>

        <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
          <select
            className="form-select"
            style={{ width: 'auto', height: '44px' }}
            value={districtFilter}
            onChange={(e) => setDistrictFilter(e.target.value)}
          >
            <option value="ALL">All Districts ({districts.length})</option>
            {districts.map((d) => (
              <option key={d} value={d}>
                {d}
              </option>
            ))}
          </select>

          <select
            className="form-select"
            style={{ width: 'auto', height: '44px' }}
            value={typeFilter}
            onChange={(e) => setTypeFilter(e.target.value)}
          >
            <option value="ALL">All College Types</option>
            {types.map((t) => (
              <option key={t} value={t}>
                {t}
              </option>
            ))}
          </select>
        </div>
      </div>

      {error ? (
        <div style={{ color: '#fca5a5', padding: '1.25rem', background: 'rgba(239, 68, 68, 0.15)', borderRadius: '0.75rem', border: '1px solid rgba(239, 68, 68, 0.3)' }}>
          {error}
        </div>
      ) : (
        <div className="result-grid">
          {filteredColleges.map((college) => (
            <div key={college.id} className="college-card">
              <div>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '0.5rem', gap: '0.5rem' }}>
                  <span className="badge" style={{ background: 'var(--primary-light)', color: '#a5b4fc' }}>
                    {college.collegeCode}
                  </span>
                  {college.type && <span className="tag" style={{ fontSize: '0.78rem' }}>{college.type}</span>}
                </div>

                <h3 style={{ fontSize: '1.1rem', fontWeight: 800, lineHeight: 1.35, marginBottom: '0.5rem', color: 'var(--text-primary)' }}>
                  {college.collegeName}
                </h3>

                <div style={{ display: 'flex', alignItems: 'center', gap: '0.4rem', fontSize: '0.88rem', color: 'var(--text-secondary)' }}>
                  <MapPin style={{ width: '0.95rem', height: '0.95rem', color: 'var(--text-muted)' }} />
                  <span>District: {college.district || 'N/A'} ({college.region || 'N/A'})</span>
                </div>
              </div>

              <div style={{ marginTop: '1.25rem' }}>
                <Link
                  to={`/colleges/${college.collegeCode}`}
                  className="btn btn-secondary btn-sm"
                  style={{ width: '100%', justifyContent: 'center' }}
                >
                  <span>View Details & Cutoffs</span>
                  <ChevronRight style={{ width: '1rem', height: '1rem' }} />
                </Link>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
