import React, { useState, useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  Info,
  ArrowLeft,
  Search,
  Building2,
  ChevronRight,
  ShieldCheck,
  Target,
  Sparkles,
  Download,
  FileText,
  Star,
  ChevronLeft,
  RefreshCw,
  Loader2,
} from 'lucide-react';
import { usePrediction } from '../context/PredictionContext';
import { downloadPredictionResultsCsv, downloadShortlistCsv } from '../utils/csvDownloader';
import { generatePredictionPdf } from '../utils/pdfDownloader';

const ITEMS_PER_PAGE = 24;

export default function ResultsPage() {
  const navigate = useNavigate();
  const {
    predictionParams,
    predictionResult,
    shortlist,
    toggleShortlist,
    isShortlisted,
    resultsPageState,
    setResultsPageState,
  } = usePrediction();

  // Local state initialized from context state preservation
  const [activeBranchTab, setActiveBranchTab] = useState(resultsPageState.activeBranchTab || 'ALL');
  const [searchQuery, setSearchQuery] = useState(resultsPageState.searchQuery || '');
  const [categoryFilter, setCategoryFilter] = useState(resultsPageState.categoryFilter || 'ALL');
  const [sortBy, setSortBy] = useState(resultsPageState.sortBy || 'closingRankAsc');
  const [currentPage, setCurrentPage] = useState(resultsPageState.currentPage || 1);

  // PDF Generation Loading state
  const [generatingPdf, setGeneratingPdf] = useState(false);

  // Sync state to PredictionContext whenever filters change
  useEffect(() => {
    setResultsPageState({
      activeBranchTab,
      searchQuery,
      categoryFilter,
      sortBy,
      currentPage,
    });
  }, [activeBranchTab, searchQuery, categoryFilter, sortBy, currentPage, setResultsPageState]);

  const handleSearchChange = (e) => {
    setSearchQuery(e.target.value);
    setCurrentPage(1);
  };

  const handleCategoryFilterChange = (cat) => {
    setCategoryFilter(cat);
    setCurrentPage(1);
  };

  const handleBranchTabChange = (tab) => {
    setActiveBranchTab(tab);
    setCurrentPage(1);
  };

  const handleSortChange = (e) => {
    setSortBy(e.target.value);
    setCurrentPage(1);
  };

  const handlePdfDownload = async () => {
    if (generatingPdf) return;
    setGeneratingPdf(true);
    try {
      await generatePredictionPdf({
        predictionResult,
        predictionParams,
        activeBranchTab,
      });
    } catch (err) {
      console.error('PDF generation error:', err);
      alert('Unable to generate the PDF. Please try again.');
    } finally {
      setGeneratingPdf(false);
    }
  };

  const handleCsvDownload = () => {
    try {
      downloadPredictionResultsCsv({
        predictionResult,
        predictionParams,
        activeBranchTab,
      });
    } catch (err) {
      console.error('CSV generation error:', err);
      alert('Unable to generate the CSV. Please try again.');
    }
  };

  if (!predictionResult) {
    return (
      <div className="empty-state">
        <Building2 style={{ width: '3.5rem', height: '3.5rem', color: 'var(--text-muted)', marginBottom: '1rem' }} />
        <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '0.5rem' }}>No Prediction Results Found</h2>
        <p style={{ color: 'var(--text-secondary)', marginBottom: '1.75rem' }}>
          Please enter your rank, category, gender and branch preferences on the Predictor page.
        </p>
        <Link to="/" className="btn btn-primary">
          <ArrowLeft style={{ width: '1.1rem', height: '1.1rem' }} />
          Go to Predictor
        </Link>
      </div>
    );
  }

  const { studentRank, category, gender, year, round, results } = predictionResult;
  const selectedBranchesList = predictionParams?.branches || [];

  // Flatten prediction records returned by backend
  let allFlattenedColleges = [];
  if (results && Array.isArray(results)) {
    results.forEach((branchGroup) => {
      if (branchGroup.colleges && Array.isArray(branchGroup.colleges)) {
        branchGroup.colleges.forEach((c) => {
          allFlattenedColleges.push({
            ...c,
            branchCode: branchGroup.branchCode || c.branchCode,
          });
        });
      }
    });
  }

  const uniqueCollegeCodes = new Set(allFlattenedColleges.map((c) => c.collegeCode));
  const totalUniqueColleges = uniqueCollegeCodes.size;

  // Filter colleges by active branch tab
  let branchFilteredColleges = [];
  if (activeBranchTab === 'ALL') {
    branchFilteredColleges = allFlattenedColleges;
  } else {
    branchFilteredColleges = allFlattenedColleges.filter(
      (c) => c.branchCode.toUpperCase() === activeBranchTab.toUpperCase()
    );
  }

  // Filter by search query and categorization status
  let processedColleges = branchFilteredColleges.filter((c) => {
    if (categoryFilter !== 'ALL' && c.categorization !== categoryFilter) {
      return false;
    }
    if (searchQuery.trim() !== '') {
      const q = searchQuery.toLowerCase().trim();
      const nameMatch = c.collegeName.toLowerCase().includes(q);
      const codeMatch = c.collegeCode.toLowerCase().includes(q);
      const distMatch = c.district && c.district.toLowerCase().includes(q);
      return nameMatch || codeMatch || distMatch;
    }
    return true;
  });

  // Apply sorting
  processedColleges.sort((a, b) => {
    if (sortBy === 'closingRankAsc') {
      return a.closingRank - b.closingRank;
    } else if (sortBy === 'closingRankDesc') {
      return b.closingRank - a.closingRank;
    } else if (sortBy === 'nameAsc') {
      return a.collegeName.localeCompare(b.collegeName);
    } else if (sortBy === 'nameDesc') {
      return b.collegeName.localeCompare(a.collegeName);
    }
    return a.closingRank - b.closingRank;
  });

  // Pagination math
  const totalPages = Math.ceil(processedColleges.length / ITEMS_PER_PAGE) || 1;
  const validCurrentPage = Math.min(Math.max(currentPage, 1), totalPages);
  const startIndex = (validCurrentPage - 1) * ITEMS_PER_PAGE;
  const paginatedColleges = processedColleges.slice(startIndex, startIndex + ITEMS_PER_PAGE);

  const getBadgeClass = (cat) => {
    if (cat === 'SAFE') return 'badge-safe';
    if (cat === 'TARGET') return 'badge-target';
    if (cat === 'DREAM') return 'badge-dream';
    return '';
  };

  const getBadgeIcon = (cat) => {
    if (cat === 'SAFE') return <ShieldCheck style={{ width: '0.85rem', height: '0.85rem' }} />;
    if (cat === 'TARGET') return <Target style={{ width: '0.85rem', height: '0.85rem' }} />;
    if (cat === 'DREAM') return <Sparkles style={{ width: '0.85rem', height: '0.85rem' }} />;
    return null;
  };

  const hasResultsToDownload = allFlattenedColleges.length > 0;

  return (
    <div>
      {/* Navigation & Action Top Bar */}
      <div style={{ marginBottom: '1.5rem', display: 'flex', alignItems: 'center', justifyContent: 'space-between', flexWrap: 'wrap', gap: '1rem' }}>
        <button className="btn btn-secondary btn-sm" onClick={() => navigate('/')}>
          <ArrowLeft style={{ width: '1rem', height: '1rem' }} />
          <span>← Back to Predictor</span>
        </button>

        {/* Download Buttons Section */}
        {hasResultsToDownload && (
          <div style={{ display: 'flex', gap: '0.65rem', flexWrap: 'wrap' }}>
            {/* Download CSV Button */}
            <button
              className="btn btn-primary btn-sm"
              onClick={handleCsvDownload}
              title="Download results as CSV spreadsheet file"
            >
              <Download style={{ width: '1rem', height: '1rem' }} />
              <span>Download CSV</span>
            </button>

            {/* Download PDF Button */}
            <button
              className="btn btn-secondary btn-sm"
              onClick={handlePdfDownload}
              disabled={generatingPdf}
              title="Download results as PDF report document"
            >
              {generatingPdf ? (
                <>
                  <Loader2 style={{ width: '1rem', height: '1rem', animation: 'spin 1s linear infinite' }} />
                  <span>Generating PDF...</span>
                </>
              ) : (
                <>
                  <FileText style={{ width: '1rem', height: '1rem', color: 'var(--primary)' }} />
                  <span>Download PDF</span>
                </>
              )}
            </button>

            {/* Shortlist CSV Export Button */}
            {shortlist.length > 0 && (
              <button
                className="btn btn-outline btn-sm"
                onClick={() => downloadShortlistCsv(shortlist, predictionResult, predictionParams)}
                title="Download shortlisted colleges as CSV"
              >
                <Star style={{ width: '1rem', height: '1rem', fill: '#f59e0b', color: '#f59e0b' }} />
                <span>Shortlist CSV ({shortlist.length})</span>
              </button>
            )}
          </div>
        )}
      </div>

      {/* Main Results Header & Input Summary */}
      <div style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ fontSize: '2.25rem', fontWeight: 800, marginBottom: '0.4rem', letterSpacing: '-0.02em' }}>
          Your College Predictions
        </h1>
        <p style={{ color: 'var(--text-secondary)' }}>
          Colleges where historical AP EAPCET closing ranks were eligible for your profile.
        </p>
      </div>

      {/* Summary Box displaying selected inputs */}
      <div className="summary-bar">
        <div className="summary-items">
          <div className="summary-item">
            <span className="summary-label">Rank</span>
            <span className="summary-value" style={{ color: 'var(--primary)' }}>
              {studentRank?.toLocaleString()}
            </span>
          </div>
          <div className="summary-item">
            <span className="summary-label">Category</span>
            <span className="summary-value">{category}</span>
          </div>
          <div className="summary-item">
            <span className="summary-label">Gender</span>
            <span className="summary-value">{gender}</span>
          </div>
          <div className="summary-item">
            <span className="summary-label">Branches</span>
            <span className="summary-value" style={{ color: '#a5b4fc', fontSize: '1.05rem' }}>
              {selectedBranchesList.join(', ')}
            </span>
          </div>
          <div className="summary-item">
            <span className="summary-label">Colleges Found</span>
            <span className="summary-value" style={{ color: 'var(--safe-color)' }}>
              {totalUniqueColleges} ({allFlattenedColleges.length} branch cutoffs)
            </span>
          </div>
        </div>
      </div>

      {/* Disclaimer Banner */}
      <div className="disclaimer-banner">
        <Info className="disclaimer-icon" style={{ width: '1.35rem', height: '1.35rem' }} />
        <div>
          <strong>Historical Cutoff Data:</strong> Predictions are based on official AP EAPCET {year} ({round}) closing ranks. Historical cutoff information is intended for guidance only and is <u>not</u> a guarantee of admission.
        </div>
      </div>

      {/* Branch Tabs Navigation */}
      <div style={{ marginBottom: '1.75rem', borderBottom: '1px solid var(--border-color)', paddingBottom: '0.85rem' }}>
        <div style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--text-muted)', textTransform: 'uppercase', marginBottom: '0.65rem', letterSpacing: '0.04em' }}>
          Grouped by Branch:
        </div>
        <div className="tabs" style={{ overflowX: 'auto', flexWrap: 'nowrap', paddingBottom: '0.2rem' }}>
          <button
            className={`tab-btn ${activeBranchTab === 'ALL' ? 'active' : ''}`}
            onClick={() => handleBranchTabChange('ALL')}
          >
            All Branches ({allFlattenedColleges.length})
          </button>
          {selectedBranchesList.map((branchCode) => {
            const count = allFlattenedColleges.filter(
              (c) => c.branchCode.toUpperCase() === branchCode.toUpperCase()
            ).length;
            return (
              <button
                key={branchCode}
                className={`tab-btn ${activeBranchTab.toUpperCase() === branchCode.toUpperCase() ? 'active' : ''}`}
                onClick={() => handleBranchTabChange(branchCode)}
              >
                {branchCode} ({count})
              </button>
            );
          })}
        </div>
      </div>

      {/* Filters Toolbar */}
      <div className="filter-bar">
        <div className="tabs">
          {['ALL', 'SAFE', 'TARGET', 'DREAM'].map((tab) => (
            <button
              key={tab}
              className={`tab-btn ${categoryFilter === tab ? 'active' : ''}`}
              onClick={() => handleCategoryFilterChange(tab)}
            >
              {tab === 'ALL' ? 'All Options' : tab}
            </button>
          ))}
        </div>

        <div style={{ display: 'flex', gap: '0.85rem', flexWrap: 'wrap', flex: 1, justifyContent: 'flex-end' }}>
          <div style={{ position: 'relative', minWidth: '240px', flex: 1, maxWidth: '340px' }}>
            <Search style={{ position: 'absolute', left: '0.85rem', top: '50%', transform: 'translateY(-50%)', width: '1rem', height: '1rem', color: 'var(--text-muted)' }} />
            <input
              type="text"
              className="form-input"
              style={{ paddingLeft: '2.3rem', fontSize: '0.9rem', height: '44px' }}
              placeholder="Search college name or code..."
              value={searchQuery}
              onChange={handleSearchChange}
            />
          </div>

          <select
            className="form-select"
            style={{ width: 'auto', fontSize: '0.9rem', height: '44px', padding: '0.4rem 1rem' }}
            value={sortBy}
            onChange={handleSortChange}
          >
            <option value="closingRankAsc">Closing Rank: Low to High</option>
            <option value="closingRankDesc">Closing Rank: High to Low</option>
            <option value="nameAsc">College Name: A to Z</option>
            <option value="nameDesc">College Name: Z to A</option>
          </select>
        </div>
      </div>

      {/* Results Header Counter */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1.25rem', flexWrap: 'wrap', gap: '0.75rem' }}>
        <h2 style={{ fontSize: '1.4rem', fontWeight: 800 }}>
          {activeBranchTab === 'ALL' ? 'All College Predictions' : `${activeBranchTab} Branch Predictions`}
        </h2>

        <div style={{ fontSize: '0.9rem', color: 'var(--text-muted)', fontWeight: 600 }}>
          Showing {processedColleges.length > 0 ? startIndex + 1 : 0} -{' '}
          {Math.min(startIndex + ITEMS_PER_PAGE, processedColleges.length)} of {processedColleges.length} results
        </div>
      </div>

      {/* College Results Cards Grid */}
      {paginatedColleges.length === 0 ? (
        <div style={{ background: 'var(--bg-card)', border: '1px dashed var(--border-color)', padding: '4rem 1.5rem', borderRadius: '1.1rem', textAlign: 'center', margin: '2rem 0' }}>
          <Building2 style={{ width: '3.5rem', height: '3.5rem', color: 'var(--text-muted)', marginBottom: '1rem' }} />
          <h3 style={{ fontSize: '1.35rem', fontWeight: 700, marginBottom: '0.65rem' }}>
            No matching colleges were found for these selections.
          </h3>
          <p style={{ color: 'var(--text-secondary)', fontSize: '0.95rem', maxWidth: '520px', margin: '0 auto 1.5rem', lineHeight: 1.6 }}>
            Suggestions:
          </p>
          <ul style={{ listStyle: 'none', color: 'var(--text-muted)', fontSize: '0.9rem', display: 'flex', flexDirection: 'column', gap: '0.4rem', marginBottom: '1.75rem' }}>
            <li>• Try selecting additional preferred branches on the Predictor page.</li>
            <li>• Check if another category or gender option applies to your profile.</li>
            <li>• Try adjusting your rank entry or clearing search filters.</li>
          </ul>
          <Link to="/" className="btn btn-secondary">
            <RefreshCw style={{ width: '1rem', height: '1rem' }} />
            <span>Modify Prediction Inputs</span>
          </Link>
        </div>
      ) : (
        <div className="result-grid">
          {paginatedColleges.map((college) => {
            const isItemShortlisted = isShortlisted(college.collegeCode, college.branchCode);

            return (
              <div key={`${college.collegeId}-${college.branchId}-${college.branchCode}`} className="college-card">
                <div>
                  <div className="college-card-header">
                    <div>
                      <div className="college-name">{college.collegeName}</div>
                      <div className="college-code">College Code: {college.collegeCode}</div>
                    </div>

                    {college.categorization && (
                      <span className={`badge ${getBadgeClass(college.categorization)}`}>
                        {getBadgeIcon(college.categorization)}
                        <span>{college.categorization}</span>
                      </span>
                    )}
                  </div>

                  <div style={{ display: 'flex', gap: '0.45rem', flexWrap: 'wrap', margin: '0.75rem 0' }}>
                    <span className="badge" style={{ background: 'var(--primary-light)', color: '#a5b4fc', fontSize: '0.82rem' }}>
                      Branch: {college.branchCode}
                    </span>
                    {college.district && (
                      <span className="tag" style={{ fontSize: '0.8rem', padding: '0.2rem 0.6rem' }}>
                        {college.district}
                      </span>
                    )}
                    {college.type && (
                      <span className="tag" style={{ fontSize: '0.8rem', padding: '0.2rem 0.6rem' }}>
                        {college.type}
                      </span>
                    )}
                  </div>

                  <div className="rank-details">
                    <div className="rank-box">
                      <span className="rank-label">Entered Rank</span>
                      <span className="rank-number" style={{ color: 'var(--primary)' }}>
                        {studentRank?.toLocaleString()}
                      </span>
                    </div>

                    <div style={{ color: 'var(--border-color)', fontWeight: 300, fontSize: '1.6rem' }}>/</div>

                    <div className="rank-box" style={{ textAlign: 'right' }}>
                      <span className="rank-label">Closing Rank</span>
                      <span className="rank-number" style={{ color: 'var(--safe-color)' }}>
                        {college.closingRank?.toLocaleString()}
                      </span>
                    </div>
                  </div>
                </div>

                <div>
                  <div className="meta-footer">
                    <span>Year: {year} ({round})</span>
                    <span>Category: {category} ({gender})</span>
                  </div>

                  <div style={{ display: 'flex', gap: '0.65rem', marginTop: '1.1rem' }}>
                    <Link
                      to={`/colleges/${college.collegeCode}`}
                      className="btn btn-secondary btn-sm"
                      style={{ flex: 1, justifyContent: 'center' }}
                    >
                      <span>View College</span>
                      <ChevronRight style={{ width: '1rem', height: '1rem' }} />
                    </Link>

                    <button
                      type="button"
                      className={`btn btn-sm ${isItemShortlisted ? 'btn-primary' : 'btn-outline'}`}
                      onClick={() => toggleShortlist(college)}
                      style={{
                        padding: '0.45rem 0.8rem',
                        borderColor: isItemShortlisted ? 'var(--primary)' : 'var(--border-color)',
                        color: isItemShortlisted ? '#ffffff' : 'var(--text-secondary)',
                      }}
                      title={isItemShortlisted ? 'Remove from shortlist' : 'Add to shortlist'}
                    >
                      <Star
                        style={{
                          width: '1rem',
                          height: '1rem',
                          fill: isItemShortlisted ? '#f59e0b' : 'none',
                          color: isItemShortlisted ? '#f59e0b' : 'currentColor',
                        }}
                      />
                    </button>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Pagination Bar */}
      {totalPages > 1 && (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: '0.5rem', marginTop: '2.5rem', flexWrap: 'wrap' }}>
          <button
            className="btn btn-secondary btn-sm"
            onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
            disabled={validCurrentPage === 1}
          >
            <ChevronLeft style={{ width: '1rem', height: '1rem' }} />
            <span>Previous</span>
          </button>

          {Array.from({ length: totalPages }, (_, i) => i + 1).map((pageNum) => (
            <button
              key={pageNum}
              className={`btn btn-sm ${pageNum === validCurrentPage ? 'btn-primary' : 'btn-secondary'}`}
              onClick={() => setCurrentPage(pageNum)}
              style={{ minWidth: '40px' }}
            >
              {pageNum}
            </button>
          ))}

          <button
            className="btn btn-secondary btn-sm"
            onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
            disabled={validCurrentPage === totalPages}
          >
            <span>Next</span>
            <ChevronRight style={{ width: '1rem', height: '1rem' }} />
          </button>
        </div>
      )}
    </div>
  );
}
