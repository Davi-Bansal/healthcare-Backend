package com.curelex.healthcare.config;

import com.curelex.healthcare.entity.Doctor;
import com.curelex.healthcare.entity.Role;
import com.curelex.healthcare.repository.DoctorRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DoctorRepo repo;
    private final PasswordEncoder encoder;

    // ⭐ USE SAME EMAIL FROM application.properties
    @Value("${spring.mail.username}")
    private String companyEmail;

    @Override
    public void run(String... args) {

        if(repo.findByEmail(companyEmail).isEmpty()){

            Doctor admin = new Doctor();

            admin.setName("ADMIN");
            admin.setEmail(companyEmail);
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            admin.setApprovalStatus("APPROVED");

            repo.save(admin);

            System.out.println("✅ ADMIN CREATED");
        }
    }
}