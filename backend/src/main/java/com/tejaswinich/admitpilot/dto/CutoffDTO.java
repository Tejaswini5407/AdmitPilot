package com.tejaswinich.admitpilot.dto;

public class CutoffDTO {

    private Long id;
    private String category;
    private String gender;
    private Integer closingRank;
    private Integer year;
    private String round;
    private Long branchId;
    private String branchCode;

    public CutoffDTO() {
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

    public Long getBranchId() {
        return branchId;
    }

    public void setBranchId(Long branchId) {
        this.branchId = branchId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }
}
