package com.example.demo.Repo;

import com.example.demo.Entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AdminRepo extends JpaRepository<Admin,Integer> {
    long countByDoctor_Id(Integer doctorId);
    List<Admin> findByDoctor_Id(Integer doctorId);
}
