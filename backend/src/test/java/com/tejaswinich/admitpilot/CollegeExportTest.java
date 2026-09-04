package com.tejaswinich.admitpilot;

import com.tejaswinich.admitpilot.entity.College;
import com.tejaswinich.admitpilot.repository.CollegeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootTest
public class CollegeExportTest {

    @Autowired
    private CollegeRepository collegeRepository;

    @Test
    void exportCollegesToCsv() throws Exception {
        List<College> colleges = collegeRepository.findAll();
        File outputDir = new File("C:/Users/tejac/OneDrive/Desktop/CollegePredictor/collector");
        if (!outputDir.exists()) {
            outputDir.mkdirs();
        }

        File outputFile = new File(outputDir, "colleges_input.csv");
        Set<String> seenCodes = new HashSet<>();

        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
            writer.println("college_code,college_name,type,region,district,local_area");
            for (College c : colleges) {
                if (c.getCollegeCode() != null && seenCodes.add(c.getCollegeCode())) {
                    writer.printf("%s,\"%s\",%s,%s,%s,%s%n",
                            c.getCollegeCode(),
                            c.getCollegeName() != null ? c.getCollegeName().replace("\"", "\"\"") : "",
                            c.getType() != null ? c.getType() : "",
                            c.getRegion() != null ? c.getRegion() : "",
                            c.getDistrict() != null ? c.getDistrict() : "",
                            c.getLocalArea() != null ? c.getLocalArea() : ""
                    );
                }
            }
        }
        System.out.println("CollegeExportTest: Exported " + seenCodes.size() + " unique colleges to " + outputFile.getAbsolutePath());
    }
}
