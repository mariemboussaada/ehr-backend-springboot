package com.pfe.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "patient")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    private String id;

    // Champs obligatoires
    private String nom;
    private String prenom;

    private String dateNaissance;
    private String genre;
    private String doctor; // Email du médecin
    private String image;

    // Rendez-vous (obligatoire mais peut être vide au début)
    @Builder.Default
    @Field("rendez_vous")
    private List<RendezVous> rendezVous = new ArrayList<>();

    // Prescriptions (obligatoire mais peut être vide au début)
    @Builder.Default
    private List<Prescription> prescriptions = new ArrayList<>();

    // Champs optionnels
    private Integer taille;
    private Integer poids;
    private String groupeSanguin;

    @Builder.Default
    private List<String> allergies = new ArrayList<>();

    @Builder.Default
    private List<String> antecedents = new ArrayList<>();

    // Map pour les champs optionnels supplémentaires
    @Builder.Default
    private Map<String, Object> donneesSupplementaires = new HashMap<>();

    // Références aux documents PDF
    @Builder.Default
    private List<DocumentMedical> documents = new ArrayList<>();
}