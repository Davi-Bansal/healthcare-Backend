package com.curelex.healthcare.controller;

import com.curelex.healthcare.entity.Doctor;
import com.curelex.healthcare.repository.DoctorRepo;
import com.curelex.healthcare.security.JwtUtil;
import com.curelex.healthcare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/doctor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorService service;
    private final DoctorRepo repo;   // ⭐ ADD THIS LINE
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    // ==========================
    // SIGNUP
    // ==========================
    @PostMapping("/signup")
    public ResponseEntity<?> signup(
            @ModelAttribute Doctor d,
            @RequestParam(required = false) MultipartFile photo,
            @RequestParam(required = false) MultipartFile certificate
    ) {
        try {
            Doctor saved = service.register(d, photo, certificate);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    // ==========================
    // LOGIN
    // ==========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Doctor doctor){

        var optional = repo.findByEmail(doctor.getEmail());

        if(optional.isEmpty())
            return ResponseEntity.badRequest().body("Doctor not found");

        Doctor db = optional.get();

        // MUST BE APPROVED
        if(!"APPROVED".equalsIgnoreCase(db.getApprovalStatus())){
            return ResponseEntity.badRequest()
                    .body("Doctor not approved by admin yet");
        }

        if(!encoder.matches(doctor.getPassword(), db.getPassword()))
            return ResponseEntity.badRequest().body("Wrong password");

        String token = jwt.generateToken(
                db.getEmail(),
                "ROLE_" + db.getRole().name()
        );

        return ResponseEntity.ok(Map.of(
                "token", token,
                "name", db.getName(),
                "email", db.getEmail(),
                "role",db.getRole().name()
        ));
    }
}
