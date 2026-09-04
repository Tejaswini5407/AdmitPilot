package com.tejaswinich.admitpilot.controller;

import com.tejaswinich.admitpilot.service.CollegeProfileImportService;
import com.tejaswinich.admitpilot.service.ExcelImportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Development & Administrative Endpoints.
 * 
 * IMPORTANT NOTE FOR PRODUCTION:
 * These endpoints trigger batch importing of official AP EAPCET data & verified college profiles.
 * In a production environment, these endpoints MUST be protected with authentication & role-based
 * authorization (e.g. Spring Security @PreAuthorize("hasRole('ADMIN')")) or disabled via profile flags.
 */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ExcelImportService excelImportService;
    private final CollegeProfileImportService profileImportService;

    public AdminController(ExcelImportService excelImportService, CollegeProfileImportService profileImportService) {
        this.excelImportService = excelImportService;
        this.profileImportService = profileImportService;
    }

    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> triggerImport() {
        System.out.println("AdminController: Triggering Excel Cutoff Import via REST API...");
        Map<String, Object> result = excelImportService.importExcel();
        return ResponseEntity.ok(result);
    }

    @PostMapping("/import-profiles")
    public ResponseEntity<Map<String, Object>> triggerProfileImport() throws Exception {
        System.out.println("AdminController: Triggering Verified College Profile Import via REST API...");
        Map<String, Object> result = profileImportService.importVerifiedProfilesFinal();
        return ResponseEntity.ok(result);
    }
}
