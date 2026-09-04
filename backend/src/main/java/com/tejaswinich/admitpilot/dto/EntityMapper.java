package com.tejaswinich.admitpilot.dto;

import com.tejaswinich.admitpilot.entity.Branch;
import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.entity.CollegePlacement;
import com.tejaswinich.admitpilot.entity.CollegeProfile;
import com.tejaswinich.admitpilot.entity.Cutoff;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class EntityMapper {

    public static CollegeDTO toCollegeDTO(College college, boolean includeBranches) {
        return toCollegeDTO(college, includeBranches, false);
    }

    public static CollegeDTO toCollegeDTO(College college, boolean includeBranches, boolean includeCutoffs) {
        if (college == null) {
            return null;
        }

        CollegeDTO dto = new CollegeDTO();
        dto.setId(college.getId());
        dto.setCollegeCode(college.getCollegeCode());
        dto.setCollegeName(college.getCollegeName());
        dto.setType(college.getType());
        dto.setRegion(college.getRegion());
        dto.setDistrict(college.getDistrict());
        dto.setLocalArea(college.getLocalArea());

        if (includeBranches && college.getBranches() != null) {
            dto.setBranches(
                college.getBranches().stream()
                    .map(b -> toBranchDTO(b, includeCutoffs))
                    .collect(Collectors.toList())
            );
        } else {
            dto.setBranches(Collections.emptyList());
        }

        return dto;
    }

    public static BranchDTO toBranchDTO(Branch branch, boolean includeCutoffs) {
        if (branch == null) {
            return null;
        }

        BranchDTO dto = new BranchDTO();
        dto.setId(branch.getId());
        dto.setBranchCode(branch.getBranchCode());
        dto.setBranchName(branch.getBranchName());

        if (branch.getCollege() != null) {
            dto.setCollegeId(branch.getCollege().getId());
            dto.setCollegeCode(branch.getCollege().getCollegeCode());
        }

        if (includeCutoffs && branch.getCutoffs() != null) {
            dto.setCutoffs(
                branch.getCutoffs().stream()
                    .map(EntityMapper::toCutoffDTO)
                    .collect(Collectors.toList())
            );
        } else {
            dto.setCutoffs(Collections.emptyList());
        }

        return dto;
    }

    public static CutoffDTO toCutoffDTO(Cutoff cutoff) {
        if (cutoff == null) {
            return null;
        }

        CutoffDTO dto = new CutoffDTO();
        dto.setId(cutoff.getId());
        dto.setCategory(cutoff.getCategory());
        dto.setGender(cutoff.getGender());
        dto.setClosingRank(cutoff.getClosingRank());
        dto.setYear(cutoff.getYear());
        dto.setRound(cutoff.getRound());

        if (cutoff.getBranch() != null) {
            dto.setBranchId(cutoff.getBranch().getId());
            dto.setBranchCode(cutoff.getBranch().getBranchCode());
        }

        return dto;
    }

    public static CollegeProfileDTO toCollegeProfileDTO(CollegeProfile profile, List<CollegePlacement> placements) {
        if (profile == null && (placements == null || placements.isEmpty())) {
            return null;
        }

        CollegeProfileDTO dto = new CollegeProfileDTO();
        if (profile != null) {
            dto.setId(profile.getId());
            dto.setOfficialWebsite(profile.getOfficialWebsite());
            dto.setNirfRank(profile.getNirfRank());
            dto.setNirfRankBand(profile.getNirfRankBand());
            dto.setNirfYear(profile.getNirfYear());
            dto.setNirfCategory(profile.getNirfCategory());
        }

        if (placements != null && !placements.isEmpty()) {
            dto.setPlacements(
                placements.stream()
                    .map(EntityMapper::toCollegePlacementDTO)
                    .collect(Collectors.toList())
            );
        } else {
            dto.setPlacements(Collections.emptyList());
        }

        return dto;
    }

    public static CollegePlacementDTO toCollegePlacementDTO(CollegePlacement placement) {
        if (placement == null) {
            return null;
        }

        CollegePlacementDTO dto = new CollegePlacementDTO();
        dto.setId(placement.getId());
        dto.setYear(placement.getYear());
        dto.setPlacementRate(placement.getPlacementRate());
        dto.setStudentsPlaced(placement.getStudentsPlaced());
        dto.setAveragePackage(placement.getAveragePackage());
        dto.setMedianPackage(placement.getMedianPackage());
        dto.setHighestPackage(placement.getHighestPackage());
        dto.setSourceUrl(placement.getSourceUrl());
        return dto;
    }

    public static College toCollegeEntity(CollegeDTO dto) {
        if (dto == null) {
            return null;
        }

        College college = new College();
        college.setId(dto.getId());
        college.setCollegeCode(dto.getCollegeCode());
        college.setCollegeName(dto.getCollegeName());
        college.setType(dto.getType());
        college.setRegion(dto.getRegion());
        college.setDistrict(dto.getDistrict());
        college.setLocalArea(dto.getLocalArea());
        return college;
    }
}
