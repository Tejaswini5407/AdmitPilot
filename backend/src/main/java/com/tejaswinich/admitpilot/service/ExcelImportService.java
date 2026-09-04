package com.tejaswinich.admitpilot.service;

import com.tejaswinich.admitpilot.entity.Branch;
import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.entity.Cutoff;
import com.tejaswinich.admitpilot.repository.BranchRepository;
import com.tejaswinich.admitpilot.repository.CollegeRepository;
import com.tejaswinich.admitpilot.repository.CutoffRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.util.*;

@Service
public class ExcelImportService {

    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final CutoffRepository cutoffRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${app.import.default-year:2025}")
    private int defaultYear;

    @Value("${app.import.default-round:Phase 1}")
    private String defaultRound;

    private final DataFormatter formatter = new DataFormatter();

    public ExcelImportService(
            CollegeRepository collegeRepository,
            BranchRepository branchRepository,
            CutoffRepository cutoffRepository) {
        this.collegeRepository = collegeRepository;
        this.branchRepository = branchRepository;
        this.cutoffRepository = cutoffRepository;
    }

    private static class CutoffColumnMapping {
        final int columnIndex;
        final String category;
        final String gender;

        CutoffColumnMapping(int columnIndex, String category, String gender) {
            this.columnIndex = columnIndex;
            this.category = category;
            this.gender = gender;
        }
    }

    @Transactional
    public Map<String, Object> importExcel() {
        Map<String, Object> summary = new LinkedHashMap<>();

        try (
                InputStream inputStream = new ClassPathResource("data/ap_cutoff.xlsx").getInputStream();
                Workbook workbook = new XSSFWorkbook(inputStream)
        ) {
            Sheet sheet = workbook.getSheet("Table 1");
            if (sheet == null) {
                throw new RuntimeException("Table 1 sheet not found in Excel workbook!");
            }

            int headerRowIndex = -1;
            int instCodeCol = -1;
            int instNameCol = -1;
            int typeCol = -1;
            int regionCol = -1;
            int districtCol = -1;
            int localAreaCol = -1;
            int branchCodeCol = -1;

            List<CutoffColumnMapping> cutoffMappings = new ArrayList<>();

            // Find header row dynamically
            for (int r = 0; r <= Math.min(10, sheet.getLastRowNum()); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                for (int c = 0; c < row.getLastCellNum(); c++) {
                    String val = getString(row, c);
                    if (val == null) continue;
                    String norm = val.replaceAll("[\\s\\n_]", "").toUpperCase();

                    if (norm.equals("INSTCODE")) {
                        headerRowIndex = r;
                        break;
                    }
                }
                if (headerRowIndex != -1) break;
            }

            if (headerRowIndex == -1) {
                throw new RuntimeException("Could not dynamically locate header row containing INST_CODE!");
            }

            Row headerRow = sheet.getRow(headerRowIndex);
            for (int c = 0; c < headerRow.getLastCellNum(); c++) {
                String val = getString(headerRow, c);
                if (val == null) continue;
                String norm = val.replaceAll("[\\s\\n_]", "").toUpperCase();

                switch (norm) {
                    case "INSTCODE":
                        instCodeCol = c;
                        break;
                    case "INSTNAME":
                        instNameCol = c;
                        break;
                    case "TYPE":
                        typeCol = c;
                        break;
                    case "INSTREG":
                    case "INSTREGIONAL":
                        regionCol = c;
                        break;
                    case "DIST":
                    case "DISTRICT":
                    case "AREA":
                        districtCol = c;
                        break;
                    case "LOCALAREA":
                        localAreaCol = c;
                        break;
                    case "BRANCHCODE":
                        branchCodeCol = c;
                        break;
                    default:
                        // Cutoff column matching
                        CutoffCategoryGender cg = resolveCategoryGender(norm);
                        if (cg != null) {
                            cutoffMappings.add(new CutoffColumnMapping(c, cg.category, cg.gender));
                        }
                        break;
                }
            }

            if (instCodeCol == -1 || branchCodeCol == -1) {
                throw new RuntimeException("Header row missing essential columns INST_CODE or BRANCH_CODE!");
            }

            System.out.println("ExcelImportService: Dynamic header scanning complete.");
            System.out.println("  Header Row Index : " + headerRowIndex);
            System.out.println("  Cutoff Columns   : " + cutoffMappings.size());

            // Cache existing database entities to prevent duplicate SELECT queries
            Map<String, College> collegeMap = new HashMap<>();
            collegeRepository.findAll().forEach(c -> collegeMap.put(c.getCollegeCode(), c));

            Map<String, Branch> branchMap = new HashMap<>();
            branchRepository.findAll().forEach(b -> {
                if (b.getCollege() != null) {
                    branchMap.put(b.getCollege().getCollegeCode() + "_" + b.getBranchCode(), b);
                }
            });

            Set<String> existingCutoffKeys = new HashSet<>();
            cutoffRepository.findAll().forEach(c -> {
                if (c.getBranch() != null && c.getBranch().getCollege() != null) {
                    String k = c.getBranch().getCollege().getCollegeCode() + "_" + c.getBranch().getBranchCode()
                            + "_" + c.getCategory() + "_" + c.getGender() + "_" + c.getYear() + "_" + c.getRound();
                    existingCutoffKeys.add(k);
                }
            });

            int collegesAdded = 0;
            int branchesAdded = 0;
            int cutoffsProcessed = 0;
            int skippedRows = 0;

            List<Cutoff> batchCutoffs = new ArrayList<>();

            for (int r = headerRowIndex + 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    skippedRows++;
                    continue;
                }

                String collegeCode = getString(row, instCodeCol);
                String branchCode = getString(row, branchCodeCol);

                if (collegeCode == null || collegeCode.isBlank() || branchCode == null || branchCode.isBlank()) {
                    skippedRows++;
                    continue;
                }

                String collegeName = instNameCol != -1 ? getString(row, instNameCol) : null;
                String type = typeCol != -1 ? getString(row, typeCol) : null;
                String region = regionCol != -1 ? getString(row, regionCol) : null;
                String district = districtCol != -1 ? getString(row, districtCol) : null;
                String localArea = localAreaCol != -1 ? getString(row, localAreaCol) : null;

                // 1. Resolve College
                College college = collegeMap.get(collegeCode);
                if (college == null) {
                    college = new College();
                    college.setCollegeCode(collegeCode);
                    college.setCollegeName(collegeName);
                    college.setType(type);
                    college.setRegion(region);
                    college.setDistrict(district);
                    college.setLocalArea(localArea);
                    college = collegeRepository.save(college);
                    collegeMap.put(collegeCode, college);
                    collegesAdded++;
                }

                // 2. Resolve Branch
                String branchKey = collegeCode + "_" + branchCode;
                Branch branch = branchMap.get(branchKey);
                if (branch == null) {
                    branch = new Branch();
                    branch.setBranchCode(branchCode);
                    branch.setCollege(college);
                    branch = branchRepository.save(branch);
                    branchMap.put(branchKey, branch);
                    branchesAdded++;
                }

                // 3. Resolve Cutoffs
                for (CutoffColumnMapping mapping : cutoffMappings) {
                    Integer closingRank = getInteger(row, mapping.columnIndex);
                    if (closingRank == null) {
                        continue;
                    }

                    String cutoffKey = branchKey + "_" + mapping.category + "_" + mapping.gender + "_" + defaultYear + "_" + defaultRound;
                    if (existingCutoffKeys.contains(cutoffKey)) {
                        continue;
                    }
                    existingCutoffKeys.add(cutoffKey);

                    Cutoff cutoff = new Cutoff();
                    cutoff.setCategory(mapping.category);
                    cutoff.setGender(mapping.gender);
                    cutoff.setClosingRank(closingRank);
                    cutoff.setYear(defaultYear);
                    cutoff.setRound(defaultRound);
                    cutoff.setBranch(branch);

                    batchCutoffs.add(cutoff);
                    cutoffsProcessed++;

                    if (batchCutoffs.size() >= 500) {
                        cutoffRepository.saveAll(batchCutoffs);
                        cutoffRepository.flush();
                        entityManager.clear();
                        batchCutoffs.clear();
                    }
                }
            }

            if (!batchCutoffs.isEmpty()) {
                cutoffRepository.saveAll(batchCutoffs);
                cutoffRepository.flush();
                entityManager.clear();
                batchCutoffs.clear();
            }

            summary.put("status", "SUCCESS");
            summary.put("headerRowIndex", headerRowIndex);
            summary.put("year", defaultYear);
            summary.put("round", defaultRound);
            summary.put("collegesTotal", collegeMap.size());
            summary.put("collegesAdded", collegesAdded);
            summary.put("branchesTotal", branchMap.size());
            summary.put("branchesAdded", branchesAdded);
            summary.put("cutoffsSaved", cutoffsProcessed);
            summary.put("skippedRows", skippedRows);

            System.out.println("========================================");
            System.out.println("EXCEL IMPORT COMPLETED SUCCESSFULLY");
            System.out.println("  Year / Round   : " + defaultYear + " / " + defaultRound);
            System.out.println("  Colleges       : " + collegeMap.size() + " (Added: " + collegesAdded + ")");
            System.out.println("  Branches       : " + branchMap.size() + " (Added: " + branchesAdded + ")");
            System.out.println("  Cutoffs Saved  : " + cutoffsProcessed);
            System.out.println("========================================");

        } catch (Exception e) {
            summary.put("status", "FAILED");
            summary.put("error", e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Excel import failed: " + e.getMessage(), e);
        }

        return summary;
    }

    private static class CutoffCategoryGender {
        final String category;
        final String gender;

        CutoffCategoryGender(String category, String gender) {
            this.category = category;
            this.gender = gender;
        }
    }

    private CutoffCategoryGender resolveCategoryGender(String norm) {
        String gender = null;
        if (norm.endsWith("BOYS")) {
            gender = "BOYS";
        } else if (norm.endsWith("GIRLS") || norm.endsWith("GIRL")) {
            gender = "GIRLS";
        }

        if (gender == null) {
            return null;
        }

        String catPart = norm.substring(0, norm.length() - gender.length());

        switch (catPart) {
            case "OC":
                return new CutoffCategoryGender("OC", gender);
            case "SCI":
            case "SC1":
                return new CutoffCategoryGender("SC-I", gender);
            case "SCII":
            case "SC2":
                return new CutoffCategoryGender("SC-II", gender);
            case "SCIII":
            case "SC3":
                return new CutoffCategoryGender("SC-III", gender);
            case "ST":
                return new CutoffCategoryGender("ST", gender);
            case "BCA":
                return new CutoffCategoryGender("BCA", gender);
            case "BCB":
                return new CutoffCategoryGender("BCB", gender);
            case "BCC":
                return new CutoffCategoryGender("BCC", gender);
            case "BCD":
                return new CutoffCategoryGender("BCD", gender);
            case "BCE":
                return new CutoffCategoryGender("BCE", gender);
            case "OCEWS":
            case "EWS":
                return new CutoffCategoryGender("OC-EWS", gender);
            default:
                return null;
        }
    }

    private String getString(Row row, int columnIndex) {
        if (row == null || row.getCell(columnIndex) == null) {
            return null;
        }
        String value = formatter.formatCellValue(row.getCell(columnIndex));
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private Integer getInteger(Row row, int columnIndex) {
        String value = getString(row, columnIndex);
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            value = value.replace(",", "").trim();
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}