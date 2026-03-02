package com.curelex.healthcare.repository;

import com.curelex.healthcare.entity.PasswordResetOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetOtpRepo extends JpaRepository<PasswordResetOtp,Long> {

    Optional<PasswordResetOtp> findByEmail(String email);

}
