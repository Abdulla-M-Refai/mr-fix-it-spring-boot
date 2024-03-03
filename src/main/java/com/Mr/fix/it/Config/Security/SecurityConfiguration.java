package com.Mr.fix.it.Config.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration
{
    private final JwtAuthenticationFilter jwtAuthFilter;

    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        http
            .csrf()
            .disable()
            .authorizeRequests(authorizeRequests -> authorizeRequests
                .requestMatchers(
                "api/auth/**",
                "api/admin/**",
                "api/client/client-donation",
                "ws",
                "app/chat",
                "api/worker/subscribe-featured",
                "uploads/reels/**",
                "uploads/ads/**",
                "uploads/**",
                "uploads/chat_img/**",
                "uploads/previous_work/**",
                "uploads/profile_picture/**",
                "uploads/task_img/**"
                ).permitAll()
                .requestMatchers("/api/client/**").hasAuthority("CLIENT")
                .requestMatchers("/api/worker/**").hasAuthority("WORKER")
                .requestMatchers("/api/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic()
            .and()
            .sessionManagement(sessionManagement ->
                    sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}