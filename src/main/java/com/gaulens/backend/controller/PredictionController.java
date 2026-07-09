package com.gaulens.backend.controller;

import com.gaulens.backend.dto.PredictionResponse;
import com.gaulens.backend.model.PredictionRecord;
import com.gaulens.backend.service.PredictionService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    /**
     * POST /api/predict
     * multipart/form-data with a single field named "image".
     * Matches the fetch call your frontend's runScan() should make once it's wired to this backend.
     */
    @PostMapping(value = "/predict", consumes = "multipart/form-data")
    public PredictionResponse predict(@RequestParam("image") MultipartFile image) {
        return predictionService.predict(image);
    }

    /** GET /api/predictions — most recent 20 scans */
    @GetMapping("/predictions")
    public List<PredictionRecord> history() {
        return predictionService.recentHistory();
    }

    /** GET /api/predictions/stats — quick summary for a dashboard */
    @GetMapping("/predictions/stats")
    public PredictionService.PredictionStats stats() {
        return predictionService.stats();
    }

}
