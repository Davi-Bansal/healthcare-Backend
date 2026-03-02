package com.curelex.healthcare.service;

import com.curelex.healthcare.dto.AppointmentRequest;
import com.curelex.healthcare.entity.*;
import com.curelex.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepo appointmentRepo;
    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    private final MedicalRecordRepo medicalRecordRepo;   // ⭐ NEW
    private final EmailService emailService;


    // =========================
    // BOOK
    // =========================
    @Transactional
    public Appointment bookAppointment(AppointmentRequest request) {

        Patient patient = patientRepo.findById(request.getPatientId())
                .orElseThrow(() -> new RuntimeException("Patient not found"));

        Doctor doctor = doctorRepo.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        if (!"APPROVED".equalsIgnoreCase(doctor.getApprovalStatus()))
            throw new RuntimeException("Doctor is not approved yet");

        if (request.getAppointmentDate() == null ||
                request.getAppointmentDate().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Appointment date must be in the future");

        List<Appointment> conflicts =
                appointmentRepo.findByDoctorIdAndAppointmentDateBetween(
                        doctor.getId(),
                        request.getAppointmentDate().minusHours(1),
                        request.getAppointmentDate().plusHours(1)
                );

        if (!conflicts.isEmpty())
            throw new RuntimeException("Doctor already has an appointment at this time");

        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setAppointmentDate(request.getAppointmentDate());
        appointment.setReason(request.getReason());
        appointment.setStatus("PENDING");

        Appointment saved = appointmentRepo.save(appointment);

        try { emailService.sendAppointmentBookedEmail(saved); } catch (Exception ignored){}

        return saved;
    }


    // =========================
    // CONFIRM
    // =========================
    @Transactional
    public Appointment confirmAppointment(Long id) {

        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!"PENDING".equalsIgnoreCase(appointment.getStatus()))
            throw new RuntimeException("Only pending appointments can be confirmed");

        appointment.setStatus("CONFIRMED");

        Appointment saved = appointmentRepo.save(appointment);

        try { emailService.sendAppointmentConfirmedEmail(saved); } catch (Exception ignored){}

        return saved;
    }


    // =========================
    // ⭐ COMPLETE + AUTO MEDICAL RECORD
    // =========================
    @Transactional
    public Appointment completeAppointment(Long id, String notes, String prescription) {

        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (!"CONFIRMED".equalsIgnoreCase(appointment.getStatus()))
            throw new RuntimeException("Only confirmed appointments can be completed");

        appointment.setStatus("COMPLETED");
        appointment.setNotes(notes);
        appointment.setPrescription(prescription);

        Appointment saved = appointmentRepo.save(appointment);


        MedicalRecord record = new MedicalRecord();
        record.setPatient(saved.getPatient());
        record.setDoctor(saved.getDoctor());
        record.setDiagnosis(saved.getReason());
        record.setPrescription(prescription);
        record.setNotes(notes);

        medicalRecordRepo.save(record);

        return saved;
    }

    @Transactional
    public Appointment cancelAppointment(Long id, String reason) {

        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if ("COMPLETED".equalsIgnoreCase(appointment.getStatus()))
            throw new RuntimeException("Completed appointments cannot be cancelled");

        appointment.setStatus("CANCELLED");
        appointment.setCancellationReason(reason);

        Appointment saved = appointmentRepo.save(appointment);

        try { emailService.sendAppointmentCancelledEmail(saved); } catch (Exception ignored){}

        return saved;
    }

    @Transactional
    public Appointment rescheduleAppointment(Long id, LocalDateTime newDate) {

        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if ("COMPLETED".equalsIgnoreCase(appointment.getStatus())
                || "CANCELLED".equalsIgnoreCase(appointment.getStatus()))
            throw new RuntimeException("Cannot reschedule completed or cancelled appointments");

        if (newDate == null || newDate.isBefore(LocalDateTime.now()))
            throw new RuntimeException("New appointment date must be in the future");

        List<Appointment> conflicts =
                appointmentRepo.findByDoctorIdAndAppointmentDateBetween(
                        appointment.getDoctor().getId(),
                        newDate.minusHours(1),
                        newDate.plusHours(1)
                );

        for (Appointment a : conflicts)
            if (!a.getId().equals(id))
                throw new RuntimeException("Doctor already has an appointment at this time");

        appointment.setAppointmentDate(newDate);
        appointment.setStatus("PENDING");

        Appointment saved = appointmentRepo.save(appointment);

        try { emailService.sendAppointmentRescheduledEmail(saved); } catch (Exception ignored){}

        return saved;
    }
    @Transactional
    public void deleteAppointment(Long id) {

        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        appointmentRepo.delete(appointment);
    }


    public Appointment getAppointmentById(Long id) {
        return appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));
    }


    public List<Appointment> getUpcomingAppointmentsForDoctor(Long doctorId) {
        return appointmentRepo
                .findByDoctorIdAndAppointmentDateAfterOrderByAppointmentDate(
                        doctorId,
                        LocalDateTime.now()
                );
    }

    public List<Appointment> getUpcomingAppointmentsForPatient(Long patientId) {
        return appointmentRepo
                .findByPatientIdAndAppointmentDateAfterOrderByAppointmentDate(
                        patientId,
                        LocalDateTime.now()
                );
    }
}
