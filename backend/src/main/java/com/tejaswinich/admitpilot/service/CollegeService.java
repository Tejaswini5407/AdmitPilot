package com.tejaswinich.admitpilot.service;

import com.tejaswinich.admitpilot.dto.CollegeDTO;
import com.tejaswinich.admitpilot.dto.CollegeProfileDTO;
import com.tejaswinich.admitpilot.dto.EntityMapper;
import com.tejaswinich.admitpilot.entity.Branch;
import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.entity.CollegePlacement;
import com.tejaswinich.admitpilot.entity.CollegeProfile;
import com.tejaswinich.admitpilot.repository.BranchRepository;
import com.tejaswinich.admitpilot.repository.CollegePlacementRepository;
import com.tejaswinich.admitpilot.repository.CollegeProfileRepository;
import com.tejaswinich.admitpilot.repository.CollegeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CollegeService {

    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final CollegeProfileRepository profileRepository;
    private final CollegePlacementRepository placementRepository;

    public CollegeService(
            CollegeRepository collegeRepository,
            BranchRepository branchRepository,
            CollegeProfileRepository profileRepository,
            CollegePlacementRepository placementRepository) {
        this.collegeRepository = collegeRepository;
        this.branchRepository = branchRepository;
        this.profileRepository = profileRepository;
        this.placementRepository = placementRepository;
    }

    @Transactional
    public CollegeDTO saveCollege(CollegeDTO collegeDTO) {
        College college = EntityMapper.toCollegeEntity(collegeDTO);
        College saved = collegeRepository.save(college);
        return EntityMapper.toCollegeDTO(saved, true, true);
    }

    @Transactional(readOnly = true)
    public List<CollegeDTO> getAllColleges() {
        return collegeRepository.findAll().stream()
                .map(c -> EntityMapper.toCollegeDTO(c, true, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CollegeDTO getCollegeById(Long id) {
        College college = collegeRepository.findById(id).orElse(null);
        if (college == null) {
            return null;
        }
        List<Branch> branchesWithCutoffs = branchRepository.findWithCutoffsByCollege(college);
        CollegeDTO dto = EntityMapper.toCollegeDTO(college, false, false);
        if (branchesWithCutoffs != null) {
            dto.setBranches(
                branchesWithCutoffs.stream()
                    .map(b -> EntityMapper.toBranchDTO(b, true))
                    .collect(Collectors.toList())
            );
        }
        attachProfileAndPlacements(dto, college.getId());
        return dto;
    }

    @Transactional(readOnly = true)
    public CollegeDTO getCollegeByCode(String code) {
        College college = collegeRepository.findByCollegeCode(code).orElse(null);
        if (college == null) {
            return null;
        }
        List<Branch> branchesWithCutoffs = branchRepository.findWithCutoffsByCollege(college);
        CollegeDTO dto = EntityMapper.toCollegeDTO(college, false, false);
        if (branchesWithCutoffs != null) {
            dto.setBranches(
                branchesWithCutoffs.stream()
                    .map(b -> EntityMapper.toBranchDTO(b, true))
                    .collect(Collectors.toList())
            );
        }
        attachProfileAndPlacements(dto, college.getId());
        return dto;
    }

    private void attachProfileAndPlacements(CollegeDTO dto, Long collegeId) {
        Optional<CollegeProfile> profileOpt = profileRepository.findByCollegeId(collegeId);
        List<CollegePlacement> placements = placementRepository.findByCollegeIdOrderByYearDesc(collegeId);

        CollegeProfile profile = profileOpt.orElse(null);
        CollegeProfileDTO profileDTO = EntityMapper.toCollegeProfileDTO(profile, placements);
        dto.setProfile(profileDTO);
    }

    @Transactional
    public CollegeDTO updateCollege(Long id, CollegeDTO updatedDTO) {
        College existingCollege = collegeRepository.findById(id).orElse(null);
        if (existingCollege != null) {
            existingCollege.setCollegeName(updatedDTO.getCollegeName());
            existingCollege.setCollegeCode(updatedDTO.getCollegeCode());
            existingCollege.setType(updatedDTO.getType());
            existingCollege.setRegion(updatedDTO.getRegion());
            existingCollege.setDistrict(updatedDTO.getDistrict());
            existingCollege.setLocalArea(updatedDTO.getLocalArea());

            College saved = collegeRepository.save(existingCollege);
            return EntityMapper.toCollegeDTO(saved, true, true);
        }
        return null;
    }

    @Transactional
    public String deleteCollege(Long id) {
        if (collegeRepository.existsById(id)) {
            collegeRepository.deleteById(id);
            return "College Deleted Successfully";
        }
        return "College Not Found";
    }

    @Transactional(readOnly = true)
    public List<CollegeDTO> getByCollegeName(String collegeName) {
        return collegeRepository.findByCollegeNameContainingIgnoreCase(collegeName).stream()
                .map(c -> EntityMapper.toCollegeDTO(c, false, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollegeDTO> getByDistrict(String district) {
        return collegeRepository.findByDistrict(district).stream()
                .map(c -> EntityMapper.toCollegeDTO(c, false, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollegeDTO> getByType(String type) {
        return collegeRepository.findByType(type).stream()
                .map(c -> EntityMapper.toCollegeDTO(c, false, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CollegeDTO> getByRegion(String region) {
        return collegeRepository.findByRegion(region).stream()
                .map(c -> EntityMapper.toCollegeDTO(c, false, false))
                .collect(Collectors.toList());
    }
}