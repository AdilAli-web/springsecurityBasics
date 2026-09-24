package com.example.demo.Service;

import com.example.demo.Dto.RegisterRequest;
import com.example.demo.Entity.User;
import com.example.demo.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    public Map<String,String> registers(RegisterRequest request){
        String email=request.email().toLowerCase();
        String password=request.password();
        String name=request.name();
if(userRepo.existsByEmail(email)){
    throw  new IllegalArgumentException("Email already registered");

}
        User user=new User();



    }
}
