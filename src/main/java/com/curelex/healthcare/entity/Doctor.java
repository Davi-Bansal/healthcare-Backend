package com.curelex.healthcare.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private String gender;
    private String specialization;
    private String registrationNumber;
    private String state;
    private String hospital;
    private Integer experience;
    private Integer patientsTreated;
    private String password;
    @Column(unique = true)
    private String email;
    @JsonIgnore
    private String photoUrl;
    @JsonIgnore
    private String certificateUrl;

    private String approvalStatus = "PENDING";

    @Enumerated(EnumType.STRING)
    private Role role = Role.DOCTOR;

}
