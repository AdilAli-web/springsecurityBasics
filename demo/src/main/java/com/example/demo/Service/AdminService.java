package com.example.demo.Service;

import com.example.demo.Entity.Admin;
import com.example.demo.Repo.AdminRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
@Autowired
    AdminRepo adminRepo;

    public List<Admin> getAdmins() {
        List<Admin> admins=adminRepo.findAll();
        return admins;


    }

    public Admin saveAdmin(Admin admin) {
        return adminRepo.save(admin);
    }



}
