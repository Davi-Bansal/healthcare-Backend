package com.curelex.healthcare.repository;
import java.util.List;
import java.util.Optional;

import com.curelex.healthcare.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepo extends JpaRepository<Doctor,Long> {
    List<Doctor> findByApprovalStatus(String status);
    Optional<Doctor> findByEmail(String email);

}
