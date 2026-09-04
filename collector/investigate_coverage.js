const fs = require('fs');
const path = require('path');

const collectorDir = __dirname;
const inputCollegesFile = path.join(collectorDir, 'colleges_input.csv');
const inputVerifiedFile = path.join(collectorDir, 'verified_profiles_for_import.csv');
const inputReviewFile = path.join(collectorDir, 'profiles_needing_review.csv');

function parseCSVContent(text) {
  const records = [];
  let currentRecord = [];
  let currentField = '';
  let inQuotes = false;

  for (let i = 0; i < text.length; i++) {
    const char = text[i];
    const nextChar = text[i + 1];

    if (char === '"') {
      if (inQuotes && nextChar === '"') {
        currentField += '"';
        i++;
      } else {
        inQuotes = !inQuotes;
      }
    } else if (char === ',' && !inQuotes) {
      currentRecord.push(currentField);
      currentField = '';
    } else if ((char === '\r' || char === '\n') && !inQuotes) {
      if (char === '\r' && nextChar === '\n') {
        i++;
      }
      currentRecord.push(currentField);
      if (currentRecord.some(f => f.trim().length > 0)) {
        records.push(currentRecord);
      }
      currentRecord = [];
      currentField = '';
    } else {
      currentField += char;
    }
  }

  if (currentField.length > 0 || currentRecord.length > 0) {
    currentRecord.push(currentField);
    if (currentRecord.some(f => f.trim().length > 0)) {
      records.push(currentRecord);
    }
  }

  return records;
}

function runCoverageInvestigation() {
  console.log('=== PHASE 7.1 — COVERAGE INVESTIGATION ===');

  // 1. All 274 colleges
  const rawColleges = fs.readFileSync(inputCollegesFile, 'utf-8');
  const collegeRecords = parseCSVContent(rawColleges);
  const collegeMap = new Map();

  for (let i = 1; i < collegeRecords.length; i++) {
    const r = collegeRecords[i];
    const code = r[0] ? r[0].trim() : '';
    const name = r[1] ? r[1].replace(/\n/g, ' ').replace(/\s+/g, ' ').trim() : '';
    if (code && !collegeMap.has(code)) {
      collegeMap.set(code, name);
    }
  }

  console.log(`Total Unique Database Colleges: ${collegeMap.size}`);

  // 2. Read verified import CSV
  const rawVerified = fs.readFileSync(inputVerifiedFile, 'utf-8');
  const verifiedRecords = parseCSVContent(rawVerified);
  const verifiedMap = new Map();

  for (let i = 1; i < verifiedRecords.length; i++) {
    const r = verifiedRecords[i];
    const code = r[0] ? r[0].trim() : '';
    if (!code) continue;

    verifiedMap.set(code, {
      officialWebsite: r[1] ? r[1].trim() : '',
      nirfRank: r[2] ? r[2].trim() : '',
      nirfRankBand: r[3] ? r[3].trim() : '',
      placementYear: r[6] ? r[6].trim() : ''
    });
  }

  // 3. Read review CSV
  const rawReview = fs.readFileSync(inputReviewFile, 'utf-8');
  const reviewRecords = parseCSVContent(rawReview);
  const reviewMap = new Map();

  for (let i = 1; i < reviewRecords.length; i++) {
    const r = reviewRecords[i];
    const code = r[0] ? r[0].trim() : '';
    const reason = r[5] ? r[5].trim() : '';
    if (code && !reviewMap.has(code)) {
      reviewMap.set(code, reason);
    }
  }

  const verifiedList = [];
  const reviewList = [];
  const notFoundList = [];

  collegeMap.forEach((name, code) => {
    if (verifiedMap.has(code)) {
      const v = verifiedMap.get(code);
      const webStatus = v.officialWebsite ? 'VERIFIED' : 'NOT_FOUND';
      const nirfStatus = (v.nirfRank || v.nirfRankBand) ? 'VERIFIED' : 'NOT_FOUND';
      const placementStatus = v.placementYear ? 'VERIFIED' : 'NOT_FOUND';

      verifiedList.push({
        code,
        name,
        webStatus,
        nirfStatus,
        placementStatus,
        overallStatus: 'VERIFIED'
      });
    } else if (reviewMap.has(code)) {
      reviewList.push({
        code,
        name,
        webStatus: 'NEEDS_REVIEW',
        nirfStatus: 'NOT_FOUND',
        placementStatus: 'NOT_FOUND',
        overallStatus: 'NEEDS_REVIEW',
        reason: reviewMap.get(code)
      });
    } else {
      notFoundList.push({
        code,
        name,
        webStatus: 'NOT_FOUND',
        nirfStatus: 'NOT_FOUND',
        placementStatus: 'NOT_FOUND',
        overallStatus: 'NOT_FOUND'
      });
    }
  });

  console.log('\n--- COVERAGE BREAKDOWN ---');
  console.log(`Total Colleges analyzed               : ${collegeMap.size}`);
  console.log(`VERIFIED Colleges                     : ${verifiedList.length}`);
  console.log(`NEEDS_REVIEW Colleges                 : ${reviewList.length}`);
  console.log(`NOT_FOUND Colleges                    : ${notFoundList.length}`);
  console.log(`Remaining Colleges to Investigate     : ${notFoundList.length + reviewList.length}`);

  console.log('\n=== VERIFIED COLLEGES LIST ===');
  verifiedList.forEach((c) => {
    console.log(`${c.code} | ${c.name} | Web: ${c.webStatus} | NIRF: ${c.nirfStatus} | Placement: ${c.placementStatus}`);
  });

  console.log('\n=== NEEDS_REVIEW COLLEGES LIST ===');
  reviewList.forEach((c) => {
    console.log(`${c.code} | ${c.name} | Reason: ${c.reason}`);
  });

  console.log('\n=== SAMPLE NOT_FOUND COLLEGES (First 15) ===');
  notFoundList.slice(0, 15).forEach((c) => {
    console.log(`${c.code} | ${c.name}`);
  });
}

runCoverageInvestigation();
