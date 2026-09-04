package com.tejaswinich.admitpilot.dto;

public class CollegePlacementDTO {

    private Long id;
    private Integer year;
    private Double placementRate;
    private Integer studentsPlaced;
    private Double averagePackage;
    private Double medianPackage;
    private Double highestPackage;
    private String sourceUrl;

    public CollegePlacementDTO() {
    }

    public CollegePlacementDTO(Long id, Integer year, Double placementRate, Integer studentsPlaced, Double averagePackage, Double medianPackage, Double highestPackage, String sourceUrl) {
        this.id = id;
        this.year = year;
        this.placementRate = placementRate;
        this.studentsPlaced = studentsPlaced;
        this.averagePackage = averagePackage;
        this.medianPackage = medianPackage;
        this.highestPackage = highestPackage;
        this.sourceUrl = sourceUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Double getPlacementRate() {
        return placementRate;
    }

    public void setPlacementRate(Double placementRate) {
        this.placementRate = placementRate;
    }

    public Integer getStudentsPlaced() {
        return studentsPlaced;
    }

    public void setStudentsPlaced(Integer studentsPlaced) {
        this.studentsPlaced = studentsPlaced;
    }

    public Double getAveragePackage() {
        return averagePackage;
    }

    public void setAveragePackage(Double averagePackage) {
        this.averagePackage = averagePackage;
    }

    public Double getMedianPackage() {
        return medianPackage;
    }

    public void setMedianPackage(Double medianPackage) {
        this.medianPackage = medianPackage;
    }

    public Double getHighestPackage() {
        return highestPackage;
    }

    public void setHighestPackage(Double highestPackage) {
        this.highestPackage = highestPackage;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
