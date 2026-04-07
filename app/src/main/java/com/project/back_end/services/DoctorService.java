package com.project.back_end.services;

import com.project.back_end.DTO.Login;
import com.project.back_end.models.Appointment;
import com.project.back_end.models.Doctor;
import com.project.back_end.repo.AppointmentRepository;
import com.project.back_end.repo.DoctorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorService {

    private final DoctorRepository doctorRepository;
    private final AppointmentRepository appointmentRepository;
    private final TokenService tokenService;

    public DoctorService(DoctorRepository doctorRepository,
                         AppointmentRepository appointmentRepository,
                         TokenService tokenService) {
        this.doctorRepository = doctorRepository;
        this.appointmentRepository = appointmentRepository;
        this.tokenService = tokenService;
    }

    @Transactional
    public List<String> getDoctorAvailability(Long doctorId, LocalDate date) {
        Optional<Doctor> optional = doctorRepository.findById(doctorId);
        if (optional.isEmpty()) return new ArrayList<>();
        Doctor doctor = optional.get();
        List<String> allSlots = new ArrayList<>(doctor.getAvailableTimes());
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);
        List<Appointment> booked = appointmentRepository
                .findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        List<String> bookedTimes = booked.stream()
                .map(a -> a.getAppointmentTime().toLocalTime().toString())
                .collect(Collectors.toList());
        allSlots.removeIf(slot -> bookedTimes.stream()
                .anyMatch(bt -> slot.startsWith(bt.substring(0, 5))));
        return allSlots;
    }

    public int saveDoctor(Doctor doctor) {
        try {
            if (doctorRepository.findByEmail(doctor.getEmail()) != null) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public int updateDoctor(Doctor doctor) {
        try {
            if (!doctorRepository.existsById(doctor.getId())) return -1;
            doctorRepository.save(doctor);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    @Transactional
    public List<Doctor> getDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        doctors.forEach(d -> d.getAvailableTimes().size());
        return doctors;
    }

    public int deleteDoctor(long id) {
        try {
            if (!doctorRepository.existsById(id)) return -1;
            appointmentRepository.deleteAllByDoctorId(id);
            doctorRepository.deleteById(id);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> validateDoctor(Login login) {
        Map<String, String> response = new HashMap<>();
        try {
            Doctor doctor = doctorRepository.findByEmail(login.getIdentifier());
            if (doctor == null || !doctor.getPassword().equals(login.getPassword())) {
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
            String token = tokenService.generateToken(doctor.getEmail());
            response.put("token", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @Transactional
    public Map<String, Object> findDoctorByName(String name) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        doctors.forEach(d -> d.getAvailableTimes().size());
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByNameSpecilityandTime(String name, String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndTime(String name, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findByNameLike(name);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByNameAndSpecility(String name, String specialty) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository
                .findByNameContainingIgnoreCaseAndSpecialtyIgnoreCase(name, specialty);
        response.put("doctors", doctors);
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorByTimeAndSpecility(String specialty, String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findBySpecialtyIgnoreCase(specialty);
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorBySpecility(String specialty) {
        Map<String, Object> response = new HashMap<>();
        response.put("doctors", doctorRepository.findBySpecialtyIgnoreCase(specialty));
        return response;
    }

    @Transactional
    public Map<String, Object> filterDoctorsByTime(String amOrPm) {
        Map<String, Object> response = new HashMap<>();
        List<Doctor> doctors = doctorRepository.findAll();
        response.put("doctors", filterDoctorByTime(doctors, amOrPm));
        return response;
    }

    private List<Doctor> filterDoctorByTime(List<Doctor> doctors, String amOrPm) {
        return doctors.stream().filter(doctor -> {
            List<String> times = doctor.getAvailableTimes();
            if (times == null) return false;
            return times.stream().anyMatch(slot -> {
                try {
                    String startTime = slot.split("-")[0].trim();
                    LocalTime time = LocalTime.parse(startTime);
                    if (amOrPm.equalsIgnoreCase("AM")) return time.getHour() < 12;
                    else return time.getHour() >= 12;
                } catch (Exception e) {
                    return false;
                }
            });
        }).collect(Collectors.toList());
    }
}