package com.curelex.healthcare.service;

import com.curelex.healthcare.entity.*;
import com.curelex.healthcare.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final PatientRepo patientRepo;
    private final DoctorRepo doctorRepo;
    private final PasswordResetOtpRepo otpRepo;
    private final PasswordEncoder encoder;
    private final EmailService email;

    // ================= SEND OTP =================
    public void sendOtp(String userEmail){

        boolean patientExists = patientRepo.findByEmail(userEmail).isPresent();
        boolean doctorExists  = doctorRepo.findByEmail(userEmail).isPresent();

        if(!patientExists && !doctorExists)
            throw new RuntimeException("Email not registered");

        String otp = String.valueOf((int)(Math.random()*900000)+100000);

        PasswordResetOtp entity = otpRepo.findByEmail(userEmail)
                .orElse(new PasswordResetOtp());

        entity.setEmail(userEmail);
        entity.setOtp(otp);
        entity.setExpiryTime(java.time.LocalDateTime.now().plusMinutes(10));

        otpRepo.save(entity);

        email.sendToUser(
                userEmail,
                "Password Reset OTP",
                "Your OTP is: "+otp+"\nValid for 10 minutes"
        );
    }


    // ================= RESET PASSWORD =================
    public void resetPassword(String userEmail,String otp,String newPassword){

        PasswordResetOtp stored = otpRepo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("OTP not requested"));

        if(!stored.getOtp().equals(otp))
            throw new RuntimeException("Invalid OTP");

        if(stored.getExpiryTime().isBefore(java.time.LocalDateTime.now()))
            throw new RuntimeException("OTP expired");

        // ⭐ update patient if exists
        var patient = patientRepo.findByEmail(userEmail);
        if(patient.isPresent()){
            Patient p = patient.get();
            p.setPassword(encoder.encode(newPassword));
            patientRepo.save(p);
        }

        // ⭐ update doctor if exists
        var doctor = doctorRepo.findByEmail(userEmail);
        if(doctor.isPresent()){
            Doctor d = doctor.get();
            d.setPassword(encoder.encode(newPassword));
            doctorRepo.save(d);
        }

        otpRepo.delete(stored);
    }
}