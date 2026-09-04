package com.tejaswinich.admitpilot.dto;

import java.util.ArrayList;
import java.util.List;

public class CollegeProfileDTO {

    private Long id;
    private String officialWebsite;
    private Integer nirfRank;
    private String nirfRankBand;
    private Integer nirfYear;
    private String nirfCategory;
    private List<CollegePlacementDTO> placements = new ArrayList<>();

    public CollegeProfileDTO() {
    }

    public CollegeProfileDTO(Long id, String officialWebsite, Integer nirfRank, String nirfRankBand, Integer nirfYear, String nirfCategory, List<CollegePlacementDTO> placements) {
        this.id = id;
        this.officialWebsite = officialWebsite;
        this.nirfRank = nirfRank;
        this.nirfRankBand = nirfRankBand;
        this.nirfYear = nirfYear;
        this.nirfCategory = nirfCategory;
        if (placements != null) {
            this.placements = placements;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<CollegePlacementDTO> getPlacements() {
        return placements;
    }

    public void setPlacements(List<CollegePlacementDTO> placements) {
        this.placements = placements;
    }
}
