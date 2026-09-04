package com.tejaswinich.admitpilot;

import com.tejaswinich.admitpilot.dto.BranchDTO;
import com.tejaswinich.admitpilot.dto.CollegeDTO;
import com.tejaswinich.admitpilot.dto.EntityMapper;
import com.tejaswinich.admitpilot.entity.Branch;
import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.entity.Cutoff;
import com.tejaswinich.admitpilot.service.CollegeService;
import com.tejaswinich.admitpilot.service.ExcelImportService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class BackendPhase1IntegrationTest {

    @Autowired
    private ExcelImportService excelImportService;

    @Autowired
    private CollegeService collegeService;

    @Test
    @DisplayName("Verify Spring Context Loads and Services Injected")
    public void contextLoads() {
        assertNotNull(excelImportService, "ExcelImportService should be injected cleanly");
    }

    @Test
    @DisplayName("Verify DTO Mapper prevents Circular Recursion")
    public void testEntityMapperNoCircularReference() {
        College college = new College();
        college.setId(100L);
        college.setCollegeCode("TEST_COLL");
        college.setCollegeName("Test Engineering College");

        Branch branch = new Branch();
        branch.setId(200L);
        branch.setBranchCode("CSE");
        branch.setCollege(college);
        college.getBranches().add(branch);

        Cutoff cutoff = new Cutoff();
        cutoff.setId(300L);
        cutoff.setCategory("OC");
        cutoff.setGender("BOYS");
        cutoff.setClosingRank(15000);
        cutoff.setYear(2025);
        cutoff.setRound("Phase 1");
        cutoff.setBranch(branch);
        branch.getCutoffs().add(cutoff);

        CollegeDTO dto = EntityMapper.toCollegeDTO(college, true, true);

        assertNotNull(dto);
        assertEquals("TEST_COLL", dto.getCollegeCode());
        assertEquals(1, dto.getBranches().size());
        assertEquals("CSE", dto.getBranches().get(0).getBranchCode());
        assertEquals("TEST_COLL", dto.getBranches().get(0).getCollegeCode());
        assertEquals(1, dto.getBranches().get(0).getCutoffs().size());
        assertEquals("OC", dto.getBranches().get(0).getCutoffs().get(0).getCategory());
    }

    @Test
    @DisplayName("Verify Excel Import Execution")
    public void testExcelImport() {
        Map<String, Object> summary = excelImportService.importExcel();
        assertNotNull(summary);
        assertEquals("SUCCESS", summary.get("status"));
        assertTrue((int) summary.get("collegesTotal") > 0, "Colleges should be imported");
        assertTrue((int) summary.get("branchesTotal") > 0, "Branches should be imported");
    }

    @Test
    @DisplayName("Verify CollegeService returns non-empty Cutoffs for AUCE CSE")
    public void testCollegeServiceReturnsCutoffsForAuceCse() {
        CollegeDTO auce = collegeService.getCollegeByCode("AUCE");
        assertNotNull(auce, "AUCE college should exist in database");
        assertEquals("AUCE", auce.getCollegeCode());
        assertFalse(auce.getBranches().isEmpty(), "AUCE should have branches");

        Optional<BranchDTO> cseOpt = auce.getBranches().stream()
                .filter(b -> "CSE".equalsIgnoreCase(b.getBranchCode()))
                .findFirst();

        assertTrue(cseOpt.isPresent(), "AUCE should have CSE branch");
        BranchDTO cse = cseOpt.get();
        assertFalse(cse.getCutoffs().isEmpty(), "AUCE CSE branch should have non-empty cutoffs array in DTO response");
        assertEquals(20, cse.getCutoffs().size(), "AUCE CSE should have 20 cutoff records");
    }
}
