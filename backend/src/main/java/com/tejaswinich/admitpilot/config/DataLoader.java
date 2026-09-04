package com.tejaswinich.admitpilot.config;

import com.tejaswinich.admitpilot.service.ExcelImportService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    private final ExcelImportService excelImportService;

    @Value("${app.import.enabled:false}")
    private boolean importEnabled;

    public DataLoader(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @Override
    public void run(String... args) {
        if (!importEnabled) {
            System.out.println("DataLoader: Automatic Excel import is DISABLED (default).");
            return;
        }

        System.out.println("DataLoader: Automatic Excel import is ENABLED.");
        System.out.println("DataLoader: Starting safe non-destructive import...");
        excelImportService.importExcel();
        System.out.println("DataLoader: Automatic import completed successfully.");
    }
}