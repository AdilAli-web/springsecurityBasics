package com.example.demo.Service;

import com.example.demo.Dto.CreatePatientRequest;
import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Doctor;
import com.example.demo.Repo.AdminRepo;
import com.example.demo.Repo.DoctorRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
    private final AdminRepo adminRepo;
    private final DoctorRepo doctorRepo;

    public AdminService(AdminRepo adminRepo,
                        DoctorRepo doctorRepo) {
        this.adminRepo = adminRepo;
        this.doctorRepo = doctorRepo;
    }

    public List<Admin> getAdmins() {
        List<Admin> admins=adminRepo.findAll();
        return admins;


    }

//    public Admin saveAdmin(Admin admin) {
//        return adminRepo.save(admin);
//    }
    @Transactional
public Admin saveAdmin(CreatePatientRequest request) {

    Doctor doctor = doctorRepo.findById(request.doctorId())
            .orElseThrow(() ->
                    new RuntimeException("Doctor not found"));

    Admin patient = new Admin();
    patient.setPatientName(request.patientName());
    patient.setRoomName(request.roomName());
    patient.setDoctor(doctor);

    return adminRepo.save(patient);
}


}
