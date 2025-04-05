package com.pfe.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentMedical {
    private String nom;
    private String type;
    private Date dateUpload;
    private String contenu; // Contenu encodé en Base64
}