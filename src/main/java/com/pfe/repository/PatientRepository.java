package com.pfe.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.pfe.model.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String> {
    List<Patient> findByDoctor(String doctorEmail);
    @Query("{ $or: [ " +
            "{ 'nom': { $regex: ?0, $options: 'i' } }, " +
            "{ 'prenom': { $regex: ?0, $options: 'i' } } " +
            "]}")
    List<Patient> findByNameContaining(String searchTerm);
}