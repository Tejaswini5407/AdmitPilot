package com.tejaswinich.admitpilot.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "branches",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_college_branch", columnNames = {"college_id", "branch_code"})
    },
    indexes = {
        @Index(name = "idx_branch_code", columnList = "branch_code")
    }
)
public class Branch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "branch_code", nullable = false)
    private String branchCode;

    @Column(name = "branch_name")
    private String branchName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @OneToMany(mappedBy = "branch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cutoff> cutoffs = new ArrayList<>();

    public Branch() {
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

    public College getCollege() {
        return college;
    }

    public void setCollege(College college) {
        this.college = college;
    }

    public List<Cutoff> getCutoffs() {
        return cutoffs;
    }

    public void setCutoffs(List<Cutoff> cutoffs) {
        this.cutoffs = cutoffs;
    }
}