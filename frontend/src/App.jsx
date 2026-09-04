import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { PredictionProvider } from './context/PredictionContext';
import Navbar from './components/Navbar';
import Footer from './components/Footer';
import PredictorPage from './pages/PredictorPage';
import ResultsPage from './pages/ResultsPage';
import CollegeDetailsPage from './pages/CollegeDetailsPage';
import CollegesPage from './pages/CollegesPage';

export default function App() {
  return (
    <PredictionProvider>
      <Router>
        <div className="app-container">
          <Navbar />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<PredictorPage />} />
              <Route path="/results" element={<ResultsPage />} />
              <Route path="/colleges" element={<CollegesPage />} />
              <Route path="/colleges/:collegeCode" element={<CollegeDetailsPage />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
    </PredictionProvider>
  );
}
