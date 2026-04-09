package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Admin;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Patient;
import com.project.back_end.repo.AdminRepository;
import com.project.back_end.repo.DoctorRepository;
import com.project.back_end.repo.PatientRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@org.springframework.stereotype.Service
public class Service {

    private final TokenService tokenService;
    private final AdminRepository adminRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final DoctorService doctorService;
    private final PatientService patientService;

    public Service(TokenService tokenService,
                   AdminRepository adminRepository,
                   DoctorRepository doctorRepository,
                   PatientRepository patientRepository,
                   DoctorService doctorService,
                   PatientService patientService) {
        this.tokenService = tokenService;
        this.adminRepository = adminRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.doctorService = doctorService;
        this.patientService = patientService;
    }

    public ResponseEntity<Map<String, String>> validateToken(String token, String user) {
        Map<String, String> response = new HashMap<>();
        if (!tokenService.validateToken(token, user)) {
            response.put("message", "Invalid or expired token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        return ResponseEntity.ok(new HashMap<>());
    }

    public ResponseEntity<Map<String, String>> validateAdmin(Admin receivedAdmin) {
        Map<String, String> response = new HashMap<>();
        try {
            Admin admin = adminRepository.findByUsername(receivedAdmin.getUsername());
            if (admin == null || !admin.getPassword().equals(receivedAdmin.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(admin.getUsername());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public Map<String, Object> filterDoctor(String name, String specialty, String time) {
        boolean hasName = name != null && !name.equals("null");
        boolean hasSpecialty = specialty != null && !specialty.equals("null");
        boolean hasTime = time != null && !time.equals("null");

        if (hasName && hasSpecialty && hasTime)
            return doctorService.filterDoctorsByNameSpecilityandTime(name, specialty, time);
        if (hasName && hasTime)
            return doctorService.filterDoctorByNameAndTime(name, time);
        if (hasName && hasSpecialty)
            return doctorService.filterDoctorByNameAndSpecility(name, specialty);
        if (hasSpecialty && hasTime)
            return doctorService.filterDoctorByTimeAndSpecility(specialty, time);
        if (hasName)
            return doctorService.findDoctorByName(name);
        if (hasSpecialty)
            return doctorService.filterDoctorBySpecility(specialty);
        if (hasTime)
            return doctorService.filterDoctorsByTime(time);

        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorService.getDoctors());
        return response;
    }

    public int validateAppointment(Appointment appointment) {
        var doctor = doctorRepository.findById(appointment.getDoctor().getId());
        if (doctor.isEmpty()) return -1;
        List<String> available = doctorService.getDoctorAvailability(
                appointment.getDoctor().getId(),
                appointment.getAppointmentTime().toLocalDate()
        );
        LocalTime requestedTime = appointment.getAppointmentTime().toLocalTime();
        boolean isAvailable = available.stream().anyMatch(slot -> {
            try {
                String startTime = slot.split("-")[0].trim();
                return LocalTime.parse(startTime).equals(requestedTime);
            } catch (Exception e) {
                return false;
            }
        });
        return isAvailable ? 1 : 0;
    }

    public boolean validatePatient(Patient patient) {
        return patientRepository.findByEmailOrPhone(
                patient.getEmail(), patient.getPhone()) == null;
    }

    public ResponseEntity<Map<String, String>> validatePatientLogin(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            if (login.getIdentifier() == null || login.getPassword() == null) {
                response.put("message", "Email and Password are required");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            Patient patient = patientRepository.findByEmail(login.getIdentifier());
            
            if (patient == null || !patient.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String token = tokenService.generateToken(patient.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    public ResponseEntity<Map<String, Object>> filterPatient(
            String condition, String name, String token) {
        String email = tokenService.extractIdentifier(token);
        Patient patient = patientRepository.findByEmail(email);
        Long patientId = patient.getId();

        boolean hasCondition = condition != null && !condition.equals("null");
        boolean hasName = name != null && !name.equals("null");

        if (hasCondition && hasName)
            return patientService.filterByDoctorAndCondition(condition, name, patientId);
        if (hasCondition)
            return patientService.filterByCondition(condition, patientId);
        if (hasName)
            return patientService.filterByDoctor(name, patientId);

        return patientService.getPatientAppointment(patientId, token);
    }
}