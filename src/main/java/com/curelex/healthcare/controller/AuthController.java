package com.curelex.healthcare.controller;

import com.curelex.healthcare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/forgot")
    public String forgot(@RequestParam String email){

        service.sendOtp(email);

        return "OTP sent if email exists";
    }

    @PostMapping("/reset")
    public String reset(
            @RequestParam String email,
            @RequestParam String otp,
            @RequestParam String password){

        service.resetPassword(email,otp,password);

        return "Password updated";
    }
}