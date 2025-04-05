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
public class RendezVous {
    private Date date;
    private String heure;
    private String motif;
    private String notes;
}