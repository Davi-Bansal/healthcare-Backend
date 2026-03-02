package com.curelex.healthcare.repository;

import com.curelex.healthcare.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordRepo extends JpaRepository<MedicalRecord,Long> {

    List<MedicalRecord> findByPatientId(Long patientId);
}
