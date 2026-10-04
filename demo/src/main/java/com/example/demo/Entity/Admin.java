package com.example.demo.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="admins")
@Getter
@Setter
public class Admin {
    @Id
            @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    String PatientName;
    String RoomName;


}
