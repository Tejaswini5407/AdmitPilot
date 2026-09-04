package com.tejaswinich.admitpilot.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "college_placements")
public class CollegePlacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "placement_rate")
    private Double placementRate;

    @Column(name = "students_placed")
    private Integer studentsPlaced;

    @Column(name = "average_package")
    private Double averagePackage;

    @Column(name = "median_package")
    private Double medianPackage;

    @Column(name = "highest_package")
    private Double highestPackage;

    @Column(name = "source_url")
    private String sourceUrl;

    public CollegePlacement() {
    }

    public CollegePlacement(College college, Integer year, Double placementRate, Integer studentsPlaced, Double averagePackage, Double medianPackage, Double highestPackage, String sourceUrl) {
        this.college = college;
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

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
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
