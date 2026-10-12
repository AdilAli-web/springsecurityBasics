package com.example.demo.Dto;

import java.util.List;

public record DoctorResponse(Integer doctorId,
                             String doctorName,
                             List<PatientResponse> patients) {
}
