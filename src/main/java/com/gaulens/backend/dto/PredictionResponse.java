package com.gaulens.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class PredictionResponse {

    private Long predictionId;
    private String predictedBreed;
    private String predictedType;
    private Double confidence;
    private List<AlternativeBreed> alternatives;
    private LocalDateTime scannedAt;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class AlternativeBreed {
        private String name;
        private String type;
        private Double confidence;
    }

}
