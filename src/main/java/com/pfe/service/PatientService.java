package com.pfe.service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.pfe.model.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pfe.dto.request.PatientRequest;
import com.pfe.repository.DoctorRepository;
import com.pfe.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public PatientService(PatientRepository patientRepository, DoctorRepository doctorRepository) {
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public Patient addPatient(PatientRequest request, MultipartFile imageFile) throws IOException {
        // Créer un nouveau patient avec les champs obligatoires
        Patient patient = Patient.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .genre(request.getGenre())
                .doctor(request.getDoctor()) // Email du médecin
                .rendezVous(new ArrayList<>())
                .prescriptions(new ArrayList<>())
                .build();

        // Ajouter le rendez-vous initial si fourni
        if (request.getRendezVousInitial() != null) {
            patient.getRendezVous().add(request.getRendezVousInitial());
        }

        // Ajouter la prescription initiale si fournie
        if (request.getPrescriptionInitiale() != null) {
            patient.getPrescriptions().add(request.getPrescriptionInitiale());
        }

        // Ajouter les champs optionnels s'ils sont fournis
        if (request.getTaille() != null) {
            patient.setTaille(request.getTaille());
        }

        if (request.getPoids() != null) {
            patient.setPoids(request.getPoids());
        }

        if (request.getGroupeSanguin() != null) {
            patient.setGroupeSanguin(request.getGroupeSanguin());
        }

        if (request.getAllergies() != null && !request.getAllergies().isEmpty()) {
            patient.setAllergies(request.getAllergies());
        }

        if (request.getAntecedents() != null && !request.getAntecedents().isEmpty()) {
            patient.setAntecedents(request.getAntecedents());
        }

        if (request.getDonneesSupplementaires() != null && !request.getDonneesSupplementaires().isEmpty()) {
            patient.setDonneesSupplementaires(request.getDonneesSupplementaires());
        }

        // Traiter l'image du patient si fournie
        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
            patient.setImage(base64Image);
        }

        // Sauvegarder le patient
        Patient savedPatient = patientRepository.save(patient);

        // Mettre à jour la liste des patients du médecin
        Doctor doctor = doctorRepository.findByEmail(request.getDoctor())
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        doctor.getPatientIds().add(savedPatient.getId());
        doctorRepository.save(doctor);

        return savedPatient;
    }

    public List<Patient> getPatientsByDoctor(String doctorEmail) {
        return patientRepository.findByDoctor(doctorEmail);
    }

    public Patient getPatientById(String id) {
        return patientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient non trouvé"));
    }

    public void deletePatient(String id, String doctorEmail) {
        Patient patient = getPatientById(id);

        // Vérifier que le patient appartient bien au médecin
        if (!patient.getDoctor().equals(doctorEmail)) {
            throw new RuntimeException("Non autorisé à supprimer ce patient");
        }

        // Supprimer l'ID du patient de la liste du médecin
        Doctor doctor = doctorRepository.findByEmail(doctorEmail)
                .orElseThrow(() -> new RuntimeException("Médecin non trouvé"));

        doctor.getPatientIds().remove(id);
        doctorRepository.save(doctor);

        // Supprimer le patient
        patientRepository.deleteById(id);
    }

    public Patient ajouterDocumentAuPatient(String patientId, String nomDocument, MultipartFile fichier) throws IOException {
        Patient patient = getPatientById(patientId);

        // Convertir le fichier en Base64
        String contenuBase64 = Base64.getEncoder().encodeToString(fichier.getBytes());

        // Créer le document médical
        DocumentMedical document = DocumentMedical.builder()
                .nom(nomDocument)
                .type(fichier.getContentType())
                .dateUpload(new Date())
                .contenu(contenuBase64)
                .build();

        // Ajouter le document à la liste des documents du patient
        patient.getDocuments().add(document);

        // Sauvegarder le patient
        return patientRepository.save(patient);
    }

    public List<Patient> searchPatients(String query) {
        return patientRepository.findByNameContaining(query);
    }

    public Patient updatePatient(Patient updatedPatient, MultipartFile imageFile) throws IOException {
        // Récupérer le patient existant
        Patient existingPatient = getPatientById(updatedPatient.getId());

        // Mettre à jour les champs
        existingPatient.setNom(updatedPatient.getNom());
        existingPatient.setPrenom(updatedPatient.getPrenom());
        existingPatient.setDateNaissance(updatedPatient.getDateNaissance());
        existingPatient.setGenre(updatedPatient.getGenre());

        // Mettre à jour les autres champs
        existingPatient.setTaille(updatedPatient.getTaille());
        existingPatient.setPoids(updatedPatient.getPoids());
        existingPatient.setGroupeSanguin(updatedPatient.getGroupeSanguin());
        existingPatient.setAllergies(updatedPatient.getAllergies());
        existingPatient.setAntecedents(updatedPatient.getAntecedents());
        existingPatient.setRendezVous(updatedPatient.getRendezVous());
        existingPatient.setPrescriptions(updatedPatient.getPrescriptions());
        existingPatient.setDonneesSupplementaires(updatedPatient.getDonneesSupplementaires());

        // Si une nouvelle image est fournie, la mettre à jour
        if (imageFile != null && !imageFile.isEmpty()) {
            String base64Image = Base64.getEncoder().encodeToString(imageFile.getBytes());
            existingPatient.setImage(base64Image);
        }

        // Sauvegarder et retourner le patient mis à jour
        return patientRepository.save(existingPatient);
    }


}