package com.pfe.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UpdateProfileRequest {
    private String nom;
    private String prenom;
    private String adresse;
    private String telephone;
}