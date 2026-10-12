package com.example.demo.Controller;

import com.example.demo.Dto.DoctorPatientCountResponse;
import com.example.demo.Dto.DoctorResponse;
import com.example.demo.Dto.GetPatientWithDoctor;
import com.example.demo.Entity.Doctor;
import com.example.demo.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    @PostMapping("/create")
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor){
        Doctor newDoctor=doctorService.createDoctor(doctor);
        return ResponseEntity.ok(newDoctor);
    }

    @GetMapping("/getAllDoctor")
    public List<Doctor> getAllDoctor(){
        return doctorService.getAllDoctor();
    }


    @GetMapping("/get/{doctorId}/patient-count")
    public ResponseEntity<DoctorPatientCountResponse> getDoctorPatientCount(
            @PathVariable Integer doctorId) {

        return ResponseEntity.ok(
                doctorService.getDoctorPatientCount(doctorId)
        );
    }


    @GetMapping("/get/{doctorId}/specific-patient")
    public ResponseEntity<DoctorResponse> getPatientWithDoctor(@PathVariable Integer doctorId){
        return ResponseEntity.ok(
                doctorService.getPatientWithDoctor(doctorId)
        );
    }


}
