package com.gaulens.backend.repository;

import com.gaulens.backend.model.PredictionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PredictionRecordRepository extends JpaRepository<PredictionRecord, Long> {

    List<PredictionRecord> findTop20ByOrderByScannedAtDesc();

}
