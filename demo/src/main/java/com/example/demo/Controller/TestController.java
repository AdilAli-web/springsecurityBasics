package com.example.demo.Controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/user")
public class TestController {

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestParam String username, @RequestParam String password){
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("password",password);
        return new ResponseEntity<>("login", HttpStatus.OK);
    }


    @GetMapping("/get")
    public ResponseEntity<?> getUser(){
        String hello="hello";
        return ResponseEntity.ok(hello);
    }


    @GetMapping("/admin")
    public Map<String,String> getAdmin(Principal principal){

        return Map.of("admin",principal.getName(),"access by admin", "admin");
    }
}