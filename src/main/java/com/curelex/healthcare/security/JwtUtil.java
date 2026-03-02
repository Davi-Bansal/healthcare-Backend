package com.curelex.healthcare.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final String secret = "SECRETKEY1234567890SECRETKEY1234567890";

    private SecretKey getKey(){
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // ✅ NOW ACCEPT ROLE ALSO
    public String generateToken(String email, String role) {

        return Jwts.builder()
                .setSubject(email)

                // ⭐ IMPORTANT LINE (STEP-2 YOU ASKED)
                .claim("role", role)

                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // extract email
    public String extractEmail(String token) {

        return getClaims(token).getSubject();
    }

    // ⭐ NEW METHOD → extract role
    public String extractRole(String token){

        return (String) getClaims(token).get("role");
    }

    private Claims getClaims(String token){

        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
