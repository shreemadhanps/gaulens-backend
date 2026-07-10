package com.gaulens.backend.repository;

import com.gaulens.backend.model.AnimalType;
import com.gaulens.backend.model.Breed;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BreedRepository extends JpaRepository<Breed, Long> {

    List<Breed> findByType(AnimalType type);

    List<Breed> findByCoatColor(String coatColor);

    Optional<Breed> findByNameIgnoreCase(String name);

    List<Breed> findByNameContainingIgnoreCase(String keyword);

}
