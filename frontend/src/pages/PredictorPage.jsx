import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Sparkles, Info, Search, Filter, ArrowRight, X } from 'lucide-react';
import { predictColleges } from '../services/api';
import { usePrediction } from '../context/PredictionContext';

const CATEGORIES = [
  { code: 'OC', label: 'OC (Open Category)' },
  { code: 'OC-EWS', label: 'OC-EWS (Economically Weaker Section)' },
  { code: 'BCA', label: 'BC-A (Backward Class A)' },
  { code: 'BCB', label: 'BC-B (Backward Class B)' },
  { code: 'BCC', label: 'BC-C (Backward Class C)' },
  { code: 'BCD', label: 'BC-D (Backward Class D)' },
  { code: 'BCE', label: 'BC-E (Backward Class E)' },
  { code: 'SC-I', label: 'SC-I (Scheduled Caste I)' },
  { code: 'SC-II', label: 'SC-II (Scheduled Caste II)' },
  { code: 'SC-III', label: 'SC-III (Scheduled Caste III)' },
  { code: 'ST', label: 'ST (Scheduled Tribe)' },
];

const GENDERS = [
  { code: 'GIRLS', label: 'GIRLS' },
  { code: 'BOYS', label: 'BOYS' },
];

const AVAILABLE_BRANCHES = [
  { code: 'CSE', name: 'Computer Science & Engineering' },
  { code: 'AIM', name: 'Artificial Intelligence & Machine Learning' },
  { code: 'ECE', name: 'Electronics & Communication Engineering' },
  { code: 'EEE', name: 'Electrical & Electronics Engineering' },
  { code: 'MEC', name: 'Mechanical Engineering' },
  { code: 'CIV', name: 'Civil Engineering' },
  { code: 'INF', name: 'Information Technology' },
  { code: 'CSD', name: 'Data Science' },
  { code: 'CSM', name: 'AI & Machine Learning (Specialization)' },
  { code: 'CSC', name: 'Cyber Security' },
  { code: 'AIDS', name: 'AI & Data Science' },
  { code: 'CST', name: 'Computer Science & Technology' },
  { code: 'DAT', name: 'Data Science (Specialized)' },
];

export default function PredictorPage() {
  const navigate = useNavigate();
  const { predictionParams, setPredictionParams, setPredictionResult } = usePrediction();

  // State initialized from context or sensible defaults
  const [rank, setRank] = useState(predictionParams?.rank ? String(predictionParams.rank) : '');
  const [category, setCategory] = useState(predictionParams?.category || '');
  const [gender, setGender] = useState(predictionParams?.gender || '');
  const [selectedBranches, setSelectedBranches] = useState(
    predictionParams?.branches && predictionParams.branches.length > 0
      ? predictionParams.branches
      : ['CSE', 'AIM', 'ECE']
  );

  const [branchSearch, setBranchSearch] = useState('');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const filteredBranches = AVAILABLE_BRANCHES.filter(
    (b) =>
      b.code.toLowerCase().includes(branchSearch.toLowerCase()) ||
      b.name.toLowerCase().includes(branchSearch.toLowerCase())
  );

  const toggleBranch = (code) => {
    if (selectedBranches.includes(code)) {
      setSelectedBranches(selectedBranches.filter((b) => b !== code));
    } else {
      setSelectedBranches([...selectedBranches, code]);
    }
  };

  const removeBranch = (code) => {
    setSelectedBranches(selectedBranches.filter((b) => b !== code));
  };

  const handleSelectAll = () => {
    setSelectedBranches(AVAILABLE_BRANCHES.map((b) => b.code));
  };

  const handleClearAll = () => {
    setSelectedBranches([]);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);

    if (!rank || rank.trim() === '') {
      setError('Please enter your AP EAPCET rank.');
      return;
    }

    const parsedRank = parseInt(rank, 10);
    if (isNaN(parsedRank) || parsedRank <= 0) {
      setError('Please enter your AP EAPCET rank.');
      return;
    }

    if (!category || category === '') {
      setError('Please select a category.');
      return;
    }

    if (!gender || gender === '') {
      setError('Please select your gender.');
      return;
    }

    if (selectedBranches.length === 0) {
      setError('Please select at least one preferred branch.');
      return;
    }

    const payload = {
      rank: parsedRank,
      category,
      gender,
      branches: selectedBranches,
    };

    setLoading(true);
    try {
      const response = await predictColleges(payload);
      setPredictionParams(payload);
      setPredictionResult(response);
      navigate('/results');
    } catch (err) {
      setError(err.message || 'Something went wrong while loading the results. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      {/* Hero Section */}
      <div className="hero">
        <div className="hero-badge">
          <Sparkles style={{ width: '1rem', height: '1rem' }} />
          <span>AP EAPCET College Predictor</span>
        </div>
        <h1 className="hero-title">Find Colleges You Can Get</h1>
        <p className="hero-subtitle">
          Enter your AP EAPCET rank, category, gender and preferred branches to explore colleges based on historical closing ranks.
        </p>
      </div>

      {/* Disclaimer Banner */}
      <div className="disclaimer-banner">
        <Info className="disclaimer-icon" style={{ width: '1.35rem', height: '1.35rem' }} />
        <div>
          <strong>Based on historical cutoff data:</strong> AP EAPCET historical closing rank information is provided for guidance only and is <u>not</u> a guarantee of admission.
        </div>
      </div>

      {/* Main Predictor Form Card */}
      <div className="predictor-card">
        <h2 style={{ fontSize: '1.4rem', fontWeight: 800, marginBottom: '1.85rem', display: 'flex', alignItems: 'center', gap: '0.65rem' }}>
          <Filter style={{ width: '1.35rem', height: '1.35rem', color: 'var(--primary)' }} />
          <span>Enter Prediction Information</span>
        </h2>

        {error && (
          <div style={{ background: 'rgba(239, 68, 68, 0.15)', border: '1px solid rgba(239, 68, 68, 0.4)', color: '#fca5a5', padding: '1rem 1.25rem', borderRadius: '0.75rem', marginBottom: '1.85rem', fontSize: '0.95rem', fontWeight: 600 }}>
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit}>
          <div className="form-grid">
            {/* Rank Input */}
            <div className="form-group">
              <label className="form-label">
                <span>AP EAPCET Rank *</span>
              </label>
              <input
                type="number"
                className="form-input"
                placeholder="Enter your rank"
                value={rank}
                onChange={(e) => setRank(e.target.value)}
                min="1"
              />
              <span className="form-helper">Use your AP EAPCET rank</span>
            </div>

            {/* Category Selector */}
            <div className="form-group">
              <label className="form-label">
                <span>Category *</span>
              </label>
              <select
                className="form-select"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
              >
                <option value="">Select Category</option>
                {CATEGORIES.map((cat) => (
                  <option key={cat.code} value={cat.code}>
                    {cat.label}
                  </option>
                ))}
              </select>
            </div>

            {/* Gender Selector */}
            <div className="form-group">
              <label className="form-label">
                <span>Gender *</span>
              </label>
              <select
                className="form-select"
                value={gender}
                onChange={(e) => setGender(e.target.value)}
              >
                <option value="">Select Gender</option>
                {GENDERS.map((g) => (
                  <option key={g.code} value={g.code}>
                    {g.label}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {/* Multiple Branch Selector Section */}
          <div className="form-group" style={{ marginBottom: '2.25rem' }}>
            <div className="form-label" style={{ marginBottom: '0.75rem' }}>
              <span style={{ fontSize: '1rem', fontWeight: 800 }}>Preferred Branches *</span>
              <span className="branch-chip-badge">
                {selectedBranches.length} {selectedBranches.length === 1 ? 'branch' : 'branches'} selected
              </span>
            </div>

            {/* Interactive Selected Branch Chips */}
            {selectedBranches.length > 0 && (
              <div className="branch-chips-container">
                {selectedBranches.map((code) => (
                  <span key={code} className="branch-chip">
                    <span>{code}</span>
                    <button
                      type="button"
                      className="chip-remove"
                      onClick={() => removeBranch(code)}
                      title={`Remove ${code}`}
                    >
                      <X style={{ width: '0.85rem', height: '0.85rem' }} />
                    </button>
                  </span>
                ))}
              </div>
            )}

            <div className="branch-selector-box">
              <div className="branch-header">
                <div style={{ position: 'relative', flex: 1, minWidth: '220px' }}>
                  <Search style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', width: '1rem', height: '1rem', color: 'var(--text-muted)' }} />
                  <input
                    type="text"
                    className="branch-search-input"
                    placeholder="Search branch code or name..."
                    value={branchSearch}
                    onChange={(e) => setBranchSearch(e.target.value)}
                  />
                </div>

                <div className="branch-actions">
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={handleSelectAll}
                  >
                    Select All
                  </button>
                  <button
                    type="button"
                    className="btn btn-secondary btn-sm"
                    onClick={handleClearAll}
                  >
                    Clear Selection
                  </button>
                </div>
              </div>

              <div className="branch-grid">
                {filteredBranches.map((branch) => {
                  const isSelected = selectedBranches.includes(branch.code);
                  return (
                    <div
                      key={branch.code}
                      className={`branch-checkbox-card ${isSelected ? 'selected' : ''}`}
                      onClick={() => toggleBranch(branch.code)}
                    >
                      <input
                        type="checkbox"
                        checked={isSelected}
                        onChange={() => {}} // Handled by container onClick
                        onClick={(e) => e.stopPropagation()}
                      />
                      <div className="branch-info">
                        <span className="branch-code-text">{branch.code}</span>
                        <span className="branch-name-text">{branch.name}</span>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>

          {/* Submit Action Button */}
          <div>
            <button
              type="submit"
              className="btn btn-primary"
              disabled={loading}
              style={{ width: '100%', height: '54px', fontSize: '1.15rem' }}
            >
              {loading ? (
                <span>Finding colleges based on historical cutoffs...</span>
              ) : (
                <>
                  <span>Predict Colleges</span>
                  <ArrowRight style={{ width: '1.3rem', height: '1.3rem' }} />
                </>
              )}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
