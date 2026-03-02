package com.curelex.healthcare.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private String gender;
    private String mobile;
    private String email;
    private String address;
    private String emergencyContact;

    private String aadhaarEncrypted;

    private String password;
    @Enumerated(EnumType.STRING)
    private Role role = Role.PATIENT;


}
