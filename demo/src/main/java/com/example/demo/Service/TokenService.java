//package com.example.demo.Service;
//
//import com.example.demo.Entity.User;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Component;
//import org.springframework.stereotype.Service;
//
//import javax.crypto.SecretKey;
//import java.nio.charset.StandardCharsets;
//import java.util.Date;
//
//
//@Component
//public class TokenService {
//    @Value("${jwt:secret}")
//    private String jwt;
//
//
//    private SecretKey secretKey(){
//
//        return Keys.hmacShaKeyFor(jwt.getBytes(StandardCharsets.UTF_8));
//    }
//
//
//    public String generateToken(Authentication user){
//        return Jwts.builder()
//                .subject(user.getName())
////                .claim("userId", user.getId())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + 1000*60*10))
//                .signWith(secretKey())
//                .compact();
//    }
//
//}
