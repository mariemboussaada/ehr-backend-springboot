package com.pfe.service;

import com.pfe.model.Doctor;
import com.pfe.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class DoctorDetailsService implements UserDetailsService {

    private final DoctorRepository doctorRepository;

    public DoctorDetailsService(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return doctorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Doctor not found with email: " + email));
    }

    public Doctor updateProfile(String doctorId, String nom, String prenom, String adresse, String telephone) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setNom(nom);
        doctor.setPrenom(prenom);
        doctor.setAdresse(adresse);
        doctor.setTelephone(telephone);

        return doctorRepository.save(doctor);
    }

    public Doctor updateProfileImage(String doctorId, MultipartFile image) throws IOException {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Convertir l'image en Base64
        String base64Image = Base64.getEncoder().encodeToString(image.getBytes());
        doctor.setImage(base64Image);

        return doctorRepository.save(doctor);
    }
}