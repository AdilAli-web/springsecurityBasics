package com.example.demo.Config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SpringConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {


        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/user/get").permitAll()
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/home").permitAll()
                        .requestMatchers("/api/v1/user/admin").hasRole("ADMIN")
                        .requestMatchers("/api/v1/user/admin/create").permitAll()
                        .requestMatchers("/api/v1/user/login").permitAll()
                        .anyRequest().authenticated()

                )
//                .oauth2ResourceServer(oauth2->oauth2.jwt(Customizer.withDefaults()))
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )
                .build();


//                there is one more -> form login uses in frontend  and http basic uses in post man to see working
//                .formLogin(Customizer.withDefaults())
//                .httpBasic(Customizer.withDefaults())


    }



@Bean
AuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder)
{
    

    DaoAuthenticationProvider provider=new DaoAuthenticationProvider(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return  provider;
}



    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey secretKey) {

        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    SecretKey secretKey(@Value("${jwt.secret}") String secret) {
        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }
    @Bean
    JwtDecoder jwtDecoder(SecretKey secretKey) {
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .build();
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider authenticationProvider){
        return new ProviderManager(authenticationProvider);

    }
//
//    @Bean
//    public JwtDecoder jwtDecoder(
//            SecretKey secretKey,
//            @Value("${jwt.issuer}") String issuer) {
//
//        NimbusJwtDecoder decoder =
//                NimbusJwtDecoder
//                        .withSecretKey(secretKey)
//                        .macAlgorithm(MacAlgorithm.HS256)
//                        .build();
//
//        decoder.setJwtValidator(
//                JwtValidators.createDefaultWithIssuer(
//                        issuer
//                )
//        );
//
//        return decoder;
//    }


//    @Bean
//    public SecretKey secretKey(@Value("${jwt.secret}") String secret){
//        byte[] decodedKey= Base64.getDecoder().decode(secret);
//        return new SecretKeySpec(
//                decodedKey,
//                "HmacSHA256"
//        );
//
//    }

    @Bean
JwtAuthenticationConverter jwtAuthenticationConverter() {

    JwtGrantedAuthoritiesConverter authoritiesConverter =
            new JwtGrantedAuthoritiesConverter();

    authoritiesConverter.setAuthoritiesClaimName("role");
    authoritiesConverter.setAuthorityPrefix("");

    JwtAuthenticationConverter converter =
            new JwtAuthenticationConverter();

    converter.setJwtGrantedAuthoritiesConverter(
            authoritiesConverter
    );

    return converter;
}
//    @Bean
//    public JwtAuthenticationConverter jwtAuthenticationConverter() {
//
//        JwtGrantedAuthoritiesConverter authoritiesConverter =
//                new JwtGrantedAuthoritiesConverter();
//
//        authoritiesConverter.setAuthoritiesClaimName(
//                "authorities"
//        );
//
//        authoritiesConverter.setAuthorityPrefix("");
//
//        JwtAuthenticationConverter
//                authenticationConverter =
//                new JwtAuthenticationConverter();
//
//        authenticationConverter
//                .setJwtGrantedAuthoritiesConverter(
//                        authoritiesConverter
//                );
//
//        return authenticationConverter;
//    }





}