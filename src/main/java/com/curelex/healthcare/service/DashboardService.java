package com.curelex.healthcare.service;

import com.curelex.healthcare.dto.PatientDashboardResponse;
import com.curelex.healthcare.entity.Appointment;
import com.curelex.healthcare.repository.AppointmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final AppointmentRepo repo;

    public PatientDashboardResponse getDashboard(Long patientId){

        List<Appointment> upcoming =
                repo.findByPatientIdAndAppointmentDateAfterOrderByAppointmentDate(
                        patientId,
                        LocalDateTime.now()
                );

        List<Appointment> completed =
                repo.findByPatientIdAndStatus(patientId, "COMPLETED");

        List<String> prescriptions =
                completed.stream()
                        .map(Appointment::getPrescription)
                        .filter(p -> p != null && !p.isBlank())
                        .collect(Collectors.toList());

        List<String> symptoms =
                completed.stream()
                        .map(Appointment::getReason)
                        .collect(Collectors.toList());

        return new PatientDashboardResponse(
                upcoming,
                completed,
                prescriptions,
                symptoms
        );
    }
}
