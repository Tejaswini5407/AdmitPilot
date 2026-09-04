import React, { createContext, useContext, useState, useEffect } from 'react';

const PredictionContext = createContext();

export function PredictionProvider({ children }) {
  const [predictionParams, setPredictionParams] = useState(() => {
    try {
      const saved = sessionStorage.getItem('admitpilot_prediction_params');
      return saved
        ? JSON.parse(saved)
        : {
            rank: 20000,
            category: 'EWS',
            gender: 'BOYS',
            branches: ['CSE', 'AIM', 'ECE', 'CSC'],
          };
    } catch (e) {
      return {
        rank: 20000,
        category: 'EWS',
        gender: 'BOYS',
        branches: ['CSE', 'AIM', 'ECE', 'CSC'],
      };
    }
  });

  const [predictionResult, setPredictionResult] = useState(() => {
    try {
      const saved = sessionStorage.getItem('admitpilot_prediction_result');
      return saved ? JSON.parse(saved) : null;
    } catch (e) {
      return null;
    }
  });

  // Shortlisted colleges state
  const [shortlist, setShortlist] = useState(() => {
    try {
      const saved = localStorage.getItem('admitpilot_shortlist');
      return saved ? JSON.parse(saved) : [];
    } catch (e) {
      return [];
    }
  });

  // Results Page state preservation (tabs, search, sort, filter, pagination)
  const [resultsPageState, setResultsPageState] = useState({
    activeBranchTab: 'ALL',
    searchQuery: '',
    categoryFilter: 'ALL',
    sortBy: 'closingRankAsc',
    currentPage: 1,
  });

  useEffect(() => {
    if (predictionParams) {
      try {
        sessionStorage.setItem('admitpilot_prediction_params', JSON.stringify(predictionParams));
      } catch (e) {
        console.error('Failed to save prediction params to sessionStorage', e);
      }
    }
  }, [predictionParams]);

  useEffect(() => {
    if (predictionResult) {
      try {
        sessionStorage.setItem('admitpilot_prediction_result', JSON.stringify(predictionResult));
      } catch (e) {
        console.error('Failed to save prediction result to sessionStorage', e);
      }
    }
  }, [predictionResult]);

  useEffect(() => {
    try {
      localStorage.setItem('admitpilot_shortlist', JSON.stringify(shortlist));
    } catch (e) {
      console.error('Failed to save shortlist to localStorage', e);
    }
  }, [shortlist]);

  const toggleShortlist = (collegeItem) => {
    const itemKey = `${collegeItem.collegeCode}-${collegeItem.branchCode}`;
    setShortlist((prev) => {
      const exists = prev.some((item) => `${item.collegeCode}-${item.branchCode}` === itemKey);
      if (exists) {
        return prev.filter((item) => `${item.collegeCode}-${item.branchCode}` !== itemKey);
      } else {
        return [...prev, collegeItem];
      }
    });
  };

  const isShortlisted = (collegeCode, branchCode) => {
    const itemKey = `${collegeCode}-${branchCode}`;
    return shortlist.some((item) => `${item.collegeCode}-${item.branchCode}` === itemKey);
  };

  const clearShortlist = () => {
    setShortlist([]);
  };

  return (
    <PredictionContext.Provider
      value={{
        predictionParams,
        setPredictionParams,
        predictionResult,
        setPredictionResult,
        shortlist,
        toggleShortlist,
        isShortlisted,
        clearShortlist,
        resultsPageState,
        setResultsPageState,
      }}
    >
      {children}
    </PredictionContext.Provider>
  );
}

export function usePrediction() {
  const context = useContext(PredictionContext);
  if (!context) {
    throw new Error('usePrediction must be used within a PredictionProvider');
  }
  return context;
}
