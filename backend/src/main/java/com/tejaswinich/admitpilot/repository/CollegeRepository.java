package com.tejaswinich.admitpilot.repository;

import com.tejaswinich.admitpilot.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    Optional<College> findByCollegeCode(String collegeCode);

    List<College> findByCollegeNameContainingIgnoreCase(String collegeName);

    List<College> findByDistrict(String district);

    List<College> findByType(String type);

    List<College> findByRegion(String region);
}