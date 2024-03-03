package com.Mr.fix.it.Config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.core.io.ClassPathResource;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.messaging.FirebaseMessaging;

import com.Mr.fix.it.Repository.UserRepository;
import com.Mr.fix.it.Exception.ExceptionType.NotFoundException;

import java.io.IOException;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class ApplicationConfig
{
    private final UserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsService()
    {
        return username -> userRepository.findByEmail(username)
            .orElseThrow(() -> new NotFoundException("user not found"));
    }

    @Bean
    public AuthenticationProvider authenticationProvider()
    {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception
    {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException
    {
        GoogleCredentials googleCredentials = GoogleCredentials
            .fromStream(
                new ClassPathResource("firebase-service-account.json").getInputStream()
            );

        FirebaseOptions firebaseOptions = FirebaseOptions
            .builder()
            .setCredentials(googleCredentials)
            .build();

        FirebaseApp app = FirebaseApp.initializeApp(firebaseOptions,"mrfixit");

        return FirebaseMessaging.getInstance(app);
    }
}