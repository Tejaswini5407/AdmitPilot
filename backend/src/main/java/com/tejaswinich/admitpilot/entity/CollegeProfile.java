package com.tejaswinich.admitpilot.entity;

import jakarta.persistence.*;

@Entity
@Table(
    name = "college_profiles",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_profile_college_id", columnNames = {"college_id"})
    }
)
public class CollegeProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false, unique = true)
    private College college;

    @Column(name = "official_website")
    private String officialWebsite;

    @Column(name = "nirf_rank")
    private Integer nirfRank;

    @Column(name = "nirf_rank_band")
    private String nirfRankBand;

    @Column(name = "nirf_year")
    private Integer nirfYear;

    @Column(name = "nirf_category")
    private String nirfCategory;

    public CollegeProfile() {
    }

    public CollegeProfile(College college, String officialWebsite, Integer nirfRank, String nirfRankBand, Integer nirfYear, String nirfCategory) {
        this.college = college;
        this.officialWebsite = officialWebsite;
        this.nirfRank = nirfRank;
        this.nirfRankBand = nirfRankBand;
        this.nirfYear = nirfYear;
        this.nirfCategory = nirfCategory;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    public String getOfficialWebsite() {
        return officialWebsite;
    }

    public void setOfficialWebsite(String officialWebsite) {
        this.officialWebsite = officialWebsite;
    }

    public Integer getNirfRank() {
        return nirfRank;
    }

    public void setNirfRank(Integer nirfRank) {
        this.nirfRank = nirfRank;
    }

    public String getNirfRankBand() {
        return nirfRankBand;
    }

    public void setNirfRankBand(String nirfRankBand) {
        this.nirfRankBand = nirfRankBand;
    }

    public Integer getNirfYear() {
        return nirfYear;
    }

    public void setNirfYear(Integer nirfYear) {
        this.nirfYear = nirfYear;
    }

    public String getNirfCategory() {
        return nirfCategory;
    }

    public void setNirfCategory(String nirfCategory) {
        this.nirfCategory = nirfCategory;
    }
}
