import { getAllAppointments } from './services/appointmentRecordService.js';
import { createPatientRow } from './components/patientRows.js';

const tableBody = document.getElementById("patientTableBody");
let selectedDate = new Date().toISOString().split('T')[0];
const urlParts = window.location.pathname.split('/');
const token = urlParts[urlParts.length - 1];
let patientName = null;

document.getElementById("searchBar").addEventListener("input", (e) => {
    const value = e.target.value.trim();
    patientName = value.length > 0 ? value : "null";
    loadAppointments();
});

document.getElementById("todayButton").addEventListener("click", () => {
    selectedDate = new Date().toISOString().split('T')[0];
    document.getElementById("datePicker").value = selectedDate;
    loadAppointments();
});

document.getElementById("datePicker").addEventListener("change", (e) => {
    selectedDate = e.target.value;
    loadAppointments();
});

async function loadAppointments() {
    try {
        const responseData = await getAllAppointments(selectedDate, patientName, token);
        
        console.log("Data received from server:", responseData);

        const appointments = responseData.appointments || responseData;

        tableBody.innerHTML = "";

        if (!Array.isArray(appointments) || appointments.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">No Appointments found.</td></tr>`;
            return;
        }

const doctorId = new URLSearchParams(window.location.search).get('doctorId') || "1"; 

appointments.forEach(appointment => {

    const patientData = {
        id: appointment.patientId || (appointment.patient ? appointment.patient.id : "N/A"),
        name: appointment.patientName || (appointment.patient ? appointment.patient.name : "Unknown"),
        phone: appointment.patientPhone || (appointment.patient ? appointment.patient.phone : "N/A"),
        email: appointment.patientEmail || (appointment.patient ? appointment.patient.email : "N/A")
    };

    const row = createPatientRow(patientData, appointment.id, doctorId); 
    tableBody.appendChild(row);
});
    } catch (error) {
        console.error("JavaScript Crash Log:", error);
        tableBody.innerHTML = `<tr><td colspan="5" class="noPatientRecord">Error loading appointments. Check console.</td></tr>`;
    }
}

document.addEventListener("DOMContentLoaded", () => {
    loadAppointments();
});