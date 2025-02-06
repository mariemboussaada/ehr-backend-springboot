package com.pfe.dto.request;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class RegisterRequest {
    private String email;
    private String nom;
    private String prenom;
    private String password;
    private String telephone;
    private String adresse;
}