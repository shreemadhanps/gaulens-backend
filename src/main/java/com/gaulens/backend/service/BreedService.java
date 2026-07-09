package com.gaulens.backend.service;

import com.gaulens.backend.dto.BreedRequest;
import com.gaulens.backend.exception.ResourceNotFoundException;
import com.gaulens.backend.model.AnimalType;
import com.gaulens.backend.model.Breed;
import com.gaulens.backend.repository.BreedRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BreedService {

    private final BreedRepository breedRepository;

    public BreedService(BreedRepository breedRepository) {
        this.breedRepository = breedRepository;
    }

    public List<Breed> getAll() {
        return breedRepository.findAll();
    }

    public Breed getById(Long id) {
        return breedRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Breed not found with id " + id));
    }

    public List<Breed> getByType(AnimalType type) {
        return breedRepository.findByType(type);
    }

    public List<Breed> search(String keyword) {
        return breedRepository.findByNameContainingIgnoreCase(keyword);
    }

    public Breed create(BreedRequest request) {
        Breed breed = new Breed();
        applyRequest(breed, request);
        return breedRepository.save(breed);
    }

    public Breed update(Long id, BreedRequest request) {
        Breed breed = getById(id);
        applyRequest(breed, request);
        return breedRepository.save(breed);
    }

    public void delete(Long id) {
        Breed breed = getById(id);
        breedRepository.delete(breed);
    }

    private void applyRequest(Breed breed, BreedRequest request) {
        breed.setName(request.getName());
        breed.setType(request.getType());
        breed.setOrigin(request.getOrigin());
        breed.setDescription(request.getDescription());
        if (request.getTraits() != null) {
            breed.setTraits(request.getTraits());
        }
    }

}
