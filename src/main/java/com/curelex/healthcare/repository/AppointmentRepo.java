package com.curelex.healthcare.repository;

import com.curelex.healthcare.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

    // For booking conflict check
    List<Appointment> findByDoctorIdAndAppointmentDateBetween(
            Long doctorId,
            LocalDateTime start,
            LocalDateTime end
    );

    // Upcoming doctor appointments
    List<Appointment> findByDoctorIdAndAppointmentDateAfterOrderByAppointmentDate(
            Long doctorId,
            LocalDateTime date
    );

    // Upcoming patient appointments
    List<Appointment> findByPatientIdAndAppointmentDateAfterOrderByAppointmentDate(
            Long patientId,
            LocalDateTime date
    );

    // ⭐ REQUIRED FOR CONTROLLER
    List<Appointment> findByPatientId(Long patientId);

    List<Appointment> findByDoctorId(Long doctorId);

    List<Appointment> findByStatus(String status);

    List<Appointment> findByPatientIdAndStatus(
            Long patientId,
            String status
    );


}
