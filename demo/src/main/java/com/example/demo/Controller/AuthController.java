package com.example.demo.Controller;

import com.example.demo.Dto.RegisterRequest;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserRepo userRepo;

    @Autowired
    private UserService userService;

    public AuthController(UserRepo userRepo) {
        this.userRepo = userRepo;

    }

@PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> register(@RequestBody RegisterRequest request){
       Map<String,String>  register=userService.registers(request);
        return register;
    }

    @GetMapping("/home")
    public String home(){
        return "Home page";
    }

}
