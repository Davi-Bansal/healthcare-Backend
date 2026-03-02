package com.curelex.healthcare.controller;

import com.curelex.healthcare.repository.MedicalRecordRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/records")
@RequiredArgsConstructor
public class MedicalRecordController {

    private final MedicalRecordRepo repo;

    @GetMapping("/patient/{id}")
    public Object getRecords(@PathVariable Long id){
        return repo.findByPatientId(id);
    }
}
