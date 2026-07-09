package com.gaulens.backend.dto;

import com.gaulens.backend.model.AnimalType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BreedRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotNull(message = "type is required (CATTLE or BUFFALO)")
    private AnimalType type;

    @NotBlank(message = "origin is required")
    private String origin;

    private String description;

    private List<String> traits;

}
