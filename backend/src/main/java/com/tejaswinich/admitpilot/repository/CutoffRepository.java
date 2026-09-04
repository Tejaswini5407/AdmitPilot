package com.tejaswinich.admitpilot.repository;

import com.tejaswinich.admitpilot.entity.Branch;
import com.tejaswinich.admitpilot.entity.Cutoff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CutoffRepository extends JpaRepository<Cutoff, Long> {

    Optional<Cutoff> findByBranchAndCategoryAndGenderAndYearAndRound(
        Branch branch,
        String category,
        String gender,
        Integer year,
        String round
    );

    List<Cutoff> findByCategoryAndGenderAndYearAndRound(
        String category,
        String gender,
        Integer year,
        String round
    );

    @Query("SELECT c FROM Cutoff c JOIN FETCH c.branch b JOIN FETCH b.college col " +
           "WHERE LOWER(c.category) = LOWER(:category) " +
           "AND LOWER(c.gender) = LOWER(:gender) " +
           "AND c.year = :year " +
           "AND LOWER(c.round) = LOWER(:round) " +
           "AND UPPER(b.branchCode) IN :branchCodes " +
           "AND c.closingRank >= :minClosingRank " +
           "ORDER BY c.closingRank ASC")
    List<Cutoff> findPredictions(
        @Param("minClosingRank") Integer minClosingRank,
        @Param("category") String category,
        @Param("gender") String gender,
        @Param("year") Integer year,
        @Param("round") String round,
        @Param("branchCodes") List<String> branchCodes
    );
}