package com.curelex.healthcare.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class MedicalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Patient patient;

    @ManyToOne
    private Doctor doctor;

    private String diagnosis;

    @Column(length = 1000)
    private String prescription;

    @Column(length = 1000)
    private String notes;

    private LocalDateTime createdAt = LocalDateTime.now();
}
