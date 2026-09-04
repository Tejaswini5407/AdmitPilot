package com.tejaswinich.admitpilot.dto;

import java.util.ArrayList;
import java.util.List;

public class CollegeDTO {

    private Long id;
    private String collegeCode;
    private String collegeName;
    private String type;
    private String region;
    private String district;
    private String localArea;
    private List<BranchDTO> branches = new ArrayList<>();
    private CollegeProfileDTO profile;

    public CollegeDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocalArea() {
        return localArea;
    }

    public void setLocalArea(String localArea) {
        this.localArea = localArea;
    }

    public List<BranchDTO> getBranches() {
        return branches;
    }

    public void setBranches(List<BranchDTO> branches) {
        this.branches = branches;
    }

    public CollegeProfileDTO getProfile() {
        return profile;
    }

    public void setProfile(CollegeProfileDTO profile) {
        this.profile = profile;
    }
}
