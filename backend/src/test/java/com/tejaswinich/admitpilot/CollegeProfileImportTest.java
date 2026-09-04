package com.tejaswinich.admitpilot;

import com.tejaswinich.admitpilot.dto.CollegeDTO;
import com.tejaswinich.admitpilot.repository.*;
import com.tejaswinich.admitpilot.service.CollegeProfileImportService;
import com.tejaswinich.admitpilot.service.CollegeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CollegeProfileImportTest {

    @Autowired
    private CollegeProfileImportService profileImportService;

    @Autowired
    private CollegeService collegeService;

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
    void testSafeImportOfVerifiedProfilesFinal() throws Exception {
        // 1. Pre-import counts check
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

        assertEquals(274, preColleges, "Pre-import colleges MUST be 274");
        assertEquals(1509, preBranches, "Pre-import branches MUST be 1509");
        assertEquals(28183, preCutoffs, "Pre-import cutoffs MUST be 28183");

        // 2. Perform safe import from collector/verified_profiles_for_import_FINAL.csv
        Map<String, Object> run1Result = profileImportService.importVerifiedProfilesFinal();
        assertNotNull(run1Result);
        assertEquals("SUCCESS", run1Result.get("status"));

        // 3. Post-import counts check
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

        // Core Invariants MUST be strictly preserved
        assertEquals(274, postColleges, "Colleges count MUST remain 274");
        assertEquals(1509, postBranches, "Branches count MUST remain 1509");
        assertEquals(28183, postCutoffs, "Cutoffs count MUST remain 28183");

        // Verified Profiles and Placements created/updated
        assertEquals(41, postProfiles, "Exactly 41 verified college profiles should exist");
        assertEquals(11, postPlacements, "Exactly 11 verified college placement records should exist");

        // 4. Verify AUCE profile (Existing non-null profile updated/maintained)
        CollegeDTO auce = collegeService.getCollegeByCode("AUCE");
        assertNotNull(auce);
        assertNotNull(auce.getProfile());
        assertEquals("https://www.andhrauniversity.edu.in", auce.getProfile().getOfficialWebsite());
        assertEquals(94, auce.getProfile().getNirfRank());
        assertEquals(1, auce.getProfile().getPlacements().size());
        assertEquals(85.0, auce.getProfile().getPlacements().get(0).getPlacementRate());

        // 5. Verify JNTK profile (Newly imported with website, NIRF, placement)
        CollegeDTO jntk = collegeService.getCollegeByCode("JNTK");
        assertNotNull(jntk);
        assertNotNull(jntk.getProfile());
        assertEquals("https://www.jntuk.edu.in", jntk.getProfile().getOfficialWebsite());
        assertEquals("101-150", jntk.getProfile().getNirfRankBand());
        assertEquals(1, jntk.getProfile().getPlacements().size());
        assertEquals(33.0, jntk.getProfile().getPlacements().get(0).getHighestPackage());

        // 6. Verify RVJC profile (RVR & JC College of Engineering using canonical code RVJC)
        CollegeDTO rvjc = collegeService.getCollegeByCode("RVJC");
        assertNotNull(rvjc);
        assertNotNull(rvjc.getProfile());
        assertEquals("https://www.rvrjc.org", rvjc.getProfile().getOfficialWebsite());
        assertEquals("201-250", rvjc.getProfile().getNirfRankBand());
        assertEquals(1, rvjc.getProfile().getPlacements().size());
        assertEquals(36.0, rvjc.getProfile().getPlacements().get(0).getHighestPackage());

        // 7. Verify ANIL profile (Website only, NIRF & placements NULL)
        CollegeDTO anil = collegeService.getCollegeByCode("ANIL");
        assertNotNull(anil);
        assertNotNull(anil.getProfile());
        assertEquals("https://www.anits.edu.in", anil.getProfile().getOfficialWebsite());
        assertNull(anil.getProfile().getNirfRank());
        assertNull(anil.getProfile().getNirfRankBand());
        assertTrue(anil.getProfile().getPlacements().isEmpty());

        // 8. Verify unimported college (e.g. JNTU Vizianagaram / JNTV -> profile is null)
        CollegeDTO jntv = collegeService.getCollegeByCode("JNTV");
        assertNotNull(jntv);
        assertNull(jntv.getProfile(), "Flagged NEEDS_REVIEW college JNTV must NOT be imported");

        // 9. IDEMPOTENCY CHECK: Run import a SECOND time
        Map<String, Object> run2Result = profileImportService.importVerifiedProfilesFinal();
        assertNotNull(run2Result);
        assertEquals(0, run2Result.get("profilesInserted"), "Run 2 must insert 0 new profiles");
        assertEquals(0, run2Result.get("placementsInserted"), "Run 2 must insert 0 new placements");

        assertEquals(274, collegeRepository.count());
        assertEquals(1509, branchRepository.count());
        assertEquals(28183, cutoffRepository.count());
        assertEquals(41, profileRepository.count());
        assertEquals(11, placementRepository.count());

        System.out.println("CollegeProfileImportTest: Safe import & idempotency verification completed successfully!");
    }
}
