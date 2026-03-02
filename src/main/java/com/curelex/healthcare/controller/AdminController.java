package com.curelex.healthcare.controller;

import com.curelex.healthcare.entity.Doctor;
import com.curelex.healthcare.repository.DoctorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DoctorRepo doctorRepo;

    // ================================
    // VIEW ALL PENDING DOCTORS
    // ================================
    @GetMapping("/pending-doctors")
    public ResponseEntity<?> getPendingDoctors(){

        return ResponseEntity.ok(
                doctorRepo.findByApprovalStatus("PENDING")
        );
    }

    // ================================
    // APPROVE DOCTOR
    // ================================
    @PutMapping("/approve-doctor/{id}")
    public ResponseEntity<?> approveDoctor(@PathVariable Long id){

        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setApprovalStatus("APPROVED");

        doctorRepo.save(doctor);

        return ResponseEntity.ok("Doctor approved successfully");
    }

    // ================================
    // REJECT DOCTOR
    // ================================
    @PutMapping("/reject-doctor/{id}")
    public ResponseEntity<?> rejectDoctor(@PathVariable Long id){

        Doctor doctor = doctorRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        doctor.setApprovalStatus("REJECTED");

        doctorRepo.save(doctor);

        return ResponseEntity.ok("Doctor rejected");
    }
}
