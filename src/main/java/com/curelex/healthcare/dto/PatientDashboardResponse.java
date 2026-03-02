package com.curelex.healthcare.dto;

import com.curelex.healthcare.entity.Appointment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PatientDashboardResponse {

    private List<Appointment> upcomingAppointments;
    private List<Appointment> completedAppointments;
    private List<String> prescriptions;
    private List<String> symptoms;
}
