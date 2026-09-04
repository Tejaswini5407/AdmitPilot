package com.tejaswinich.admitpilot.controller;

import com.tejaswinich.admitpilot.dto.PredictionRequest;
import com.tejaswinich.admitpilot.dto.PredictionResponse;
import com.tejaswinich.admitpilot.service.PredictionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/predict")
    public ResponseEntity<?> predictColleges(@Valid @RequestBody PredictionRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("status", HttpStatus.BAD_REQUEST.value());
            errorResponse.put("error", "Bad Request");

            Map<String, String> fieldErrors = bindingResult.getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            FieldError::getField,
                            err -> err.getDefaultMessage() != null ? err.getDefaultMessage() : "Invalid value",
                            (existing, replacement) -> existing
                    ));

            errorResponse.put("message", "Validation failed for prediction request");
            errorResponse.put("errors", fieldErrors);

            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Additional manual sanity validation
        if (request.getRank() == null || request.getRank() <= 0) {
            return ResponseEntity.badRequest().body(createErrorMap("rank must be a positive integer"));
        }

        if (request.getCategory() == null || request.getCategory().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorMap("category is required"));
        }

        if (request.getGender() == null || request.getGender().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorMap("gender is required"));
        }

        if (request.getBranches() == null || request.getBranches().isEmpty()) {
            return ResponseEntity.badRequest().body(createErrorMap("branches must contain at least one branch"));
        }

        PredictionResponse response = predictionService.predict(request);
        return ResponseEntity.ok(response);
    }

    private Map<String, Object> createErrorMap(String message) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.BAD_REQUEST.value());
        map.put("error", "Bad Request");
        map.put("message", message);
        return map;
    }
}
