import React, { useEffect, useState } from 'react';
import { useParams, Link, useNavigate } from 'react-router-dom';
import {
  ArrowLeft,
  Building2,
  MapPin,
  Award,
  TrendingUp,
  Globe,
  ExternalLink,
  Filter,
  ChevronDown,
  ChevronUp,
  CheckCircle,
  AlertCircle,
  FileText,
  BookOpen,
  Info,
} from 'lucide-react';
import { getCollegeByCode } from '../services/api';
import { usePrediction } from '../context/PredictionContext';

export default function CollegeDetailsPage() {
  const { collegeCode } = useParams();
  const navigate = useNavigate();
  const { predictionParams } = usePrediction();

  const [college, setCollege] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [showExploration, setShowExploration] = useState(false);
  const [selectedBranchFilter, setSelectedBranchFilter] = useState('ALL');
  const [selectedCategoryFilter, setSelectedCategoryFilter] = useState('ALL');

  useEffect(() => {
    async function fetchCollege() {
      setLoading(true);
      setError(null);
      try {
        const data = await getCollegeByCode(collegeCode);
        setCollege(data);
      } catch (err) {
        setError(err.message || 'Something went wrong while loading the results. Please try again.');
      } finally {
        setLoading(false);
      }
    }
    fetchCollege();
  }, [collegeCode]);

  if (loading) {
    return (
      <div className="loading-state">
        <div className="spinner"></div>
        <p style={{ color: 'var(--text-secondary)', fontSize: '1.1rem' }}>
          Loading college details...
        </p>
      </div>
    );
  }

  if (error || !college) {
    return (
      <div className="empty-state">
        <Building2 style={{ width: '3.5rem', height: '3.5rem', color: '#fca5a5', marginBottom: '1rem' }} />
        <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '0.5rem' }}>College Details Unavailable</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.75rem' }}>
          {error || 'Something went wrong while loading the results. Please try again.'}
        </p>
        <button className="btn btn-secondary" onClick={() => navigate('/results')}>
          <ArrowLeft style={{ width: '1.1rem', height: '1.1rem' }} />
          <span>← Back to Results</span>
        </button>
      </div>
    );
  }

  const { rank, category, gender, branches } = predictionParams || {};
  const requestedBranches = branches || [];
  const profile = college.profile;
  const placements = profile?.placements || [];

  // Extract all cutoff records across branches
  let allCutoffs = [];
  if (college.branches) {
    college.branches.forEach((b) => {
      if (b.cutoffs && b.cutoffs.length > 0) {
        b.cutoffs.forEach((c) => {
          allCutoffs.push({
            ...c,
            branchCode: b.branchCode,
            branchName: b.branchName,
          });
        });
      }
    });
  }

  // Filter cutoffs matching user selection
  const selectedCutoffs = allCutoffs.filter((c) => {
    const catMatch = !category || c.category.toUpperCase() === category.toUpperCase();
    const genMatch = !gender || c.gender.toUpperCase() === gender.toUpperCase();
    const branchMatch =
      requestedBranches.length === 0 ||
      requestedBranches.some((b) => b.toUpperCase() === c.branchCode.toUpperCase());
    return catMatch && genMatch && branchMatch;
  });

  const offeredBranchCodes = college.branches ? college.branches.map((b) => b.branchCode.toUpperCase()) : [];
  const missingBranches = requestedBranches.filter((b) => !offeredBranchCodes.includes(b.toUpperCase()));

  // Cutoffs filter for optional exploration section
  const explorationCutoffs = allCutoffs.filter((c) => {
    if (selectedBranchFilter !== 'ALL' && c.branchCode.toUpperCase() !== selectedBranchFilter.toUpperCase()) {
      return false;
    }
    if (selectedCategoryFilter !== 'ALL' && c.category.toUpperCase() !== selectedCategoryFilter.toUpperCase()) {
      return false;
    }
    return true;
  });

  const availableBranchCodes = Array.from(new Set(offeredBranchCodes));
  const availableCategories = Array.from(new Set(allCutoffs.map((c) => c.category)));

  return (
    <div>
      {/* Top Navigation Back Action */}
      <div style={{ marginBottom: '1.75rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
        <button className="btn btn-secondary btn-sm" onClick={() => navigate('/results')}>
          <ArrowLeft style={{ width: '1rem', height: '1rem' }} />
          <span>← Back to Results</span>
        </button>
      </div>

      {/* College Details Page Header */}
      <div className="detail-header">
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', flexWrap: 'wrap', gap: '1.25rem' }}>
          <div>
            <h1 style={{ fontSize: '2.4rem', fontWeight: 800, marginBottom: '0.4rem', letterSpacing: '-0.02em' }}>
              {college.collegeName}
            </h1>
            <div style={{ fontSize: '1.1rem', color: 'var(--text-secondary)', fontWeight: 600 }}>
              College Code: <span style={{ color: 'var(--primary)', fontWeight: 800 }}>{college.collegeCode}</span>
            </div>
          </div>

          <div>
            <span className="tag" style={{ background: 'var(--primary-light)', color: '#a5b4fc', borderColor: 'rgba(99, 102, 241, 0.4)', fontSize: '0.95rem', padding: '0.45rem 1rem' }}>
              {college.type || 'Engineering College'}
            </span>
          </div>
        </div>

        <div className="detail-tags">
          <span className="tag">District: {college.district || 'Not available'}</span>
          <span className="tag">Region: {college.region || 'Not available'}</span>
          <span className="tag">Local Area: {college.localArea || 'Not available'}</span>
          <span className="tag">Branches Offered: {college.branches ? college.branches.length : 0}</span>
        </div>
      </div>

      {/* 6 DISTINCT SECTIONS / CARDS */}

      {/* SECTION 1: College Overview */}
      <div className="section-card">
        <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <Building2 style={{ width: '1.35rem', height: '1.35rem', color: 'var(--primary)' }} />
          <span>1. College Overview</span>
        </h2>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1.25rem' }}>
          <div style={{ background: 'var(--bg-dark)', padding: '1rem 1.2rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)' }}>
            <span className="summary-label">College Name</span>
            <div style={{ fontWeight: 700, fontSize: '1rem', marginTop: '0.2rem' }}>{college.collegeName || 'Not available'}</div>
          </div>

          <div style={{ background: 'var(--bg-dark)', padding: '1rem 1.2rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)' }}>
            <span className="summary-label">College Code</span>
            <div style={{ fontWeight: 800, fontSize: '1rem', color: 'var(--primary)', marginTop: '0.2rem' }}>{college.collegeCode || 'Not available'}</div>
          </div>

          <div style={{ background: 'var(--bg-dark)', padding: '1rem 1.2rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)' }}>
            <span className="summary-label">College Type</span>
            <div style={{ fontWeight: 700, fontSize: '1rem', marginTop: '0.2rem' }}>{college.type || 'Not available'}</div>
          </div>

          <div style={{ background: 'var(--bg-dark)', padding: '1rem 1.2rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)' }}>
            <span className="summary-label">Region</span>
            <div style={{ fontWeight: 700, fontSize: '1rem', marginTop: '0.2rem' }}>{college.region || 'Not available'}</div>
          </div>

          <div style={{ background: 'var(--bg-dark)', padding: '1rem 1.2rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)' }}>
            <span className="summary-label">District</span>
            <div style={{ fontWeight: 700, fontSize: '1rem', marginTop: '0.2rem' }}>{college.district || 'Not available'}</div>
          </div>

          <div style={{ background: 'var(--bg-dark)', padding: '1rem 1.2rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)' }}>
            <span className="summary-label">Local Area</span>
            <div style={{ fontWeight: 700, fontSize: '1rem', marginTop: '0.2rem' }}>{college.localArea || 'Not available'}</div>
          </div>
        </div>
      </div>

      {/* SECTION 2: Official Website */}
      <div className="section-card">
        <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <Globe style={{ width: '1.35rem', height: '1.35rem', color: 'var(--primary)' }} />
          <span>2. Official Website</span>
        </h2>

        {profile && profile.officialWebsite ? (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem', background: 'var(--bg-dark)', padding: '1.25rem 1.5rem', borderRadius: '0.85rem', border: '1px solid var(--border-color)' }}>
            <div>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)', textTransform: 'uppercase', fontWeight: 700 }}>Verified Institution Website</div>
              <div style={{ fontSize: '1.05rem', fontWeight: 700, color: 'var(--text-primary)', marginTop: '0.25rem' }}>{profile.officialWebsite}</div>
            </div>
            <a
              href={profile.officialWebsite}
              target="_blank"
              rel="noopener noreferrer"
              className="btn btn-primary"
              style={{ textDecoration: 'none' }}
            >
              <span>Visit Official Website</span>
              <ExternalLink style={{ width: '1.1rem', height: '1.1rem' }} />
            </a>
          </div>
        ) : (
          <div style={{ background: 'var(--bg-dark)', padding: '1.25rem', borderRadius: '0.75rem', border: '1px border-color', color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            Official website not available
          </div>
        )}
      </div>

      {/* SECTION 3: NIRF Information */}
      <div className="section-card">
        <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <Award style={{ width: '1.35rem', height: '1.35rem', color: 'var(--dream-color)' }} />
          <span>3. NIRF Information</span>
        </h2>

        {profile && (profile.nirfRank || profile.nirfRankBand) ? (
          <div style={{ background: 'var(--bg-dark)', padding: '1.5rem', borderRadius: '0.85rem', border: '1px solid var(--border-color)', display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.25rem' }}>
            <div>
              <span className="summary-label">NIRF Year</span>
              <div style={{ fontSize: '1.2rem', fontWeight: 800, marginTop: '0.25rem' }}>{profile.nirfYear || 2024}</div>
            </div>
            <div>
              <span className="summary-label">Category</span>
              <div style={{ fontSize: '1.2rem', fontWeight: 800, marginTop: '0.25rem' }}>{profile.nirfCategory || 'Engineering'}</div>
            </div>
            <div>
              <span className="summary-label">Ranking / Rank Band</span>
              <div style={{ fontSize: '1.35rem', fontWeight: 800, color: 'var(--dream-color)', marginTop: '0.25rem' }}>
                {profile.nirfRank ? `Rank ${profile.nirfRank}` : `Rank Band ${profile.nirfRankBand}`}
              </div>
            </div>
          </div>
        ) : (
          <div style={{ background: 'var(--bg-dark)', padding: '1.25rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            NIRF ranking not available
          </div>
        )}
      </div>

      {/* SECTION 4: Placement Information */}
      <div className="section-card">
        <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <TrendingUp style={{ width: '1.35rem', height: '1.35rem', color: 'var(--target-color)' }} />
          <span>4. Placement Information</span>
        </h2>

        {placements.length > 0 ? (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1.25rem' }}>
            {placements.map((p) => (
              <div key={p.id || p.year} style={{ background: 'var(--bg-dark)', padding: '1.5rem', borderRadius: '0.85rem', border: '1px solid var(--border-color)' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '0.5rem' }}>
                  <div style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--target-color)' }}>
                    Placement Year: {p.year}
                  </div>
                  {p.placementRate && (
                    <span className="badge badge-target">
                      Placement Rate: {p.placementRate}%
                    </span>
                  )}
                </div>

                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem', marginTop: '0.5rem' }}>
                  <div style={{ background: 'var(--bg-card)', padding: '1rem', borderRadius: '0.65rem' }}>
                    <span className="summary-label">Placement Rate</span>
                    <div style={{ fontSize: '1.25rem', fontWeight: 800, marginTop: '0.2rem' }}>
                      {p.placementRate ? `${p.placementRate}%` : 'Not available'}
                    </div>
                  </div>

                  <div style={{ background: 'var(--bg-card)', padding: '1rem', borderRadius: '0.65rem' }}>
                    <span className="summary-label">Students Placed</span>
                    <div style={{ fontSize: '1.25rem', fontWeight: 800, marginTop: '0.2rem' }}>
                      {p.studentsPlaced ? p.studentsPlaced : 'Not available'}
                    </div>
                  </div>

                  <div style={{ background: 'var(--bg-card)', padding: '1rem', borderRadius: '0.65rem' }}>
                    <span className="summary-label">Average Package</span>
                    <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--text-primary)', marginTop: '0.2rem' }}>
                      {p.averagePackage ? `₹${p.averagePackage} LPA` : 'Not available'}
                    </div>
                  </div>

                  <div style={{ background: 'var(--bg-card)', padding: '1rem', borderRadius: '0.65rem' }}>
                    <span className="summary-label">Median Package</span>
                    <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--text-primary)', marginTop: '0.2rem' }}>
                      {p.medianPackage ? `₹${p.medianPackage} LPA` : 'Not available'}
                    </div>
                  </div>

                  <div style={{ background: 'var(--bg-card)', padding: '1rem', borderRadius: '0.65rem' }}>
                    <span className="summary-label">Highest Package</span>
                    <div style={{ fontSize: '1.25rem', fontWeight: 800, color: 'var(--safe-color)', marginTop: '0.2rem' }}>
                      {p.highestPackage ? `₹${p.highestPackage} LPA` : 'Not available'}
                    </div>
                  </div>
                </div>

                {p.sourceUrl && (
                  <div style={{ marginTop: '1rem' }}>
                    <a
                      href={p.sourceUrl}
                      target="_blank"
                      rel="noopener noreferrer"
                      style={{ display: 'inline-flex', alignItems: 'center', gap: '0.35rem', fontSize: '0.85rem', color: 'var(--primary)', textDecoration: 'none', fontWeight: 600 }}
                    >
                      <FileText style={{ width: '0.85rem', height: '0.85rem' }} />
                      <span>View Official Placement Document ↗</span>
                    </a>
                  </div>
                )}
              </div>
            ))}
          </div>
        ) : (
          <div style={{ background: 'var(--bg-dark)', padding: '1.25rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            Not available
          </div>
        )}
      </div>

      {/* SECTION 5: Branches Section */}
      <div className="section-card">
        <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <BookOpen style={{ width: '1.35rem', height: '1.35rem', color: 'var(--primary)' }} />
          <span>5. Branches Offered</span>
        </h2>

        {college.branches && college.branches.length > 0 ? (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(260px, 1fr))', gap: '1rem' }}>
            {college.branches.map((b) => (
              <div key={b.branchId || b.branchCode} style={{ background: 'var(--bg-dark)', padding: '1rem 1.2rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', display: 'flex', alignItems: 'center', gap: '0.85rem' }}>
                <span className="badge" style={{ background: 'var(--primary-light)', color: '#a5b4fc', fontSize: '0.9rem', padding: '0.4rem 0.8rem' }}>
                  {b.branchCode}
                </span>
                <div>
                  <div style={{ fontWeight: 700, fontSize: '0.95rem', color: 'var(--text-primary)' }}>{b.branchName || b.branchCode}</div>
                </div>
              </div>
            ))}
          </div>
        ) : (
          <div style={{ background: 'var(--bg-dark)', padding: '1.25rem', borderRadius: '0.75rem', border: '1px solid var(--border-color)', color: 'var(--text-muted)', fontSize: '0.95rem' }}>
            No branch information available
          </div>
        )}
      </div>

      {/* SECTION 6: Cutoff Information */}
      <div className="section-card" style={{ marginBottom: '2.5rem' }}>
        <h2 style={{ fontSize: '1.35rem', fontWeight: 800, marginBottom: '1.25rem', display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <CheckCircle style={{ width: '1.35rem', height: '1.35rem', color: 'var(--safe-color)' }} />
          <span>6. Cutoff Information ({category || 'All Categories'} - {gender || 'All Genders'})</span>
        </h2>

        {selectedCutoffs.length === 0 ? (
          <div style={{ background: 'var(--bg-dark)', border: '1px dashed var(--border-color)', padding: '2rem', borderRadius: '0.75rem', textAlign: 'center', color: 'var(--text-muted)' }}>
            No matching cutoff records for category {category} ({gender}) in the selected branches.
          </div>
        ) : (
          <div className="table-responsive">
            <table className="data-table">
              <thead>
                <tr>
                  <th>Branch</th>
                  <th>Category</th>
                  <th>Gender</th>
                  <th>Closing Rank</th>
                  <th>Year</th>
                  <th>Round</th>
                </tr>
              </thead>
              <tbody>
                {selectedCutoffs.map((c) => (
                  <tr key={c.id}>
                    <td>
                      <span className="badge" style={{ background: 'var(--primary-light)', color: '#a5b4fc' }}>
                        {c.branchCode}
                      </span>
                    </td>
                    <td style={{ fontWeight: 700 }}>{c.category}</td>
                    <td>{c.gender}</td>
                    <td style={{ fontWeight: 800, fontSize: '1.05rem', color: 'var(--safe-color)' }}>
                      {c.closingRank?.toLocaleString()}
                    </td>
                    <td>{c.year}</td>
                    <td>{c.round}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}

        {missingBranches.length > 0 && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginTop: '1.25rem', padding: '0.85rem 1rem', background: 'rgba(245, 158, 11, 0.1)', border: '1px solid rgba(245, 158, 11, 0.3)', borderRadius: '0.6rem', color: '#fcd34d', fontSize: '0.88rem' }}>
            <AlertCircle style={{ width: '1.1rem', height: '1.1rem', flexShrink: 0 }} />
            <span>Note: This college does not offer the following requested branches: <strong>{missingBranches.join(', ')}</strong>.</span>
          </div>
        )}

        {/* Optional Accordion to Explore All Cutoffs */}
        <div className="accordion-box" style={{ marginTop: '2rem' }}>
          <div className="accordion-header" onClick={() => setShowExploration(!showExploration)}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
              <Filter style={{ width: '1.2rem', height: '1.2rem', color: 'var(--primary)' }} />
              <span>Explore All Historical Cutoffs for {college.collegeCode}</span>
            </div>
            {showExploration ? <ChevronUp style={{ width: '1.2rem', height: '1.2rem' }} /> : <ChevronDown style={{ width: '1.2rem', height: '1.2rem' }} />}
          </div>

          {showExploration && (
            <div className="accordion-body">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '1rem', marginBottom: '1.5rem' }}>
                <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                  View all historical cutoff records for this college.
                </p>

                <div style={{ display: 'flex', gap: '0.75rem', flexWrap: 'wrap' }}>
                  <select
                    className="form-select"
                    style={{ width: 'auto', fontSize: '0.88rem', height: '40px' }}
                    value={selectedBranchFilter}
                    onChange={(e) => setSelectedBranchFilter(e.target.value)}
                  >
                    <option value="ALL">All Branches ({availableBranchCodes.length})</option>
                    {availableBranchCodes.map((code) => (
                      <option key={code} value={code}>
                        Branch: {code}
                      </option>
                    ))}
                  </select>

                  <select
                    className="form-select"
                    style={{ width: 'auto', fontSize: '0.88rem', height: '40px' }}
                    value={selectedCategoryFilter}
                    onChange={(e) => setSelectedCategoryFilter(e.target.value)}
                  >
                    <option value="ALL">All Categories</option>
                    {availableCategories.map((cat) => (
                      <option key={cat} value={cat}>
                        Category: {cat}
                      </option>
                    ))}
                  </select>
                </div>
              </div>

              {explorationCutoffs.length === 0 ? (
                <div style={{ textAlign: 'center', padding: '2rem 1rem', color: 'var(--text-muted)' }}>
                  No cutoff records match the selected exploration filters.
                </div>
              ) : (
                <div className="table-responsive">
                  <table className="data-table">
                    <thead>
                      <tr>
                        <th>Branch</th>
                        <th>Category</th>
                        <th>Gender</th>
                        <th>Closing Rank</th>
                        <th>Year</th>
                        <th>Round</th>
                      </tr>
                    </thead>
                    <tbody>
                      {explorationCutoffs.map((c) => (
                        <tr key={c.id}>
                          <td>
                            <span className="badge" style={{ background: 'var(--primary-light)', color: '#a5b4fc' }}>
                              {c.branchCode}
                            </span>
                          </td>
                          <td style={{ fontWeight: 600 }}>{c.category}</td>
                          <td>{c.gender}</td>
                          <td style={{ fontWeight: 800, color: 'var(--text-primary)' }}>
                            {c.closingRank?.toLocaleString()}
                          </td>
                          <td>{c.year}</td>
                          <td>{c.round}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
