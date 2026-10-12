package com.example.demo.Dto;

public record DoctorPatientCountResponse( Integer doctorId,
                                          String doctorName,
                                          long totalPatients) {
}
