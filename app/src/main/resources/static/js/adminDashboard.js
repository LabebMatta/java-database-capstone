import { openModal } from './components/modals.js';
import { getDoctors, filterDoctors, saveDoctor } from './services/doctorServices.js';
import { createDoctorCard } from './components/doctorCard.js';

document.getElementById('addDocBtn').addEventListener('click', () => {
    openModal('addDoctor');
});

document.addEventListener("DOMContentLoaded", () => {
    loadDoctorCards();
});

async function loadDoctorCards() {
    try {
        const doctors = await getDoctors();
        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";
        doctors.forEach(doctor => {
            const card = createDoctorCard(doctor);
            contentDiv.appendChild(card);
        });
    } catch (error) {
        console.error("Failed to load doctors:", error);
    }
}

document.getElementById("searchBar").addEventListener("input", filterDoctorsOnChange);
document.getElementById("filterTime").addEventListener("change", filterDoctorsOnChange);
document.getElementById("filterSpecialty").addEventListener("change", filterDoctorsOnChange);

async function filterDoctorsOnChange() {
    const name = document.getElementById("searchBar").value.trim() || null;
    const time = document.getElementById("filterTime").value || null;
    const specialty = document.getElementById("filterSpecialty").value || null;

    try {
        const response = await filterDoctors(name, time, specialty);
        const doctors = response.doctors;
        const contentDiv = document.getElementById("content");
        contentDiv.innerHTML = "";

        if (doctors && doctors.length > 0) {
            doctors.forEach(doctor => {
                const card = createDoctorCard(doctor);
                contentDiv.appendChild(card);
            });
        } else {
            contentDiv.innerHTML = "<p>No doctors found with the given filters.</p>";
        }
    } catch (error) {
        console.error("Failed to filter doctors:", error);
        alert("❌ An error occurred while filtering doctors.");
    }
}

function renderDoctorCards(doctors) {
    const contentDiv = document.getElementById("content");
    contentDiv.innerHTML = "";
    doctors.forEach(doctor => {
        const card = createDoctorCard(doctor);
        contentDiv.appendChild(card);
    });
}

window.adminAddDoctor = async function () {
    const name = document.getElementById("name").value;
    const email = document.getElementById("email").value;
    const phone = document.getElementById("phone").value;
    const password = document.getElementById("password").value;
    const specialty = document.getElementById("specialty").value;

    const checkboxes = document.querySelectorAll(
        'input[name="availableTimes"]:checked'
    );
    const availableTimes = Array.from(checkboxes).map(cb => cb.value);

    const token = localStorage.getItem("token");
    if (!token) {
        alert("❌ You must be logged in as admin.");
        return;
    }

    const doctor = { name, email, phone, password, specialty, availableTimes };

    const { success, message } = await saveDoctor(doctor, token);
    if (success) {
        alert("✅ " + message);
        document.getElementById("modal").style.display = "none";
        window.location.reload();
    } else {
        alert("❌ " + message);
    }
};