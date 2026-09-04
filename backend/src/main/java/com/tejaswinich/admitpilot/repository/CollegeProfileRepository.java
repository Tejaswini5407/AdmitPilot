package com.tejaswinich.admitpilot.repository;

import com.tejaswinich.admitpilot.entity.CollegeProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollegeProfileRepository extends JpaRepository<CollegeProfile, Long> {
    Optional<CollegeProfile> findByCollegeId(Long collegeId);
    Optional<CollegeProfile> findByCollegeCollegeCodeIgnoreCase(String collegeCode);
}
