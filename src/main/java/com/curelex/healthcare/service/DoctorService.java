package com.curelex.healthcare.service;

import com.curelex.healthcare.entity.Doctor;
import com.curelex.healthcare.repository.DoctorRepo;
import com.curelex.healthcare.util.FileUploadUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class DoctorService {

    private final DoctorRepo repo;
    private final EmailService email;
    private final PasswordEncoder encoder;


    public Doctor register(
            Doctor d,
            MultipartFile photo,
            MultipartFile certificate
    ) throws Exception {

        // Validate required fields
        if (d.getName() == null || d.getName().isEmpty()) {
            throw new Exception("Doctor name is required");
        }

        if (d.getSpecialization() == null || d.getSpecialization().isEmpty()) {
            throw new Exception("Specialization is required");
        }

        // Handle photo upload if provided
        if (photo != null && !photo.isEmpty()) {
            String photoPath = FileUploadUtil.saveFile("uploads", photo);
            d.setPhotoUrl(photoPath);
        }

        // Handle certificate upload if provided
        if (certificate != null && !certificate.isEmpty()) {
            String certPath = FileUploadUtil.saveFile("uploads", certificate);
            d.setCertificateUrl(certPath);
        }

        // Set default approval status
        if (d.getApprovalStatus() == null) {
            d.setApprovalStatus("PENDING");
        }
        d.setPassword(encoder.encode(d.getPassword()));
        // Save doctor
        Doctor saved = repo.save(d);

        // Send email notification (don't fail registration if email fails)
        try {
            email.send(
                    "New Doctor Registered",
                    "Doctor: " + d.getName() + " | Specialization: " + d.getSpecialization()
            );
        } catch (Exception e) {
            System.err.println("Failed to send email notification: " + e.getMessage());
        }

        return saved;
    }
}