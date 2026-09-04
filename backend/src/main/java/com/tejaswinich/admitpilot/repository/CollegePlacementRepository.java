package com.tejaswinich.admitpilot.repository;

import com.tejaswinich.admitpilot.entity.CollegePlacement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollegePlacementRepository extends JpaRepository<CollegePlacement, Long> {
    List<CollegePlacement> findByCollegeIdOrderByYearDesc(Long collegeId);
    List<CollegePlacement> findByCollegeCollegeCodeIgnoreCaseOrderByYearDesc(String collegeCode);
    Optional<CollegePlacement> findByCollegeIdAndYear(Long collegeId, Integer year);
}
