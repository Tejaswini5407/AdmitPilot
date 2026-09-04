import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

/**
 * AdmitPilot PDF Export Utility
 * Generates structured, professional PDF prediction reports with student context,
 * closing rank data table, and official disclaimer.
 */
export async function generatePredictionPdf({
  predictionResult,
  predictionParams,
  itemsToExport = null,
  activeBranchTab = 'ALL',
}) {
  if (!predictionResult || !predictionResult.results) {
    alert('No prediction results available to generate PDF.');
    return;
  }

  const studentRank = predictionResult.studentRank || predictionParams?.rank || 'N/A';
  const category = predictionResult.category || predictionParams?.category || 'N/A';
  const gender = predictionResult.gender || predictionParams?.gender || 'N/A';
  const selectedBranches = predictionParams?.branches ? predictionParams.branches.join(', ') : 'All Branches';
  const year = predictionResult.year || 2025;
  const round = predictionResult.round || 'Phase 1';

  // Determine list of colleges to render in PDF
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
    alert('No matching prediction records found to export to PDF.');
    return;
  }

  // Initialize jsPDF document (A4 portrait)
  const doc = new jsPDF({
    orientation: 'portrait',
    unit: 'mm',
    format: 'a4',
  });

  const pageWidth = doc.internal.pageSize.getWidth();
  const margin = 14;

  // Header Banner Background
  doc.setFillColor(15, 23, 42); // #0f172a (Primary dark background)
  doc.rect(0, 0, pageWidth, 28, 'F');

  // AdmitPilot Title & Branding
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(18);
  doc.setTextColor(99, 102, 241); // #6366f1 Primary brand color
  doc.text('AdmitPilot', margin, 12);

  doc.setFontSize(10);
  doc.setFont('helvetica', 'normal');
  doc.setTextColor(203, 213, 225); // #cbd5e1 Light text
  doc.text('AP EAPCET College Prediction Report', margin, 18);

  doc.setFontSize(8);
  doc.setTextColor(148, 163, 184);
  const dateStr = new Date().toLocaleDateString('en-IN', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
  doc.text(`Generated: ${dateStr}`, pageWidth - margin - 30, 18);

  // Student Prediction Context Box
  let yPos = 34;
  doc.setFillColor(248, 250, 252);
  doc.setDrawColor(226, 232, 240);
  doc.roundedRect(margin, yPos, pageWidth - margin * 2, 24, 2, 2, 'FD');

  doc.setFont('helvetica', 'bold');
  doc.setFontSize(9);
  doc.setTextColor(51, 65, 85);
  doc.text('Student Selection Criteria:', margin + 4, yPos + 6);

  doc.setFont('helvetica', 'normal');
  doc.setFontSize(9);
  doc.setTextColor(30, 41, 59);

  const col1 = `AP EAPCET Rank: ${studentRank.toLocaleString()}`;
  const col2 = `Category: ${category}`;
  const col3 = `Gender: ${gender}`;
  const col4 = `Active Tab: ${activeBranchTab}`;

  doc.text(col1, margin + 4, yPos + 13);
  doc.text(col2, margin + 55, yPos + 13);
  doc.text(col3, margin + 95, yPos + 13);
  doc.text(col4, margin + 135, yPos + 13);

  doc.text(`Preferred Branches: ${selectedBranches}`, margin + 4, yPos + 19);
  doc.text(`Dataset: AP EAPCET ${year} (${round})`, margin + 135, yPos + 19);

  yPos += 30;

  // Section Heading
  doc.setFont('helvetica', 'bold');
  doc.setFontSize(11);
  doc.setTextColor(15, 23, 42);
  doc.text(
    `College Prediction Results (${exportList.length} ${exportList.length === 1 ? 'College' : 'Colleges'})`,
    margin,
    yPos
  );

  yPos += 4;

  // Map data to table rows
  const tableData = exportList.map((item, index) => [
    index + 1,
    item.collegeName || 'N/A',
    item.collegeCode || 'N/A',
    item.branchCode || 'N/A',
    item.closingRank ? item.closingRank.toLocaleString() : 'N/A',
    year,
    round,
    item.categorization || 'N/A',
  ]);

  // Generate Table using jsPDF-AutoTable
  autoTable(doc, {
    startY: yPos,
    head: [['#', 'College Name', 'Code', 'Branch', 'Closing Rank', 'Year', 'Round', 'Status']],
    body: tableData,
    theme: 'grid',
    headStyles: {
      fillColor: [30, 41, 59], // #1e293b
      textColor: [255, 255, 255],
      fontStyle: 'bold',
      fontSize: 8.5,
      halign: 'left',
    },
    bodyStyles: {
      fontSize: 8,
      textColor: [30, 41, 59],
      cellPadding: 2.5,
    },
    alternateRowStyles: {
      fillColor: [248, 250, 252],
    },
    columnStyles: {
      0: { cellWidth: 8, halign: 'center' },
      1: { cellWidth: 70 }, // College Name
      2: { cellWidth: 18, fontStyle: 'bold' }, // Code
      3: { cellWidth: 18, fontStyle: 'bold' }, // Branch
      4: { cellWidth: 24, halign: 'right', fontStyle: 'bold' }, // Closing Rank
      5: { cellWidth: 14, halign: 'center' }, // Year
      6: { cellWidth: 16, halign: 'center' }, // Round
      7: { cellWidth: 14, halign: 'center', fontStyle: 'bold' }, // Status
    },
    didParseCell: function (data) {
      if (data.section === 'body' && data.column.index === 7) {
        const val = data.cell.raw;
        if (val === 'SAFE') {
          data.cell.styles.textColor = [16, 185, 129]; // Green
        } else if (val === 'TARGET') {
          data.cell.styles.textColor = [245, 158, 11]; // Amber
        } else if (val === 'DREAM') {
          data.cell.styles.textColor = [139, 92, 246]; // Purple
        }
      }
    },
    margin: { left: margin, right: margin, bottom: 20 },
  });

  // Footer Disclaimer on all pages
  const totalPages = doc.internal.getNumberOfPages();
  for (let i = 1; i <= totalPages; i++) {
    doc.setPage(i);
    const pageHeight = doc.internal.pageSize.getHeight();

    doc.setDrawColor(226, 232, 240);
    doc.line(margin, pageHeight - 14, pageWidth - margin, pageHeight - 14);

    doc.setFont('helvetica', 'italic');
    doc.setFontSize(7.5);
    doc.setTextColor(100, 116, 139);
    doc.text(
      'Disclaimer: Predictions are based on historical AP EAPCET cutoff data and are not a guarantee of admission.',
      margin,
      pageHeight - 8
    );

    doc.setFont('helvetica', 'normal');
    doc.text(`Page ${i} of ${totalPages}`, pageWidth - margin - 15, pageHeight - 8);
  }

  // Save PDF file
  const sanitizeRank = String(studentRank).replace(/[^0-9]/g, '');
  const filename = sanitizeRank
    ? `admitpilot-predictions-${sanitizeRank}.pdf`
    : 'admitpilot-predictions.pdf';

  doc.save(filename);
}
