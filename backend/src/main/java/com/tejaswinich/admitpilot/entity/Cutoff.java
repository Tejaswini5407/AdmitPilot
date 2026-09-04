package com.tejaswinich.admitpilot.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "cutoffs",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_branch_cat_gen_yr_rnd", columnNames = {"branch_id", "category", "gender", "year", "round"})
    },
    indexes = {
        @Index(name = "idx_cutoff_lookup", columnList = "category, gender, closing_rank, year, round")
    }
)
public class Cutoff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "gender", nullable = false)
    private String gender;

    @Column(name = "closing_rank", nullable = false)
    private Integer closingRank;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "round", nullable = false)
    private String round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    public Cutoff() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getClosingRank() {
        return closingRank;
    }

    public void setClosingRank(Integer closingRank) {
        this.closingRank = closingRank;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}