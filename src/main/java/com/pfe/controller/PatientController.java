package com.pfe.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import com.pfe.model.Doctor;
import com.pfe.model.RendezVous;
import com.pfe.repository.DoctorRepository;
import com.pfe.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pfe.dto.request.PatientRequest;
import com.pfe.model.Patient;
import com.pfe.service.PatientService;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "http://localhost:4200")
public class PatientController {
    private final PatientRepository patientRepository;
    private final PatientService patientService;
    private final ObjectMapper objectMapper;

    public PatientController(
            PatientService patientService,
            ObjectMapper objectMapper,
            PatientRepository patientRepository
    ) {
        this.patientService = patientService;
        this.objectMapper = objectMapper;
        this.patientRepository = patientRepository;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addPatient(
            @RequestPart("patient") String patientJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Convertir le JSON en objet PatientRequest
            PatientRequest patientRequest = objectMapper.readValue(patientJson, PatientRequest.class);

            // L'email du médecin devrait déjà être défini dans patientRequest.doctor depuis le frontend

            // Vérifier si doctor est défini
            if (patientRequest.getDoctor() == null || patientRequest.getDoctor().isEmpty()) {
                return ResponseEntity.badRequest().body("Erreur: L'email du médecin n'est pas spécifié");
            }

            // Appeler le service pour ajouter le patient
            Patient patient = patientService.addPatient(patientRequest, imageFile);

            return ResponseEntity.ok(patient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout du patient: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients(Principal principal) {
        String doctorEmail = principal.getName();
        return ResponseEntity.ok(patientService.getPatientsByDoctor(doctorEmail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable String id) {
        try {
            Patient patient = patientService.getPatientById(id);
            return ResponseEntity.ok(patient);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable String id, Principal principal) {
        try {
            patientService.deletePatient(id, principal.getName());
            return ResponseEntity.ok("Patient supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression du patient : " + e.getMessage());
        }
    }

    @GetMapping("/{id}/documents")
    public ResponseEntity<?> getDocuments(@PathVariable String id) {
        try {
            Patient patient = patientService.getPatientById(id);
            return ResponseEntity.ok(patient.getDocuments());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur : " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(
            @RequestParam String query
    ) {
        List<Patient> patients = patientRepository.findByNameContaining(query);
        return ResponseEntity.ok(patients);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePatient(
            @PathVariable String id,
            @RequestPart("patient") String patientJson,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {

        try {
            // Convertir le JSON en objet Patient
            Patient updatedPatient = objectMapper.readValue(patientJson, Patient.class);
            updatedPatient.setId(id); // S'assurer que l'ID reste le même

            // Mettre à jour le patient
            Patient result = patientService.updatePatient(updatedPatient, imageFile);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            e.printStackTrace(); // Pour le débogage
            return ResponseEntity.badRequest().body("Erreur lors de la mise à jour du patient: " + e.getMessage());
        }
    }

    @PostMapping("/{id}/documents")
    public ResponseEntity<?> addDocumentToPatient(
            @PathVariable String id,
            @RequestParam("nomDocument") String nomDocument,
            @RequestParam("fichier") MultipartFile fichier) {

        try {
            // Vérifier si le patient existe
            Patient patient = patientService.getPatientById(id);

            // Ajouter le document au patient
            Patient updatedPatient = patientService.ajouterDocumentAuPatient(id, nomDocument, fichier);

            return ResponseEntity.ok(updatedPatient);
        } catch (Exception e) {
            e.printStackTrace(); // Pour le débogage
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout du document: " + e.getMessage());
        }
    }


}