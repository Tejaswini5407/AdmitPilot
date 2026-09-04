package com.tejaswinich.admitpilot.repository;

import com.tejaswinich.admitpilot.entity.Branch;
import com.tejaswinich.admitpilot.entity.College;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByCollegeAndBranchCode(College college, String branchCode);

    @Query("SELECT DISTINCT b FROM Branch b LEFT JOIN FETCH b.cutoffs WHERE b.college = :college")
    List<Branch> findWithCutoffsByCollege(@Param("college") College college);
}