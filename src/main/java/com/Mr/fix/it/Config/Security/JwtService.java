package com.Mr.fix.it.Config.Security;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.userdetails.UserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.security.Key;

@Service
public class JwtService
{
    @Value(value = "${secret.key}")
    private String SECRET_KEY;

    public String extractUsername(String token)
    {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver)
    {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails, long lifeTime)
    {
        return generateToken(new HashMap<>(), userDetails, lifeTime);
    }

    public String generateToken(Map<String,Object> extraClaims, UserDetails userDetails, long lifeTime)
    {
        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + lifeTime))
            .signWith(getSignInKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails)
    {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public Date extractExpiration(String token)
    {
        return extractClaim(token, Claims::getExpiration);
    }

    public Claims extractAllClaims(String token)
    {
        return Jwts
            .parserBuilder()
            .setSigningKey(getSignInKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    private boolean isTokenExpired(String token)
    {
        return extractExpiration(token).before(new Date());
    }

    private Key getSignInKey()
    {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}