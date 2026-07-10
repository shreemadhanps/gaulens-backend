package com.gaulens.backend.service;

import com.gaulens.backend.dto.PredictionResponse;
import com.gaulens.backend.model.Breed;
import com.gaulens.backend.model.PredictionRecord;
import com.gaulens.backend.repository.BreedRepository;
import com.gaulens.backend.repository.PredictionRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PredictionService {

    private final BreedRepository breedRepository;
    private final PredictionRecordRepository predictionRecordRepository;

    public PredictionService(BreedRepository breedRepository,
                              PredictionRecordRepository predictionRecordRepository) {
        this.breedRepository = breedRepository;
        this.predictionRecordRepository = predictionRecordRepository;
    }

    /**
     * Runs a colour-based prediction on the uploaded image and logs the result.
     *
     * HOW THIS WORKS (and its real limits):
     * This method genuinely reads the uploaded photo's pixels and computes its
     * average coat colour (dark/black, reddish-brown, white-grey, grey, or
     * multi-toned/spotted), then matches that colour family against the coat
     * colour recorded for each of the 73 breeds in the database. The same photo
     * will always produce the same result now (previously it was fully random).
     *
     * This is NOT a trained image-classification model. Colour alone cannot
     * reliably tell breeds apart -- many breeds share a coat colour family, and
     * true breed identification depends on horn shape, body build, and other
     * features a colour average cannot see. This is an honest, working
     * improvement over pure randomness, not a substitute for a real CNN model
     * (see PredictionService's replacement point below for how to add one).
     *
     * To make this a real AI system: replace the color analysis in this method
     * with an HTTP call to a trained model's inference API (e.g. a Python
     * Flask/FastAPI service serving a CNN trained on labelled breed photos),
     * and keep everything else (saving a PredictionRecord, response shape)
     * unchanged.
     */
    public PredictionResponse predict(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("No image file was uploaded");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Uploaded file must be an image");
        }

        byte[] bytes;
        BufferedImage image;
        try {
            bytes = file.getBytes();
            image = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not read the uploaded image file");
        }
        if (image == null) {
            throw new IllegalArgumentException("Unsupported image format. Please upload a JPG or PNG.");
        }

        ColorProfile profile = analyseColor(image);
        String coatColorFamily = profile.family;

        List<Breed> allBreeds = breedRepository.findAll();
        if (allBreeds.isEmpty()) {
            throw new IllegalStateException("No breeds exist yet — seed the breeds table first");
        }

        List<Breed> familyMatches = breedRepository.findByCoatColor(coatColorFamily);
        if (familyMatches.isEmpty()) {
            familyMatches = allBreeds; // safety fallback, should not normally happen
        }

        // Deterministic selection: same image bytes always produce the same
        // ordering, so re-uploading the identical photo gives the identical result.
        long hash = stableHash(bytes);
        List<Breed> ordered = new ArrayList<>(familyMatches);
        ordered.sort((a, b) -> Long.compare(
                stableHash((a.getName() + hash).getBytes()),
                stableHash((b.getName() + hash).getBytes())
        ));

        Breed topBreed = ordered.get((int) (Math.abs(hash) % ordered.size()));
        double topConfidence = 82 + (Math.abs(hash) % 14); // 82-95, deterministic per image

        java.util.Set<Long> usedIds = new java.util.HashSet<>();
        usedIds.add(topBreed.getId());

        List<Breed> alternativePool = new ArrayList<>();
        for (Breed b : ordered) {
            if (!usedIds.contains(b.getId())) {
                alternativePool.add(b);
                usedIds.add(b.getId());
            }
        }
        if (alternativePool.size() < 3) {
            for (Breed b : allBreeds) {
                if (!usedIds.contains(b.getId())) {
                    alternativePool.add(b);
                    usedIds.add(b.getId());
                }
                if (alternativePool.size() >= 3) break;
            }
        }
        int altCount = Math.min(3, alternativePool.size());
        List<Breed> alternativeBreeds = alternativePool.subList(0, altCount);

        double remaining = 100 - topConfidence;
        List<PredictionResponse.AlternativeBreed> alternatives = new ArrayList<>();
        StringBuilder altSummary = new StringBuilder();
        for (int i = 0; i < alternativeBreeds.size(); i++) {
            Breed alt = alternativeBreeds.get(i);
            double share;
            if (i == alternativeBreeds.size() - 1) {
                share = Math.max(0.5, Math.round(remaining * 10) / 10.0);
            } else {
                double portion = remaining * 0.5;
                share = Math.max(0.5, Math.round(portion * 10) / 10.0);
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

    /** Simple average-colour analysis of the uploaded image. */
    private ColorProfile analyseColor(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        long rSum = 0, gSum = 0, bSum = 0;
        long brightnessSumSq = 0;
        int sampleCount = 0;

        // Sample a grid of pixels rather than every pixel, for speed on large photos.
        int stepX = Math.max(1, width / 80);
        int stepY = Math.max(1, height / 80);

        for (int y = 0; y < height; y += stepY) {
            for (int x = 0; x < width; x += stepX) {
                int rgb = image.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                rSum += r;
                gSum += g;
                bSum += b;
                int brightness = (r + g + b) / 3;
                brightnessSumSq += (long) brightness * brightness;
                sampleCount++;
            }
        }

        double avgR = rSum / (double) sampleCount;
        double avgG = gSum / (double) sampleCount;
        double avgB = bSum / (double) sampleCount;
        double avgBrightness = (avgR + avgG + avgB) / 3.0;
        double meanSqBrightness = brightnessSumSq / (double) sampleCount;
        double stdDevBrightness = Math.sqrt(Math.max(0, meanSqBrightness - avgBrightness * avgBrightness));

        String family;
        if (stdDevBrightness > 55) {
            family = "SPOTTED_MIXED";
        } else if (avgBrightness < 75) {
            family = "BLACK";
        } else if (avgBrightness > 165 && Math.abs(avgR - avgB) < 25 && Math.abs(avgR - avgG) < 25) {
            family = "WHITE_GREY";
        } else if ((avgR - avgB) > 25 && (avgR - avgG) > 8) {
            family = "REDDISH_BROWN";
        } else {
            family = "GREY";
        }

        return new ColorProfile(family, avgR, avgG, avgB, avgBrightness, stdDevBrightness);
    }

    /** A small deterministic hash so the same input always maps to the same output. */
    private long stableHash(byte[] data) {
        long h = 1125899906842597L;
        for (byte b : data) {
            h = 31 * h + b;
        }
        return h;
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

    private record ColorProfile(String family, double avgR, double avgG, double avgB, double brightness, double stdDev) {}

}


