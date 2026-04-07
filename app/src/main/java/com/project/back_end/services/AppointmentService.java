package com.project.back_end.services;

import com.project.back_end.DTO.AppointmentDTO;
import com.project.back_end.models.Appointment;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final com.project.back_end.services.Service service;
    private final TokenService tokenService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              com.project.back_end.services.Service service,
                              TokenService tokenService,
                              PatientRepository patientRepository,
                              DoctorRepository doctorRepository) {
        this.appointmentRepository = appointmentRepository;
        this.service = service;
        this.tokenService = tokenService;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
    }

    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
            if (existing.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            int valid = service.validateAppointment(appointment);
            if (valid == -1) {
                response.put("message", "Doctor not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            } else if (valid == 0) {
                response.put("message", "Time slot not available");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
            }
            appointmentRepository.save(appointment);
            response.put("message", "Appointment updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        try {
            Optional<Appointment> optional = appointmentRepository.findById(id);
            if (optional.isEmpty()) {
                response.put("message", "Appointment not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            Appointment appointment = optional.get();
            String email = tokenService.extractIdentifier(token);
            if (!appointment.getPatient().getEmail().equals(email)) {
                response.put("message", "Unauthorized");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> response = new HashMap<>();
        try {
            String email = tokenService.extractIdentifier(token);
            var doctor = doctorRepository.findByEmail(email);
            if (doctor == null) {
                response.put("message", "Doctor not found");
                return response;
            }
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.atTime(23, 59, 59);
            List<Appointment> appointments;
            if (pname == null || pname.equals("null")) {
                appointments = appointmentRepository
                        .findByDoctorIdAndAppointmentTimeBetween(doctor.getId(), start, end);
            } else {
                appointments = appointmentRepository
                        .findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(
                                doctor.getId(), pname, start, end);
            }
            List<AppointmentDTO> dtos = appointments.stream().map(a -> new AppointmentDTO(
                    a.getId(),
                    a.getDoctor().getId(),
                    a.getDoctor().getName(),
                    a.getPatient().getId(),
                    a.getPatient().getName(),
                    a.getPatient().getEmail(),
                    a.getPatient().getPhone(),
                    a.getPatient().getAddress(),
                    a.getAppointmentTime(),
                    a.getStatus()
            )).collect(Collectors.toList());
            response.put("appointments", dtos);
        } catch (Exception e) {
            response.put("message", "Error fetching appointments");
        }
        return response;
    }

    @Transactional
    public void changeStatus(int status, long id) {
        appointmentRepository.updateStatus(status, id);
    }
}