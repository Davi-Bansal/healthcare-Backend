package com.curelex.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------- RELATIONS ----------

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    @NotNull(message = "Patient is required")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    @NotNull(message = "Doctor is required")
    @JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
    private Doctor doctor;

    // ---------- APPOINTMENT DETAILS ----------

    @NotNull(message = "Appointment date is required")
    @Future(message = "Appointment date must be in the future")
    private LocalDateTime appointmentDate;

    @NotBlank(message = "Reason is required")
    @Column(length = 500)
    private String reason;

    // PENDING / CONFIRMED / COMPLETED / CANCELLED
    @Column(nullable = false)
    private String status;

    @Column(length = 1000)
    private String notes;

    @Column(length = 1000)
    private String prescription;

    private String cancellationReason;

    // ---------- AUDIT FIELDS ----------

    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    private LocalDateTime updatedAt;

    // ---------- AUTO TIMESTAMP ----------

    @PrePersist
    protected void onCreate(){

        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();

        if(status == null || status.isBlank()){
            status = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
