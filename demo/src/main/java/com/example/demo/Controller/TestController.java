package com.example.demo.Controller;

import com.example.demo.Entity.Admin;
import com.example.demo.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
public class TestController {
@Autowired
    AdminService adminService;
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password){
//        Map<String,Object> map = new HashMap<>();
//        map.put("username",username);
//        map.put("password",password);
//        return new ResponseEntity<>("login", HttpStatus.OK);
//    }


    @GetMapping("/get")
    public ResponseEntity<?> getUser(){
        String hello="hello";
        return ResponseEntity.ok(hello);
    }


    @GetMapping("/admin")
    public ResponseEntity<List<Admin>> getAdmin(Principal principal) {

        System.out.println("Logged in user: " + principal.getName());

        List<Admin> admins = adminService.getAdmins();

        return ResponseEntity.ok(admins);
    }

    @PostMapping("/admin/create")
    public ResponseEntity<Admin> saveAdmin(@RequestBody Admin admin) {

        Admin savedAdmin = adminService.saveAdmin(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedAdmin);
    }

}