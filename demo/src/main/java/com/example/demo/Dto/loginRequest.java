package com.example.demo.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
//@AllArgsConstructor
//@NoArgsConstructor
public class loginRequest {
    public loginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    String email;
    String password;
//    public loginRequest(String email, String password)
}
