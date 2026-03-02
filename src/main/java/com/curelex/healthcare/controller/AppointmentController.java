package com.curelex.healthcare.controller;

import com.curelex.healthcare.dto.AppointmentRequest;
import com.curelex.healthcare.entity.Appointment;
import com.curelex.healthcare.service.AppointmentService;
import com.curelex.healthcare.repository.AppointmentRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AppointmentController {

    private final AppointmentService service;
    private final AppointmentRepo repo;

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@Valid @RequestBody AppointmentRequest request) {
        try {
            Appointment appointment = service.bookAppointment(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Appointment booked successfully",
                    "appointment", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Get all appointments for a patient
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long patientId) {
        List<Appointment> appointments = repo.findByPatientId(patientId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", appointments.size(),
                "appointments", appointments
        ));
    }

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long doctorId) {
        List<Appointment> appointments = repo.findByDoctorId(doctorId);
        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", appointments.size(),
                "appointments", appointments
        ));
    }

    /**
     * Get appointment by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        var optional = repo.findById(id);

        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("success", false, "message", "Appointment not found"));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "appointment", optional.get()
        ));
    }

    /**
     * Get all appointments
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllAppointments(
            @RequestParam(required = false) String status
    ) {
        List<Appointment> appointments;

        if (status != null) {
            appointments = repo.findByStatus(status);
        } else {
            appointments = repo.findAll();
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "count", appointments.size(),
                "appointments", appointments
        ));
    }

    /**
     * Confirm appointment
     */
    @PutMapping("/{id}/confirm")
    public ResponseEntity<?> confirmAppointment(@PathVariable Long id) {
        try {
            Appointment appointment = service.confirmAppointment(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment confirmed",
                    "appointment", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Complete appointment with prescription
     */
    @PutMapping("/{id}/complete")
    public ResponseEntity<?> completeAppointment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String notes = body.get("notes");
            String prescription = body.get("prescription");
            Appointment appointment = service.completeAppointment(id, notes, prescription);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment completed",
                    "appointment", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    /**
     * Cancel appointment
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(
            @PathVariable Long id,
            @RequestBody Map<String, String> body
    ) {
        try {
            String reason = body.get("reason");
            Appointment appointment = service.cancelAppointment(id, reason);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment cancelled",
                    "appointment", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(
            @PathVariable Long id,
            @RequestBody AppointmentRequest request
    ) {
        try {
            Appointment appointment = service.rescheduleAppointment(id, request.getAppointmentDate());
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment rescheduled",
                    "appointment", appointment
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        try {
            service.deleteAppointment(id);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Appointment deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}