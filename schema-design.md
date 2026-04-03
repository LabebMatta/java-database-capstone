# Schema Design: Smart Clinic Management System

## MySQL Database Design
The relational schema ensures data integrity, prevents double-booking, and manages secure access levels for different user roles.

### Table: doctors
- `doctor_id`: INT, Primary Key, Auto Increment
- `name`: VARCHAR(100), Not Null
- `specialization`: VARCHAR(100), Not Null
- `email`: VARCHAR(100), Unique, Not Null
- `phone`: VARCHAR(20), Not Null

### Table: patients
- `patient_id`: INT, Primary Key, Auto Increment
- `first_name`: VARCHAR(50), Not Null
- `last_name`: VARCHAR(50), Not Null
- `email`: VARCHAR(100), Unique, Not Null
- `date_of_birth`: DATE, Not Null

### Table: appointments
- `appointment_id`: INT, Primary Key, Auto Increment
- `patient_id`: INT, Foreign Key → patients(patient_id)
- `doctor_id`: INT, Foreign Key → doctors(doctor_id)
- `appointment_date`: DATETIME, Not Null
- `status`: ENUM('Scheduled', 'Completed', 'Cancelled'), Default 'Scheduled'
*Note: A composite unique constraint on doctor_id and appointment_date prevents overlapping slots.*

### Table: admins
- `admin_id`: INT, Primary Key, Auto Increment
- `username`: VARCHAR(50), Unique, Not Null
- `password_hash`: VARCHAR(255), Not Null
- `last_login`: TIMESTAMP, Default Current_Timestamp

---

## MongoDB Collection Design
We use MongoDB for prescriptions because medical orders vary in complexity and benefit from a flexible, nested structure.

### Collection: prescriptions
```json
{
  "prescription_id": "65f2a1b8e4b0f5a2c1d3e4f5",
  "appointment_id": 1024,
  "patient_id": 45,
  "doctor_id": 12,
  "date_issued": "2026-04-03T10:30:00Z",
  "diagnosis": "Acute Bronchitis",
  "medications": [
    {
      "drug_name": "Amoxicillin",
      "dosage": "500mg",
      "frequency": "Three times daily",
      "duration": "7 days",
      "instructions": "Take after meals"
    }
  ],
  "follow_up_required": true,
  "notes": "Patient should rest and increase fluid intake."
}
