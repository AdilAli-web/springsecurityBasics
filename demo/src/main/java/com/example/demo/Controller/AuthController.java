package com.example.demo.Controller;

import com.example.demo.Dto.RegisterRequest;
import com.example.demo.Dto.loginRequest;
import com.example.demo.Dto.loginResponse;
import com.example.demo.Repo.UserRepo;
import com.example.demo.Service.AuthService;
import com.example.demo.Service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    public AuthController(UserRepo userRepo, AuthService authService, UserService userService) {
        this.userRepo = userRepo;
        this.authService = authService;
        this.userService = userService;
    }

    private final UserRepo userRepo;


    private UserService userService;

private final AuthService authService;


@PostMapping("/register")
@ResponseStatus(HttpStatus.CREATED)
    public Map<String,String> register(@RequestBody RegisterRequest request){
       Map<String,String>  register=userService.registers(request);
        return register;
    }


    @PostMapping("/login")
    public ResponseEntity<loginResponse> login(@RequestBody loginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }








    @GetMapping("/home")
    public String home(){
        return "Home page";
    }

}
