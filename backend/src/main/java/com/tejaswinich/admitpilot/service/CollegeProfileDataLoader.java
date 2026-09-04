package com.tejaswinich.admitpilot.service;

import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.entity.CollegePlacement;
import com.tejaswinich.admitpilot.entity.CollegeProfile;
import com.tejaswinich.admitpilot.repository.CollegePlacementRepository;
import com.tejaswinich.admitpilot.repository.CollegeProfileRepository;
import com.tejaswinich.admitpilot.repository.CollegeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class CollegeProfileDataLoader implements CommandLineRunner {

    private final CollegeRepository collegeRepository;
    private final CollegeProfileRepository profileRepository;
    private final CollegePlacementRepository placementRepository;

    public CollegeProfileDataLoader(
            CollegeRepository collegeRepository,
            CollegeProfileRepository profileRepository,
            CollegePlacementRepository placementRepository) {
        this.collegeRepository = collegeRepository;
        this.profileRepository = profileRepository;
        this.placementRepository = placementRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // Seed verified profile data for AUCE if profile repository is empty
        if (profileRepository.count() == 0) {
            Optional<College> auceOpt = collegeRepository.findByCollegeCode("AUCE");
            if (auceOpt.isPresent()) {
                College auce = auceOpt.get();

                CollegeProfile profile = new CollegeProfile(
                        auce,
                        "https://www.andhrauniversity.edu.in",
                        94,
                        null,
                        2024,
                        "Engineering"
                );
                profileRepository.save(profile);

                CollegePlacement placement2024 = new CollegePlacement(
                        auce,
                        2024,
                        85.0,
                        450,
                        6.8,
                        6.0,
                        18.0,
                        "https://www.andhrauniversity.edu.in/placements.html"
                );
                placementRepository.save(placement2024);

                System.out.println("CollegeProfileDataLoader: Seeded verified profile data for AUCE.");
            }
        }
    }
}
