package com.example.demo.Dto;


import com.example.demo.Entity.Role;

public record RegisterRequest(String name, String email, String password, Role role) {
}
