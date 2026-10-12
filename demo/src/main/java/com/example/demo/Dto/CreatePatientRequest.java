package com.example.demo.Dto;

public record CreatePatientRequest(  String patientName,
                                     String roomName,
                                     Integer doctorId) {
}
