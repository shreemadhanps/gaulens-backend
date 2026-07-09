package com.gaulens.backend.service;

import com.gaulens.backend.dto.PredictionResponse;
import com.gaulens.backend.model.Breed;
import com.gaulens.backend.model.PredictionRecord;
import com.gaulens.backend.repository.BreedRepository;
import com.gaulens.backend.repository.PredictionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private static final Random RANDOM = new Random();

    private final BreedRepository breedRepository;
    private final PredictionRecordRepository predictionRecordRepository;

    public PredictionService(BreedRepository breedRepository,
                              PredictionRecordRepository predictionRecordRepository) {
        this.breedRepository = breedRepository;
        this.predictionRecordRepository = predictionRecordRepository;
    }

    /**
     * Runs a "prediction" on the uploaded image and logs the result.
     *
     * NOTE: There is no trained model wired in here yet. This method picks a
     * plausible-looking result from the breeds already stored in H2, the same
     * way the frontend demo does it, so the whole stack works end-to-end.
     *
     * To make this real, replace the body of this method with a call to your
     * actual model, for example:
     *   1. Save `file` to disk or memory.
     *   2. POST the image bytes to a Python inference microservice
     *      (Flask/FastAPI serving your trained CNN) using RestTemplate or WebClient.
     *   3. Parse the returned breed name + confidence + alternatives.
     *   4. Keep the rest of this method (saving a PredictionRecord) unchanged.
     */
    public PredictionResponse predict(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No image file was uploaded");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Uploaded file must be an image");
        }

        List<Breed> allBreeds = breedRepository.findAll();
        if (allBreeds.isEmpty()) {
            throw new IllegalStateException("No breeds exist yet — seed the breeds table first");
        }

        List<Breed> shuffled = new ArrayList<>(allBreeds);
        Collections.shuffle(shuffled, RANDOM);

        Breed topBreed = shuffled.get(0);
        double topConfidence = 88 + RANDOM.nextInt(10); // 88-97

        int altCount = Math.min(3, shuffled.size() - 1);
        List<Breed> alternativeBreeds = shuffled.subList(1, 1 + altCount);

        double remaining = 100 - topConfidence;
        List<PredictionResponse.AlternativeBreed> alternatives = new ArrayList<>();
        StringBuilder altSummary = new StringBuilder();

        for (int i = 0; i < alternativeBreeds.size(); i++) {
            Breed alt = alternativeBreeds.get(i);
            double share;
            if (i == alternativeBreeds.size() - 1) {
                share = Math.max(0.5, remaining);
            } else {
                share = Math.max(0.5, Math.round(RANDOM.nextDouble() * remaining * 0.6 * 10) / 10.0);
            }
            remaining -= share;
            alternatives.add(new PredictionResponse.AlternativeBreed(alt.getName(), alt.getType().name(), share));
            if (altSummary.length() > 0) altSummary.append(",");
            altSummary.append(alt.getName()).append(":").append(share);
        }

        PredictionRecord record = new PredictionRecord();
        record.setImageName(file.getOriginalFilename());
        record.setPredictedBreed(topBreed.getName());
        record.setPredictedType(topBreed.getType());
        record.setConfidence(topConfidence);
        record.setAlternatives(altSummary.toString());
        predictionRecordRepository.save(record);

        return new PredictionResponse(
                record.getId(),
                topBreed.getName(),
                topBreed.getType().name(),
                topConfidence,
                alternatives,
                record.getScannedAt()
        );
    }

    public List<PredictionRecord> recentHistory() {
        return predictionRecordRepository.findTop20ByOrderByScannedAtDesc();
    }

    public PredictionStats stats() {
        List<PredictionRecord> all = predictionRecordRepository.findAll();
        long total = all.size();
        double avgConfidence = all.stream().mapToDouble(PredictionRecord::getConfidence).average().orElse(0);
        List<String> mostPredicted = all.stream()
                .map(PredictionRecord::getPredictedBreed)
                .distinct()
                .collect(Collectors.toList());
        return new PredictionStats(total, Math.round(avgConfidence * 10) / 10.0, mostPredicted);
    }

    public record PredictionStats(long totalScans, double averageConfidence, List<String> breedsSeen) {}

}
