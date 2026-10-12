package com.example.demo.Service;

import com.example.demo.Dto.DoctorPatientCountResponse;
import com.example.demo.Dto.DoctorResponse;
import com.example.demo.Dto.GetPatientWithDoctor;
import com.example.demo.Dto.PatientResponse;
import com.example.demo.Entity.Admin;
import com.example.demo.Entity.Doctor;
import com.example.demo.Repo.AdminRepo;
import com.example.demo.Repo.DoctorRepo;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class DoctorService {
    @Autowired
    private DoctorRepo doctorRepo;

    public Doctor createDoctor(Doctor doctor){
        return doctorRepo.save(doctor);
    }

    public List<Doctor> getAllDoctor(){
        return doctorRepo.findAll();
    }


    @Autowired
    private AdminRepo adminRepo;

    public DoctorPatientCountResponse getDoctorPatientCount(Integer doctorId) {

        Doctor doctor = doctorRepo.findById(doctorId)
                .orElseThrow(() ->
                        new RuntimeException("Doctor not found"));

        long totalPatients = adminRepo.countByDoctor_Id(doctorId);

        return new DoctorPatientCountResponse(
                doctor.getId(),
                doctor.getDoctorName(),
                totalPatients
        );
    }


    public DoctorResponse getPatientWithDoctor(Integer doctorId) {
        Doctor doctor=doctorRepo.findById(doctorId).orElseThrow(()->new NoSuchElementException("Doctor not found with this id"));

   List<PatientResponse> patient= adminRepo.findByDoctor_Id(doctorId)
                .stream()
                .map(patients -> new PatientResponse(
                        patients.getPatientName(),
                        patients.getRoomName(),
                        patients.getId()
                ))
                .toList();

   return  new DoctorResponse(
           doctor.getId(),
           doctor.getDoctorName(),
           patient
   );


    }
}
