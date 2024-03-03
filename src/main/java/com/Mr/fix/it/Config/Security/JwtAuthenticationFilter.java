package com.Mr.fix.it.Config.Security;

import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import org.springframework.http.ResponseEntity;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import io.jsonwebtoken.ExpiredJwtException;

import com.Mr.fix.it.Exception.Exception_Handler.ExpiredJwtExceptionHandler;
import com.Mr.fix.it.Exception.ExceptionResponse.ExceptionResponse;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    private final JwtService jwtService;

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException,
            IOException
    {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        if(authHeader == null || !authHeader.startsWith("Bearer "))
        {
            filterChain.doFilter(request,response);
            return;
        }

        jwt = authHeader.substring(7);

        try
        {
            userEmail = jwtService.extractUsername(jwt);
        }
        catch(ExpiredJwtException e)
        {
            ExpiredJwtExceptionHandler expiredJwtExceptionHandler = new ExpiredJwtExceptionHandler();
            ResponseEntity<ExceptionResponse> exceptionResponse = expiredJwtExceptionHandler.handleException(e);

            response.setStatus(exceptionResponse.getStatusCode().value());
            response.getWriter().println(new ObjectMapper().writeValueAsString(exceptionResponse.getBody()));

            return;
        }

        if(userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            if(jwtService.isTokenValid(jwt,userDetails))
            {
                UsernamePasswordAuthenticationToken authToken = new  UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities()
                );

                authToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request,response);
    }
}