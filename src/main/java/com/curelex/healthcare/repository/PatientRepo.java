package com.curelex.healthcare.repository;

import com.curelex.healthcare.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient,Long> {
    Optional<Patient> findByEmail(String email);
}
