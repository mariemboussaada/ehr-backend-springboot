package com.pfe.repository;

import com.pfe.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    Optional<Doctor> findByEmail(String email);
    boolean existsByEmail(String email);

    // Ajoutez cette méthode pour vérifier
    @Query(value = "{}", count = true)
    long countAll();

    List<Doctor> findAll();
}