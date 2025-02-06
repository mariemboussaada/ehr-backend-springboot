package com.pfe.dto.response;


import com.pfe.model.Doctor;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthenticationResponse {
    private String token;
    private Doctor doctor;
    private String error;
}