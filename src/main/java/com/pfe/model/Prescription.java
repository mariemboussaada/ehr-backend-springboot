package com.pfe.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Prescription {
    private Date date;
    @Builder.Default
    private List<Medicament> medicaments = new ArrayList<>();
}