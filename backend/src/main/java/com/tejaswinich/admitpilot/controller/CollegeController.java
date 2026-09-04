package com.tejaswinich.admitpilot.controller;

import com.tejaswinich.admitpilot.dto.CollegeDTO;
import com.tejaswinich.admitpilot.service.CollegeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colleges")
public class CollegeController {

    private final CollegeService collegeService;

    public CollegeController(CollegeService collegeService) {
        this.collegeService = collegeService;
    }

    // Add College
    @PostMapping
    public ResponseEntity<CollegeDTO> addCollege(@RequestBody CollegeDTO collegeDTO) {
        return ResponseEntity.ok(collegeService.saveCollege(collegeDTO));
    }

    // Get All Colleges
    @GetMapping
    public ResponseEntity<List<CollegeDTO>> getAllColleges() {
        return ResponseEntity.ok(collegeService.getAllColleges());
    }

    // Get College By ID
    @GetMapping("/{id}")
    public ResponseEntity<CollegeDTO> getCollegeById(@PathVariable Long id) {
        CollegeDTO college = collegeService.getCollegeById(id);
        if (college == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(college);
    }

    // Get College By Code
    @GetMapping("/code/{code}")
    public ResponseEntity<CollegeDTO> getCollegeByCode(@PathVariable String code) {
        CollegeDTO college = collegeService.getCollegeByCode(code);
        if (college == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(college);
    }

    // Update College
    @PutMapping("/{id}")
    public ResponseEntity<CollegeDTO> updateCollege(@PathVariable Long id, @RequestBody CollegeDTO collegeDTO) {
        CollegeDTO updated = collegeService.updateCollege(id, collegeDTO);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    // Delete College
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteCollege(@PathVariable Long id) {
        String result = collegeService.deleteCollege(id);
        return ResponseEntity.ok(result);
    }

    // Search By College Name
    @GetMapping("/name/{name}")
    public ResponseEntity<List<CollegeDTO>> getByCollegeName(@PathVariable String name) {
        return ResponseEntity.ok(collegeService.getByCollegeName(name));
    }

    // Search By District
    @GetMapping("/district/{district}")
    public ResponseEntity<List<CollegeDTO>> getByDistrict(@PathVariable String district) {
        return ResponseEntity.ok(collegeService.getByDistrict(district));
    }

    // Search By Type
    @GetMapping("/type/{type}")
    public ResponseEntity<List<CollegeDTO>> getByType(@PathVariable String type) {
        return ResponseEntity.ok(collegeService.getByType(type));
    }

    // Search By Region
    @GetMapping("/region/{region}")
    public ResponseEntity<List<CollegeDTO>> getByRegion(@PathVariable String region) {
        return ResponseEntity.ok(collegeService.getByRegion(region));
    }
}