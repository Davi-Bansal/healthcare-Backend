package com.curelex.healthcare.service;

import com.curelex.healthcare.entity.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String companyEmail;

    private void sendMail(String to, String subject, String body){

        SimpleMailMessage msg = new SimpleMailMessage();

        msg.setTo(to);        // ⭐ NOW DYNAMIC EMAIL
        msg.setSubject(subject);
        msg.setText(body);

        mailSender.send(msg);
    }

    public void sendToUser(String userEmail, String subject, String body){
        sendMail(userEmail, subject, body);
    }

    public void send(String subject, String body){
        sendMail(companyEmail, subject, body);
    }

    // ================= APPOINTMENT BOOKED =================
    public void sendAppointmentBookedEmail(Appointment a){

        String text =
                "Patient: " + a.getPatient().getName()
                        + "\nDoctor: " + a.getDoctor().getName()
                        + "\nDate: " + a.getAppointmentDate()
                        + "\nReason: " + a.getReason();

        sendMail(a.getPatient().getEmail(),"Appointment Booked",text);
    }

    public void sendAppointmentConfirmedEmail(Appointment a){

        String text =
                "Doctor confirmed your appointment"
                        + "\nDate: " + a.getAppointmentDate();

        sendMail(a.getPatient().getEmail(),"Appointment Confirmed",text);
    }

    public void sendAppointmentCancelledEmail(Appointment a){

        String text =
                "Appointment cancelled"
                        + "\nReason: " + a.getCancellationReason();

        sendMail(a.getPatient().getEmail(),"Appointment Cancelled",text);
    }

    public void sendAppointmentRescheduledEmail(Appointment a){

        String text =
                "New Date: " + a.getAppointmentDate();

        sendMail(a.getPatient().getEmail(),"Appointment Rescheduled",text);
    }
}