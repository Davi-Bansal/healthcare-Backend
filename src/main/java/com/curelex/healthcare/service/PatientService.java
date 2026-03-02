package com.curelex.healthcare.service;

import com.curelex.healthcare.entity.PasswordResetOtp;
import com.curelex.healthcare.entity.Patient;
import com.curelex.healthcare.repository.PasswordResetOtpRepo;
import com.curelex.healthcare.repository.PatientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PatientService {

    private final PasswordResetOtpRepo otpRepo;
    private final PatientRepo repo;
    private final PasswordEncoder encoder;
    private final EncryptionService encryption;
    private final EmailService email;

    public void sendOtp(String userEmail){

        repo.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Email not registered"));

        String otp = String.valueOf((int)(Math.random()*900000)+100000);

        PasswordResetOtp entity = otpRepo.findByEmail(userEmail)
                .orElse(new PasswordResetOtp());

        entity.setEmail(userEmail);
        entity.setOtp(otp);
        entity.setExpiryTime(java.time.LocalDateTime.now().plusMinutes(10));

        otpRepo.save(entity);

        // ✅ FIXED HERE
        email.sendToUser(
                userEmail,
                "Password Reset OTP",
                "Your OTP is: " + otp + "\nValid for 10 minutes"
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

        var patient = repo.findByEmail(userEmail).orElseThrow();

        patient.setPassword(encoder.encode(newPassword));

        repo.save(patient);

        otpRepo.delete(stored);
    }
    private String maskAadhaar(String aadhaar){

        if(aadhaar == null || aadhaar.length() < 4) return "XXXX";

        String last4 = aadhaar.substring(aadhaar.length()-4);

        return "XXXXXXXX" + last4;
    }

    // ================= SIGNUP =================
    public Patient signup(Patient p){

        if (p.getEmail()==null || p.getEmail().isBlank())
            throw new RuntimeException("Email required");

        if (p.getPassword()==null || p.getPassword().isBlank())
            throw new RuntimeException("Password required");

        if (p.getName()==null || p.getName().isBlank())
            throw new RuntimeException("Name required");

        if(repo.findByEmail(p.getEmail()).isPresent())
            throw new RuntimeException("Email already registered");

        p.setPassword(encoder.encode(p.getPassword()));

        if(p.getAadhaarEncrypted()!=null && !p.getAadhaarEncrypted().isBlank()){
            p.setAadhaarEncrypted(encryption.encrypt(p.getAadhaarEncrypted()));
        }

        Patient saved = repo.save(p);

        try{
            String decrypted = encryption.decrypt(saved.getAadhaarEncrypted());
            String masked = maskAadhaar(decrypted);

            email.send(
                    "New Patient Registered",
                    "Name: " + saved.getName()
                            + "\nEmail: " + saved.getEmail()
                            + "\nAadhaar: " + masked
            );
        }catch(Exception ignored){}

        return saved;
    }
}
