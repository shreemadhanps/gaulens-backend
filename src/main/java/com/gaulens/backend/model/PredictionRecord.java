package com.gaulens.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * A logged result of one breed-recognition scan.
 * Every call to POST /api/predict creates one row here, so the app has a
 * history of what was scanned, what was predicted, and how confident the model was.
 */
@Entity
@Table(name = "prediction_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PredictionRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageName;

    @Column(nullable = false)
    private String predictedBreed;

    @Enumerated(EnumType.STRING)
    private AnimalType predictedType;

    @Column(nullable = false)
    private Double confidence;

    /** Comma separated "Name:confidence" pairs for the runner-up breeds, e.g. "Sahiwal:6.0,Tharparkar:2.0" */
    @Column(length = 500)
    private String alternatives;

    @Column(nullable = false)
    private LocalDateTime scannedAt;

    @PrePersist
    public void prePersist() {
        if (scannedAt == null) {
            scannedAt = LocalDateTime.now();
        }
    }

}
