# Schema Design: Smart Clinic Management System

## ## MySQL Database Design (Relational)
The relational schema ensures data integrity, prevents double-booking, and manages secure access levels for different user roles.

### 1. doctors Table
Stores professional details and credentials for medical staff.

| Column | Data Type | Constraints |
| :--- | :--- | :--- |
| `doctor_id` | INT | PRIMARY KEY, AUTO_INCREMENT |
| `name` | VARCHAR(100) | NOT NULL |
| `specialization` | VARCHAR(100) | NOT NULL |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL |
| `phone` | VARCHAR(20) | NOT NULL |

### 2. patients Table
Stores personal information and contact details for registered patients.

| Column | Data Type | Constraints |
| :--- | :--- | :--- |
| `patient_id` | INT | PRIMARY KEY, AUTO_INCREMENT |
| `first_name` | VARCHAR(50) | NOT NULL |
| `last_name` | VARCHAR(50) | NOT NULL |
| `email` | VARCHAR(100) | UNIQUE, NOT NULL |
| `date_of_birth` | DATE | NOT NULL |

### 3. appointments Table
Coordinates the relationship between doctors and patients.

| Column | Data Type | Constraints |
| :--- | :--- | :--- |
| `appointment_id` | INT | PRIMARY KEY, AUTO_INCREMENT |
| `patient_id` | INT | FOREIGN KEY (patients.patient_id) |
| `doctor_id` | INT | FOREIGN KEY (doctors.doctor_id) |
| `appointment_date` | DATETIME | NOT NULL |
| `status` | ENUM('Scheduled', 'Completed', 'Cancelled') | DEFAULT 'Scheduled' |

> **Note:** A Composite Unique constraint should be applied to (`doctor_id`, `appointment_date`) to prevent double-booking the same doctor at the same time.

### 4. admins Table
Manages administrative credentials for portal oversight.

| Column | Data Type | Constraints |
| :--- | :--- | :--- |
| `admin_id` | INT | PRIMARY KEY, AUTO_INCREMENT |
| `username` | VARCHAR(50) | UNIQUE, NOT NULL |
| `password_hash` | VARCHAR(255) | NOT NULL (Encrypted) |
| `last_login` | TIMESTAMP | DEFAULT CURRENT_TIMESTAMP |

---

## ## MongoDB Collection Design (Non-Relational)
We use MongoDB for **Prescriptions** because medical orders vary wildly in complexity (number of medications, specific instructions, and duration).

### Collection: prescriptions
This collection stores detailed clinical orders. Using a document format allows us to nest multiple medications within a single record without complex SQL joins.

**Realistic JSON Example:**

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
    },
    {
      "drug_name": "Guaifenesin",
      "dosage": "600mg",
      "frequency": "Every 12 hours",
      "duration": "5 days",
      "instructions": "Drink plenty of water"
    }
  ],
  "follow_up_required": true,
  "notes": "Patient should rest for at least 3 days and avoid cold drinks."
}
