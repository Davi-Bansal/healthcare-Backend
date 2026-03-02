package com.curelex.healthcare.controller;

import com.curelex.healthcare.entity.Patient;
import com.curelex.healthcare.repository.PatientRepo;
import com.curelex.healthcare.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService service;
    private final PatientRepo patientRepo;

    @GetMapping("/patient/{id}")
    public Object getPatientDashboard(@PathVariable Long id){
        return service.getDashboard(id);
    }

    @GetMapping("/patient-info/{id}")
    public Patient getPatient(@PathVariable Long id){
        return patientRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
    }
}
