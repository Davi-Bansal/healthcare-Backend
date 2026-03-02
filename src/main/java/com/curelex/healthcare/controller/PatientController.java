package com.curelex.healthcare.controller;

import com.curelex.healthcare.entity.Patient;
import com.curelex.healthcare.service.PatientService;
import com.curelex.healthcare.repository.PatientRepo;
import com.curelex.healthcare.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/patient")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService service;
    private final PatientRepo repo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwt;

    // =========================
    // SIGNUP
    // =========================
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Patient p) {
        try {

            Patient saved = service.signup(p);

            saved.setPassword(null);
            saved.setAadhaarEncrypted(null);

            return ResponseEntity.ok(saved);

        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.badRequest().body("Signup failed: " + e.getMessage());
        }
    }

    // =========================
    // LOGIN
    // =========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Patient p) {

        try {

            var optional = repo.findByEmail(p.getEmail());

            if (optional.isEmpty()) {
                return ResponseEntity.badRequest().body("User not found");
            }

            Patient db = optional.get();

            if (!encoder.matches(p.getPassword(), db.getPassword())) {
                return ResponseEntity.badRequest().body("Wrong password");
            }

            String token = jwt.generateToken(
                    db.getEmail(),
                    "ROLE_" + db.getRole().name()
            );

            Map<String, Object> response = new HashMap<>();

            response.put("token", token);
            response.put("email", db.getEmail());
            response.put("name", db.getName());
            response.put("role", "PATIENT");

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            e.printStackTrace();
            return ResponseEntity.badRequest().body("Login failed: " + e.getMessage());
        }
    }
}