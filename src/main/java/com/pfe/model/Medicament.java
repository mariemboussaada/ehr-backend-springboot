package com.pfe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medicament {
    private String nom;
    private String dosage;
    private String frequence;
    private String duree;
}