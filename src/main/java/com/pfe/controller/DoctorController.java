package com.pfe.controller;

import com.pfe.dto.request.UpdateProfileRequest;
import com.pfe.model.Doctor;
import com.pfe.service.DoctorDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

@RestController
@RequestMapping("/api/doctor")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorDetailsService doctorService;

    @GetMapping("/get")
    public ResponseEntity<?> getDoctor(@RequestParam String email) {
        try {
            Doctor doctor = (Doctor) doctorService.loadUserByUsername(email);
            return ResponseEntity.ok(doctor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal Doctor currentDoctor,
            @RequestBody UpdateProfileRequest request) {
        try {
            Doctor updatedDoctor = doctorService.updateProfile(
                    currentDoctor.getId(),
                    request.getNom(),
                    request.getPrenom(),
                    request.getAdresse(),
                    request.getTelephone()
            );
            return ResponseEntity.ok(updatedDoctor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/update-profile-image")
    public ResponseEntity<?> updateProfileImage(
            @AuthenticationPrincipal Doctor currentDoctor,
            @RequestParam("image") MultipartFile image) {
        try {
            Doctor updatedDoctor = doctorService.updateProfileImage(currentDoctor.getId(), image);
            return ResponseEntity.ok(updatedDoctor);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


}