package com.gaulens.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * A recognised indigenous cattle or buffalo breed.
 * India has 53 NBAGR-recognised cattle breeds and 20 recognised buffalo breeds;
 * this table holds whichever subset the model has been trained on.
 */
@Entity
@Table(name = "breeds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Breed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String name;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnimalType type;

    @NotBlank
    private String origin;

    @Column(length = 1000)
    private String description;

    /** Short trait tags shown as chips on the frontend, e.g. "Curved horns", "Reddish coat". */
    @ElementCollection
    @CollectionTable(name = "breed_traits", joinColumns = @JoinColumn(name = "breed_id"))
    @Column(name = "trait")
    private List<String> traits = new ArrayList<>();

}
