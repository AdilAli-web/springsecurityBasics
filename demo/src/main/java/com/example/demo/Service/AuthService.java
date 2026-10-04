package com.example.demo.Service;

import com.example.demo.Dto.loginRequest;
import com.example.demo.Dto.loginResponse;
import com.example.demo.Entity.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthService {
    public AuthService(AuthenticationManager authenticationManager, TokenGenerator tokenGenerator) {
        this.authenticationManager = authenticationManager;
        this.tokenGenerator = tokenGenerator;
    }

    private final AuthenticationManager authenticationManager;
    private final TokenGenerator tokenGenerator;
//    private final TokenService tokenService;

//    public AuthService(AuthenticationManager authenticationManager, TokenService tokenService) {
//        this.authenticationManager = authenticationManager;
//
//        this.tokenService = tokenService;
//    }
    public loginResponse login(loginRequest loginRequest){

        Authentication authentication=authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())

        );

//        User user= (User) authentication.getPrincipal();
//        String email = authentication.getName();
        String token =
                tokenGenerator.generateToken(authentication);

        return new loginResponse(token);



    }

}
