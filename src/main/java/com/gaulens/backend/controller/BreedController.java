package com.gaulens.backend.controller;

import com.gaulens.backend.dto.BreedRequest;
import com.gaulens.backend.model.AnimalType;
import com.gaulens.backend.model.Breed;
import com.gaulens.backend.service.BreedService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/breeds")
public class BreedController {

    private final BreedService breedService;

    public BreedController(BreedService breedService) {
        this.breedService = breedService;
    }

    /** GET /api/breeds — list every breed, or GET /api/breeds?type=CATTLE / ?search=gir */
    @GetMapping
    public List<Breed> getBreeds(
            @RequestParam(required = false) AnimalType type,
            @RequestParam(required = false) String search) {
        if (search != null && !search.isBlank()) {
            return breedService.search(search);
        }
        if (type != null) {
            return breedService.getByType(type);
        }
        return breedService.getAll();
    }

    /** GET /api/breeds/{id} */
    @GetMapping("/{id}")
    public Breed getBreed(@PathVariable Long id) {
        return breedService.getById(id);
    }

    /** POST /api/breeds — add a new breed */
    @PostMapping
    public ResponseEntity<Breed> createBreed(@Valid @RequestBody BreedRequest request) {
        Breed created = breedService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /** PUT /api/breeds/{id} — update an existing breed */
    @PutMapping("/{id}")
    public Breed updateBreed(@PathVariable Long id, @Valid @RequestBody BreedRequest request) {
        return breedService.update(id, request);
    }

    /** DELETE /api/breeds/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBreed(@PathVariable Long id) {
        breedService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
