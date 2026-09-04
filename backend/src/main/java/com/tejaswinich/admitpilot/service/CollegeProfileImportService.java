package com.tejaswinich.admitpilot.service;

import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.entity.CollegePlacement;
import com.tejaswinich.admitpilot.entity.CollegeProfile;
import com.tejaswinich.admitpilot.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CollegeProfileImportService {

    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final CutoffRepository cutoffRepository;
    private final CollegeProfileRepository profileRepository;
    private final CollegePlacementRepository placementRepository;

    public CollegeProfileImportService(
            CollegeRepository collegeRepository,
            BranchRepository branchRepository,
            CutoffRepository cutoffRepository,
            CollegeProfileRepository profileRepository,
            CollegePlacementRepository placementRepository) {
        this.collegeRepository = collegeRepository;
        this.branchRepository = branchRepository;
        this.cutoffRepository = cutoffRepository;
        this.profileRepository = profileRepository;
        this.placementRepository = placementRepository;
    }

    /**
     * Imports from the default audited file: collector/verified_profiles_for_import_FINAL.csv
     */
    @Transactional
    public Map<String, Object> importVerifiedProfilesFinal() throws Exception {
        File csvFile = new File("C:/Users/tejac/OneDrive/Desktop/CollegePredictor/collector/verified_profiles_for_import_FINAL.csv");
        if (!csvFile.exists()) {
            csvFile = new File("collector/verified_profiles_for_import_FINAL.csv");
        }
        if (!csvFile.exists()) {
            throw new IllegalStateException("FINAL import source file does not exist: verified_profiles_for_import_FINAL.csv");
        }

        try (InputStream is = new FileInputStream(csvFile)) {
            return importProfileCsv(is);
        }
    }

    @Transactional
    public Map<String, Object> importProfileCsv(InputStream inputStream) throws Exception {
        // 1. PRE-IMPORT DATABASE SNAPSHOT
        long preColleges = collegeRepository.count();
        long preBranches = branchRepository.count();
        long preCutoffs = cutoffRepository.count();
        long preProfiles = profileRepository.count();
        long prePlacements = placementRepository.count();

        System.out.println("=== PRE-IMPORT DATABASE SNAPSHOT ===");
        System.out.println("colleges          : " + preColleges);
        System.out.println("branches          : " + preBranches);
        System.out.println("cutoffs           : " + preCutoffs);
        System.out.println("college_profiles  : " + preProfiles);
        System.out.println("college_placements: " + prePlacements);

        int csvRowsProcessed = 0;
        int profilesInserted = 0;
        int profilesUpdated = 0;
        int profilesUnchanged = 0;
        int placementsInserted = 0;
        int placementsUpdated = 0;
        int placementsUnchanged = 0;
        int skippedColleges = 0;
        int invalidRows = 0;
        int errors = 0;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("CSV file is empty");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                csvRowsProcessed++;

                String[] tokens = line.split(",", -1);
                if (tokens.length < 1) {
                    invalidRows++;
                    continue;
                }

                String collegeCode = parseString(tokens[0]);
                if (collegeCode == null) {
                    invalidRows++;
                    continue;
                }

                Optional<College> collegeOpt = collegeRepository.findByCollegeCode(collegeCode);
                if (collegeOpt.isEmpty()) {
                    System.out.println("CollegeProfileImportService: SKIPPED unmatched college_code: " + collegeCode);
                    skippedColleges++;
                    continue;
                }

                College college = collegeOpt.get();

                String officialWebsite = tokens.length > 1 ? parseString(tokens[1]) : null;
                Integer nirfRank = tokens.length > 2 ? parseInteger(tokens[2]) : null;
                String nirfRankBand = tokens.length > 3 ? parseString(tokens[3]) : null;
                Integer nirfYear = tokens.length > 4 ? parseInteger(tokens[4]) : null;
                String nirfCategory = tokens.length > 5 ? parseString(tokens[5]) : null;

                // --- UPSERT PROFILE ---
                Optional<CollegeProfile> profileOpt = profileRepository.findByCollegeId(college.getId());
                boolean isNewProfile = profileOpt.isEmpty();
                CollegeProfile profile = profileOpt.orElseGet(() -> {
                    CollegeProfile p = new CollegeProfile();
                    p.setCollege(college);
                    return p;
                });

                boolean profileModified = false;
                if (officialWebsite != null && !officialWebsite.equals(profile.getOfficialWebsite())) {
                    profile.setOfficialWebsite(officialWebsite);
                    profileModified = true;
                }
                if (nirfRank != null && !nirfRank.equals(profile.getNirfRank())) {
                    profile.setNirfRank(nirfRank);
                    profileModified = true;
                }
                if (nirfRankBand != null && !nirfRankBand.equals(profile.getNirfRankBand())) {
                    profile.setNirfRankBand(nirfRankBand);
                    profileModified = true;
                }
                if (nirfYear != null && !nirfYear.equals(profile.getNirfYear())) {
                    profile.setNirfYear(nirfYear);
                    profileModified = true;
                }
                if (nirfCategory != null && !nirfCategory.equals(profile.getNirfCategory())) {
                    profile.setNirfCategory(nirfCategory);
                    profileModified = true;
                }

                if (isNewProfile) {
                    profileRepository.save(profile);
                    profilesInserted++;
                } else if (profileModified) {
                    profileRepository.save(profile);
                    profilesUpdated++;
                } else {
                    profilesUnchanged++;
                }

                // --- UPSERT PLACEMENT ---
                Integer placementYear = tokens.length > 6 ? parseInteger(tokens[6]) : null;
                if (placementYear != null) {
                    Double placementRate = tokens.length > 7 ? parseDouble(tokens[7]) : null;
                    Integer studentsPlaced = tokens.length > 8 ? parseInteger(tokens[8]) : null;
                    Double averagePackage = tokens.length > 9 ? parseDouble(tokens[9]) : null;
                    Double medianPackage = tokens.length > 10 ? parseDouble(tokens[10]) : null;
                    Double highestPackage = tokens.length > 11 ? parseDouble(tokens[11]) : null;
                    String sourceUrl = tokens.length > 12 ? parseString(tokens[12]) : null;

                    final Integer year = placementYear;
                    Optional<CollegePlacement> placementOpt = placementRepository.findByCollegeIdAndYear(college.getId(), year);
                    boolean isNewPlacement = placementOpt.isEmpty();
                    CollegePlacement placement = placementOpt.orElseGet(() -> {
                        CollegePlacement cp = new CollegePlacement();
                        cp.setCollege(college);
                        cp.setYear(year);
                        return cp;
                    });

                    boolean placementModified = false;
                    if (placementRate != null && !placementRate.equals(placement.getPlacementRate())) {
                        placement.setPlacementRate(placementRate);
                        placementModified = true;
                    }
                    if (studentsPlaced != null && !studentsPlaced.equals(placement.getStudentsPlaced())) {
                        placement.setStudentsPlaced(studentsPlaced);
                        placementModified = true;
                    }
                    if (averagePackage != null && !averagePackage.equals(placement.getAveragePackage())) {
                        placement.setAveragePackage(averagePackage);
                        placementModified = true;
                    }
                    if (medianPackage != null && !medianPackage.equals(placement.getMedianPackage())) {
                        placement.setMedianPackage(medianPackage);
                        placementModified = true;
                    }
                    if (highestPackage != null && !highestPackage.equals(placement.getHighestPackage())) {
                        placement.setHighestPackage(highestPackage);
                        placementModified = true;
                    }
                    if (sourceUrl != null && !sourceUrl.equals(placement.getSourceUrl())) {
                        placement.setSourceUrl(sourceUrl);
                        placementModified = true;
                    }

                    if (isNewPlacement) {
                        placementRepository.save(placement);
                        placementsInserted++;
                    } else if (placementModified) {
                        placementRepository.save(placement);
                        placementsUpdated++;
                    } else {
                        placementsUnchanged++;
                    }
                }
            }
        }

        // 2. POST-IMPORT DATABASE SNAPSHOT & CORE DATA INTEGRITY CHECK
        long postColleges = collegeRepository.count();
        long postBranches = branchRepository.count();
        long postCutoffs = cutoffRepository.count();
        long postProfiles = profileRepository.count();
        long postPlacements = placementRepository.count();

        System.out.println("=== POST-IMPORT DATABASE SNAPSHOT ===");
        System.out.println("colleges          : " + postColleges);
        System.out.println("branches          : " + postBranches);
        System.out.println("cutoffs           : " + postCutoffs);
        System.out.println("college_profiles  : " + postProfiles);
        System.out.println("college_placements: " + postPlacements);

        // Core Invariants Check
        if (postColleges != 274 || postBranches != 1509 || postCutoffs != 28183) {
            throw new IllegalStateException("CRITICAL INTEGRITY VIOLATION: Core database counts changed! Colleges: " 
                    + postColleges + ", Branches: " + postBranches + ", Cutoffs: " + postCutoffs);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("status", "SUCCESS");
        result.put("csvRowsProcessed", csvRowsProcessed);
        result.put("profilesInserted", profilesInserted);
        result.put("profilesUpdated", profilesUpdated);
        result.put("profilesUnchanged", profilesUnchanged);
        result.put("placementsInserted", placementsInserted);
        result.put("placementsUpdated", placementsUpdated);
        result.put("placementsUnchanged", placementsUnchanged);
        result.put("skippedColleges", skippedColleges);
        result.put("invalidRows", invalidRows);
        result.put("errors", errors);
        result.put("preColleges", preColleges);
        result.put("preBranches", preBranches);
        result.put("preCutoffs", preCutoffs);
        result.put("preProfiles", preProfiles);
        result.put("prePlacements", prePlacements);
        result.put("postColleges", postColleges);
        result.put("postBranches", postBranches);
        result.put("postCutoffs", postCutoffs);
        result.put("postProfiles", postProfiles);
        result.put("postPlacements", postPlacements);

        return result;
    }

    private String parseString(String val) {
        if (val == null) return null;
        String trimmed = val.trim();
        return trimmed.isEmpty() || trimmed.equalsIgnoreCase("NULL") ? null : trimmed;
    }

    private Integer parseInteger(String val) {
        String s = parseString(val);
        if (s == null) return null;
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private Double parseDouble(String val) {
        String s = parseString(val);
        if (s == null) return null;
        try {
            return Double.parseDouble(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
