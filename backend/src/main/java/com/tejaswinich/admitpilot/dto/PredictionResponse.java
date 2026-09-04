package com.tejaswinich.admitpilot.dto;

import java.util.ArrayList;
import java.util.List;

public class PredictionResponse {

    private Integer studentRank;
    private String category;
    private String gender;
    private Integer year;
    private String round;
    private List<BranchPredictionResult> results = new ArrayList<>();

    public PredictionResponse() {
    }

    public Integer getStudentRank() {
        return studentRank;
    }

    public void setStudentRank(Integer studentRank) {
        this.studentRank = studentRank;
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

    public List<BranchPredictionResult> getResults() {
        return results;
    }

    public void setResults(List<BranchPredictionResult> results) {
        this.results = results;
    }
}
