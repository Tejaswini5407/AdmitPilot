package com.tejaswinich.admitpilot.dto;

import java.util.ArrayList;
import java.util.List;

public class BranchDTO {

    private Long id;
    private String branchCode;
    private String branchName;
    private Long collegeId;
    private String collegeCode;
    private List<CutoffDTO> cutoffs = new ArrayList<>();

    public BranchDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Long getCollegeId() {
        return collegeId;
    }

    public void setCollegeId(Long collegeId) {
        this.collegeId = collegeId;
    }

    public String getCollegeCode() {
        return collegeCode;
    }

    public void setCollegeCode(String collegeCode) {
        this.collegeCode = collegeCode;
    }

    public List<CutoffDTO> getCutoffs() {
        return cutoffs;
    }

    public void setCutoffs(List<CutoffDTO> cutoffs) {
        this.cutoffs = cutoffs;
    }
}
