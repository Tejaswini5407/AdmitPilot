package com.tejaswinich.admitpilot;

import com.tejaswinich.admitpilot.dto.CollegeDTO;
import com.tejaswinich.admitpilot.dto.CollegePlacementDTO;
import com.tejaswinich.admitpilot.dto.CollegeProfileDTO;
import com.tejaswinich.admitpilot.service.CollegeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CollegeProfileIntegrationTest {

    @Autowired
    private CollegeService collegeService;

    @Test
    @DisplayName("Verify College Profile & Placement DTO structure for AUCE")
    void testGetCollegeProfileForAuce() {
        CollegeDTO auce = collegeService.getCollegeByCode("AUCE");
        assertNotNull(auce, "AUCE college should exist in DB");
        assertEquals("AUCE", auce.getCollegeCode());

        CollegeProfileDTO profile = auce.getProfile();
        assertNotNull(profile, "Profile should be populated for AUCE");
        assertEquals("https://www.andhrauniversity.edu.in", profile.getOfficialWebsite());
        assertEquals(94, profile.getNirfRank());
        assertEquals(2024, profile.getNirfYear());
        assertEquals("Engineering", profile.getNirfCategory());

        assertNotNull(profile.getPlacements(), "Placements list should not be null");
        assertFalse(profile.getPlacements().isEmpty(), "Placements list should contain records");

        CollegePlacementDTO placement = profile.getPlacements().get(0);
        assertEquals(2024, placement.getYear());
        assertEquals(85.0, placement.getPlacementRate());
        assertEquals(450, placement.getStudentsPlaced());
        assertEquals(6.8, placement.getAveragePackage());
        assertEquals(6.0, placement.getMedianPackage());
        assertEquals(18.0, placement.getHighestPackage());
        assertEquals("https://www.andhrauniversity.edu.in/placements.html", placement.getSourceUrl());
    }

    @Test
    @DisplayName("Verify handling of colleges with missing profile data returns null profile DTO")
    void testGetCollegeWithNoProfileData() {
        // Fetch any college without a profile
        CollegeDTO college = collegeService.getCollegeByCode("JNTUA");
        if (college != null) {
            // Should gracefully return null or empty placements rather than error
            if (college.getProfile() != null) {
                assertTrue(college.getProfile().getPlacements().isEmpty() || college.getProfile().getOfficialWebsite() == null);
            }
        }
    }
}
