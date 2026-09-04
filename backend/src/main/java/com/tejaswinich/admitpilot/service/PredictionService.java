package com.tejaswinich.admitpilot.service;

import com.tejaswinich.admitpilot.dto.BranchPredictionResult;
import com.tejaswinich.admitpilot.dto.CollegePredictionDTO;
import com.tejaswinich.admitpilot.dto.PredictionRequest;
import com.tejaswinich.admitpilot.dto.PredictionResponse;
import com.tejaswinich.admitpilot.entity.Branch;
import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.entity.Cutoff;
import com.tejaswinich.admitpilot.repository.CutoffRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final CutoffRepository cutoffRepository;

    @Value("${app.import.default-year:2025}")
    private Integer defaultYear;

    @Value("${app.import.default-round:Phase 1}")
    private String defaultRound;

    @Value("${prediction.safe-margin:0.20}")
    private double safeMargin;

    @Value("${prediction.dream-margin:0.10}")
    private double dreamMargin;

    public PredictionService(CutoffRepository cutoffRepository) {
        this.cutoffRepository = cutoffRepository;
    }

    @Transactional(readOnly = true)
    public PredictionResponse predict(PredictionRequest request) {
        Integer year = (request.getYear() != null) ? request.getYear() : defaultYear;
        String round = (request.getRound() != null && !request.getRound().trim().isEmpty())
                ? request.getRound().trim()
                : defaultRound;

        List<String> originalBranches = request.getBranches();
        List<String> upperBranchCodes = originalBranches.stream()
                .filter(b -> b != null && !b.trim().isEmpty())
                .map(String::trim)
                .map(String::toUpperCase)
                .distinct()
                .collect(Collectors.toList());

        int minClosingRank = (int) Math.floor(request.getRank() / (1.0 + dreamMargin));

        List<Cutoff> cutoffs = cutoffRepository.findPredictions(
                minClosingRank,
                request.getCategory().trim(),
                request.getGender().trim(),
                year,
                round,
                upperBranchCodes
        );

        Map<String, List<CollegePredictionDTO>> branchMap = new HashMap<>();

        for (Cutoff c : cutoffs) {
            Branch b = c.getBranch();
            College col = b.getCollege();

            int closingRank = c.getClosingRank();
            int studentRank = request.getRank();
            String categorization;

            if (studentRank <= closingRank) {
                double margin = (closingRank - studentRank) / (double) closingRank;
                if (margin >= safeMargin) {
                    categorization = "SAFE";
                } else {
                    categorization = "TARGET";
                }
            } else {
                double dreamGap = (studentRank - closingRank) / (double) closingRank;
                if (dreamGap <= dreamMargin) {
                    categorization = "DREAM";
                } else {
                    continue;
                }
            }

            CollegePredictionDTO dto = new CollegePredictionDTO();
            dto.setCollegeId(col.getId());
            dto.setCollegeCode(col.getCollegeCode());
            dto.setCollegeName(col.getCollegeName());
            dto.setType(col.getType());
            dto.setRegion(col.getRegion());
            dto.setDistrict(col.getDistrict());
            dto.setLocalArea(col.getLocalArea());

            dto.setBranchId(b.getId());
            dto.setBranchCode(b.getBranchCode());
            dto.setBranchName(b.getBranchName());

            dto.setStudentRank(studentRank);
            dto.setClosingRank(closingRank);
            dto.setCategory(c.getCategory());
            dto.setGender(c.getGender());
            dto.setYear(c.getYear());
            dto.setRound(c.getRound());
            dto.setCategorization(categorization);

            String codeKey = b.getBranchCode().toUpperCase();
            branchMap.computeIfAbsent(codeKey, k -> new ArrayList<>()).add(dto);
        }

        List<BranchPredictionResult> branchResults = new ArrayList<>();
        Set<String> processedCodes = new HashSet<>();

        for (String branchInput : originalBranches) {
            if (branchInput == null || branchInput.trim().isEmpty()) {
                continue;
            }
            String upperCode = branchInput.trim().toUpperCase();
            if (processedCodes.contains(upperCode)) {
                continue;
            }
            processedCodes.add(upperCode);

            List<CollegePredictionDTO> collegesForBranch = branchMap.getOrDefault(upperCode, new ArrayList<>());
            
            // Sort colleges inside each branch by categorization priority (SAFE -> TARGET -> DREAM) then closingRank ASC
            collegesForBranch.sort((c1, c2) -> {
                int catOrder1 = getCategoryOrder(c1.getCategorization());
                int catOrder2 = getCategoryOrder(c2.getCategorization());
                if (catOrder1 != catOrder2) {
                    return Integer.compare(catOrder1, catOrder2);
                }
                return Integer.compare(c1.getClosingRank(), c2.getClosingRank());
            });

            branchResults.add(new BranchPredictionResult(upperCode, collegesForBranch));
        }

        PredictionResponse response = new PredictionResponse();
        response.setStudentRank(request.getRank());
        response.setCategory(request.getCategory().trim());
        response.setGender(request.getGender().trim());
        response.setYear(year);
        response.setRound(round);
        response.setResults(branchResults);

        return response;
    }

    private int getCategoryOrder(String cat) {
        if ("SAFE".equalsIgnoreCase(cat)) return 1;
        if ("TARGET".equalsIgnoreCase(cat)) return 2;
        if ("DREAM".equalsIgnoreCase(cat)) return 3;
        return 4;
    }
}
