package com.pfe.controller;

import com.pfe.model.Doctor;
import com.pfe.repository.DoctorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@Slf4j
public class TestController {

    private final DoctorRepository doctorRepository;

    public TestController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @GetMapping("/db-status")
    public ResponseEntity<Map<String, Object>> checkDbStatus() {
        Map<String, Object> status = new HashMap<>();
        try {
            List<Doctor> doctors = doctorRepository.findAll();
            status.put("connected", true);
            status.put("doctors", doctors);
            status.put("success", true);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("connected", false);
            status.put("error", e.getMessage());
            return ResponseEntity.status(500).body(status);
        }
    }
}