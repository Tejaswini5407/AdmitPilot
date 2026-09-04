package com.tejaswinich.admitpilot.dto;

import java.util.ArrayList;
import java.util.List;

public class BranchPredictionResult {

    private String branchCode;
    private List<CollegePredictionDTO> colleges = new ArrayList<>();

    public BranchPredictionResult() {
    }

    public BranchPredictionResult(String branchCode, List<CollegePredictionDTO> colleges) {
        this.branchCode = branchCode;
        this.colleges = colleges;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public List<CollegePredictionDTO> getColleges() {
        return colleges;
    }

    public void setColleges(List<CollegePredictionDTO> colleges) {
        this.colleges = colleges;
    }
}
