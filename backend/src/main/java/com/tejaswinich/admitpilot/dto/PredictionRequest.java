package com.tejaswinich.admitpilot.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class PredictionRequest {

    @NotNull(message = "rank is required")
    @Min(value = 1, message = "rank must be positive")
    private Integer rank;

    @NotBlank(message = "category is required")
    private String category;

    @NotBlank(message = "gender is required")
    private String gender;

    @NotEmpty(message = "branches must contain at least one branch")
    private List<String> branches;

    private Integer year;
    private String round;

    public PredictionRequest() {
    }

    public PredictionRequest(Integer rank, String category, String gender, List<String> branches) {
        this.rank = rank;
        this.category = category;
        this.gender = gender;
        this.branches = branches;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
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

    public List<String> getBranches() {
        return branches;
    }

    public void setBranches(List<String> branches) {
        this.branches = branches;
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
}
