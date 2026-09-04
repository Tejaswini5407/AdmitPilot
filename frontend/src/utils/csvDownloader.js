/**
 * AdmitPilot CSV Downloader Utility
 * Formats prediction records into RFC 4180 compliant CSV files with student context
 * and triggers browser file download.
 */

function escapeCsvField(field) {
  if (field === null || field === undefined) return '""';
  const str = String(field);
  // If value contains comma, double quote, or newline, enclose in quotes and escape internal quotes
  if (str.includes(',') || str.includes('"') || str.includes('\n') || str.includes('\r')) {
    return `"${str.replace(/"/g, '""')}"`;
  }
  return `"${str}"`;
}

export function downloadPredictionResultsCsv({
  predictionResult,
  predictionParams,
  itemsToExport = null,
  activeBranchTab = 'ALL',
}) {
  if (!predictionResult || !predictionResult.results) {
    alert('No prediction results available to download.');
    return;
  }

  const studentRank = predictionResult.studentRank || predictionParams?.rank || '';
  const category = predictionResult.category || predictionParams?.category || '';
  const gender = predictionResult.gender || predictionParams?.gender || '';
  const selectedBranches = predictionParams?.branches ? predictionParams.branches.join('; ') : 'All';
  const year = predictionResult.year || 2025;
  const round = predictionResult.round || 'Phase 1';

  // Determine list of items to export
  let exportList = [];
  if (itemsToExport && Array.isArray(itemsToExport) && itemsToExport.length > 0) {
    exportList = itemsToExport;
  } else {
    predictionResult.results.forEach((branchGroup) => {
      if (branchGroup.colleges && Array.isArray(branchGroup.colleges)) {
        branchGroup.colleges.forEach((college) => {
          if (
            activeBranchTab === 'ALL' ||
            (branchGroup.branchCode && branchGroup.branchCode.toUpperCase() === activeBranchTab.toUpperCase()) ||
            (college.branchCode && college.branchCode.toUpperCase() === activeBranchTab.toUpperCase())
          ) {
            exportList.push({
              ...college,
              branchCode: branchGroup.branchCode || college.branchCode,
            });
          }
        });
      }
    });
  }

  if (exportList.length === 0) {
    alert('No matching prediction records found to export.');
    return;
  }

  // Build CSV metadata context block
  const metaLines = [
    [escapeCsvField('AdmitPilot - AP EAPCET College Prediction Results')],
    [escapeCsvField('AP EAPCET Rank'), escapeCsvField(studentRank)],
    [escapeCsvField('Category'), escapeCsvField(category)],
    [escapeCsvField('Gender'), escapeCsvField(gender)],
    [escapeCsvField('Preferred Branches'), escapeCsvField(selectedBranches)],
    [escapeCsvField('Active Branch Filter'), escapeCsvField(activeBranchTab)],
    [
      escapeCsvField('Disclaimer'),
      escapeCsvField(
        'Predictions are based on historical AP EAPCET cutoff data and are not a guarantee of admission.'
      ),
    ],
    [], // Blank row separator
  ].map((row) => row.join(','));

  // Data table headers
  const headers = [
    'College Name',
    'College Code',
    'Branch',
    'District',
    'Type',
    'Student Rank',
    'Category',
    'Gender',
    'Closing Rank',
    'Year',
    'Round',
    'Status',
  ];

  const dataRows = exportList.map((item) => [
    item.collegeName || '',
    item.collegeCode || '',
    item.branchCode || '',
    item.district || 'N/A',
    item.type || 'N/A',
    studentRank,
    category,
    gender,
    item.closingRank || '',
    year,
    round,
    item.categorization || 'N/A',
  ]);

  const csvBody = [
    headers.map(escapeCsvField).join(','),
    ...dataRows.map((row) => row.map(escapeCsvField).join(',')),
  ].join('\r\n');

  const fullCsvContent = metaLines.join('\r\n') + '\r\n' + csvBody;

  const sanitizeRank = String(studentRank).replace(/[^0-9]/g, '');
  const filename = sanitizeRank
    ? `admitpilot-predictions-${sanitizeRank}.csv`
    : 'admitpilot-predictions.csv';

  triggerDownload(fullCsvContent, filename);
}

export function downloadShortlistCsv(shortlistItems, predictionResult, predictionParams) {
  if (!shortlistItems || shortlistItems.length === 0) {
    alert('Your shortlist is empty. Add colleges to your shortlist to download.');
    return;
  }

  const studentRank = predictionResult?.studentRank || predictionParams?.rank || '';
  const category = predictionResult?.category || predictionParams?.category || '';
  const gender = predictionResult?.gender || predictionParams?.gender || '';
  const year = predictionResult?.year || 2025;
  const round = predictionResult?.round || 'Phase 1';

  const metaLines = [
    [escapeCsvField('AdmitPilot - Shortlisted Colleges')],
    [escapeCsvField('AP EAPCET Rank'), escapeCsvField(studentRank)],
    [escapeCsvField('Category'), escapeCsvField(category)],
    [escapeCsvField('Gender'), escapeCsvField(gender)],
    [
      escapeCsvField('Disclaimer'),
      escapeCsvField(
        'Predictions are based on historical AP EAPCET cutoff data and are not a guarantee of admission.'
      ),
    ],
    [],
  ].map((row) => row.join(','));

  const headers = [
    'College Name',
    'College Code',
    'Branch',
    'District',
    'Type',
    'Student Rank',
    'Category',
    'Gender',
    'Closing Rank',
    'Year',
    'Round',
    'Status',
  ];

  const dataRows = shortlistItems.map((item) => [
    item.collegeName || '',
    item.collegeCode || '',
    item.branchCode || '',
    item.district || 'N/A',
    item.type || 'N/A',
    studentRank,
    category,
    gender,
    item.closingRank || '',
    year,
    round,
    item.categorization || 'N/A',
  ]);

  const csvBody = [
    headers.map(escapeCsvField).join(','),
    ...dataRows.map((row) => row.map(escapeCsvField).join(',')),
  ].join('\r\n');

  const fullCsvContent = metaLines.join('\r\n') + '\r\n' + csvBody;

  const sanitizeRank = String(studentRank).replace(/[^0-9]/g, '');
  const filename = sanitizeRank
    ? `admitpilot-shortlist-${sanitizeRank}.csv`
    : 'admitpilot-shortlist.csv';

  triggerDownload(fullCsvContent, filename);
}

function triggerDownload(content, filename) {
  // UTF-8 BOM for Microsoft Excel compatibility
  const bom = '\uFEFF';
  const blob = new Blob([bom + content], { type: 'text/csv;charset=utf-8;' });
  const url = URL.createObjectURL(blob);
  const link = document.createElement('a');
  link.setAttribute('href', url);
  link.setAttribute('download', filename);
  link.style.visibility = 'hidden';
  document.body.appendChild(link);
  link.click();
  document.body.removeChild(link);
  URL.revokeObjectURL(url);
}
