package com.tejaswinich.admitpilot;

import com.tejaswinich.admitpilot.controller.PredictionController;
import com.tejaswinich.admitpilot.dto.BranchPredictionResult;
import com.tejaswinich.admitpilot.dto.CollegePredictionDTO;
import com.tejaswinich.admitpilot.dto.PredictionRequest;
import com.tejaswinich.admitpilot.dto.PredictionResponse;
import com.tejaswinich.admitpilot.service.PredictionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PredictionApiIntegrationTest {

    @Autowired
    private PredictionService predictionService;

    @Autowired
    private PredictionController predictionController;

    @Test
    @DisplayName("TEST 1: Valid request with one branch returns categorized predicted colleges")
    public void testValidRequestSingleBranch() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(50000);
        request.setCategory("OC");
        request.setGender("GIRLS");
        request.setBranches(Collections.singletonList("CSE"));

        PredictionResponse response = predictionService.predict(request);

        assertNotNull(response, "Prediction response should not be null");
        assertEquals(50000, response.getStudentRank());
        assertEquals("OC", response.getCategory());
        assertEquals("GIRLS", response.getGender());
        assertEquals(2025, response.getYear());
        assertEquals("Phase 1", response.getRound());
        assertEquals(1, response.getResults().size());

        BranchPredictionResult cseResult = response.getResults().get(0);
        assertEquals("CSE", cseResult.getBranchCode());

        for (CollegePredictionDTO college : cseResult.getColleges()) {
            assertNotNull(college.getCategorization(), "College categorization must be present");
            assertTrue(Arrays.asList("SAFE", "TARGET", "DREAM").contains(college.getCategorization()));

            if ("SAFE".equals(college.getCategorization())) {
                assertTrue(college.getClosingRank() >= 50000);
                double margin = (college.getClosingRank() - 50000) / (double) college.getClosingRank();
                assertTrue(margin >= 0.20, "SAFE margin must be >= 20%");
            } else if ("TARGET".equals(college.getCategorization())) {
                assertTrue(college.getClosingRank() >= 50000);
                double margin = (college.getClosingRank() - 50000) / (double) college.getClosingRank();
                assertTrue(margin < 0.20, "TARGET margin must be < 20%");
            } else if ("DREAM".equals(college.getCategorization())) {
                assertTrue(college.getClosingRank() < 50000);
                double gap = (50000 - college.getClosingRank()) / (double) college.getClosingRank();
                assertTrue(gap <= 0.10, "DREAM gap must be <= 10%");
            }
            assertEquals("OC", college.getCategory());
            assertEquals("GIRLS", college.getGender());
            assertEquals("CSE", college.getBranchCode());
            assertNotNull(college.getCollegeId());
            assertNotNull(college.getCollegeCode());
        }
    }

    @Test
    @DisplayName("TEST 2: Valid request with multiple branches grouped by branch")
    public void testValidRequestMultipleBranches() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(50000);
        request.setCategory("OC");
        request.setGender("GIRLS");
        request.setBranches(Arrays.asList("CSE", "AIM", "ECE"));

        PredictionResponse response = predictionService.predict(request);

        assertNotNull(response);
        assertEquals(3, response.getResults().size());

        List<String> resultBranchCodes = Arrays.asList(
                response.getResults().get(0).getBranchCode(),
                response.getResults().get(1).getBranchCode(),
                response.getResults().get(2).getBranchCode()
        );

        assertTrue(resultBranchCodes.contains("CSE"));
        assertTrue(resultBranchCodes.contains("AIM"));
        assertTrue(resultBranchCodes.contains("ECE"));

        for (BranchPredictionResult bResult : response.getResults()) {
            for (CollegePredictionDTO c : bResult.getColleges()) {
                assertEquals(bResult.getBranchCode(), c.getBranchCode());
                assertNotNull(c.getCategorization());
            }
        }
    }

    @Test
    @DisplayName("TEST 3: Category matching - OC does not return non-OC categories")
    public void testCategoryMatchingExact() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(50000);
        request.setCategory("OC");
        request.setGender("GIRLS");
        request.setBranches(Collections.singletonList("CSE"));

        PredictionResponse response = predictionService.predict(request);

        for (BranchPredictionResult bResult : response.getResults()) {
            for (CollegePredictionDTO c : bResult.getColleges()) {
                assertEquals("OC", c.getCategory(), "Category must be strictly OC");
            }
        }
    }

    @Test
    @DisplayName("TEST 4: Gender matching - GIRLS does not return BOYS records")
    public void testGenderMatchingExact() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(50000);
        request.setCategory("OC");
        request.setGender("GIRLS");
        request.setBranches(Collections.singletonList("CSE"));

        PredictionResponse response = predictionService.predict(request);

        for (BranchPredictionResult bResult : response.getResults()) {
            for (CollegePredictionDTO c : bResult.getColleges()) {
                assertEquals("GIRLS", c.getGender(), "Gender must be strictly GIRLS");
            }
        }
    }

    @Test
    @DisplayName("TEST 5: Invalid rank returns HTTP 400")
    public void testInvalidRankHttp400() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(-500);
        request.setCategory("OC");
        request.setGender("GIRLS");
        request.setBranches(Collections.singletonList("CSE"));

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        ResponseEntity<?> response = predictionController.predictColleges(request, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("TEST 6: Empty branch list returns HTTP 400")
    public void testEmptyBranchesHttp400() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(50000);
        request.setCategory("OC");
        request.setGender("GIRLS");
        request.setBranches(Collections.emptyList());

        BindingResult bindingResult = new BeanPropertyBindingResult(request, "request");
        ResponseEntity<?> response = predictionController.predictColleges(request, bindingResult);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("TEST 7: No matching cutoff returns clean empty results list")
    public void testNoMatchingCutoffCleanEmpty() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(1);
        request.setCategory("NON_EXISTENT_CATEGORY");
        request.setGender("GIRLS");
        request.setBranches(Collections.singletonList("CSE"));

        PredictionResponse response = predictionService.predict(request);

        assertNotNull(response);
        assertEquals(1, response.getResults().size());
        BranchPredictionResult result = response.getResults().get(0);
        assertEquals("CSE", result.getBranchCode());
        assertTrue(result.getColleges().isEmpty(), "Colleges list should be empty when no cutoffs match");
    }

    @Test
    @DisplayName("TEST 8: Verify year and round in response are 2025 and Phase 1")
    public void testYearAndRoundDefaultMetadata() {
        PredictionRequest request = new PredictionRequest();
        request.setRank(50000);
        request.setCategory("OC");
        request.setGender("GIRLS");
        request.setBranches(Collections.singletonList("CSE"));

        PredictionResponse response = predictionService.predict(request);

        assertEquals(2025, response.getYear());
        assertEquals("Phase 1", response.getRound());
    }
}
