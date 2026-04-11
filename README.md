# 🏥 Smart Clinic Management System

A full-stack web application that digitizes clinic administration, built as the IBM Java Developer Capstone Project. The system supports three user roles — **Admin**, **Doctor**, and **Patient** — each with their own authenticated dashboard and functionality.

---

## 🚀 Tech Stack

**Backend:** Java, Spring Boot, Spring MVC, Spring Data JPA, Hibernate, JWT Authentication  
**Frontend:** HTML5, CSS3, JavaScript (ES6 Modules)  
**Databases:** MySQL (relational data) + MongoDB (prescriptions)  
**Tools:** Maven, Git, Thymeleaf

---

## ✨ Features

- 🔐 JWT-based authentication for all user roles
- 👨‍⚕️ Admin: Add, update, delete, and filter doctors
- 🩺 Doctor: View appointments by date, write prescriptions
- 🧑‍💼 Patient: Browse doctors, book and cancel appointments
- 🔍 Real-time doctor search and filtering (by name, specialty, time)
- 💊 Prescription management stored in MongoDB
- 📊 SQL stored procedures for appointment reports

---

## 🗄️ Database Design

- **MySQL:** Admin, Doctor, Patient, Appointment tables with relational integrity
- **MongoDB:** Prescriptions collection linked to MySQL appointments via `appointmentId`

---

## 🏗️ Architecture

```
Frontend (HTML/CSS/JS)
        ↓
Spring Boot REST API
        ↓
Service Layer (Business Logic)
        ↓
Repository Layer (Spring Data JPA / MongoDB)
        ↓
MySQL + MongoDB
```

---

## ▶️ Running the Project

1. Clone the repository:
```bash
git clone https://github.com/LabebMatta/java-database-capstone.git
```

2. Restore the databases:
```bash
mysql -u root -p < scripts/cms_backup.sql
mongosh < scripts/mongodb_setup.js
```

3. Run the application:
```bash
cd app
mvn spring-boot:run
```

4. Open your browser at `http://localhost:8080`

---

## 👤 Author

**Labeb Matta**  
