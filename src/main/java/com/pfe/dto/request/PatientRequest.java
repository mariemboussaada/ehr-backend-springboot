package com.pfe.dto.request;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pfe.model.RendezVous;
import com.pfe.model.Prescription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientRequest {
    // Champs obligatoires
    private String nom;
    private String prenom;
    private String dateNaissance;
    private String genre;
    private String doctor;

    private RendezVous rendezVousInitial;

    private Prescription prescriptionInitiale;

    private Integer taille;
    private Integer poids;
    private String groupeSanguin;
    private List<String> allergies;
    private List<String> antecedents;

    private Map<String, Object> donneesSupplementaires;


}