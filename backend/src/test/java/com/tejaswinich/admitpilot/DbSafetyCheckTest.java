package com.tejaswinich.admitpilot;

import com.tejaswinich.admitpilot.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class DbSafetyCheckTest {

    @Autowired
    private CollegeRepository collegeRepository;

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private CutoffRepository cutoffRepository;

    @Autowired
    private CollegeProfileRepository profileRepository;

    @Autowired
    private CollegePlacementRepository placementRepository;

    @Test
    void verifyDatabaseCountsBeforeImport() {
        long collegeCount = collegeRepository.count();
        long branchCount = branchRepository.count();
        long cutoffCount = cutoffRepository.count();
        long profileCount = profileRepository.count();
        long placementCount = placementRepository.count();

        System.out.println("=== PRE-IMPORT DATABASE SAFETY CHECK ===");
        System.out.println("SELECT COUNT(*) FROM colleges           : " + collegeCount);
        System.out.println("SELECT COUNT(*) FROM branches           : " + branchCount);
        System.out.println("SELECT COUNT(*) FROM cutoffs            : " + cutoffCount);
        System.out.println("SELECT COUNT(*) FROM college_profiles   : " + profileCount);
        System.out.println("SELECT COUNT(*) FROM college_placements : " + placementCount);

        assertEquals(274, collegeCount, "Colleges count must equal 274");
        assertEquals(1509, branchCount, "Branches count must equal 1509");
        assertEquals(28183, cutoffCount, "Cutoffs count MUST remain exactly 28,183");
    }
}
