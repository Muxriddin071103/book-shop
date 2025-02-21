package uz.app.config;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import uz.app.entity.User;

import java.security.Key;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${my_token.key}")
    private String key;

    @Value("${my_token.expire_time}")
    private Long expireTime;

    public String generateToken(User userDetails) {
        Date expiryDate = new Date(System.currentTimeMillis() + expireTime);
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setSubject(userDetails.getPhoneNumber())
                .setExpiration(expiryDate)
                .signWith(signKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Key signKey() {
        return Keys.hmacShaKeyFor(key.getBytes());
    }

    public String getSubject(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("Invalid or missing Authorization header");
        }

        try {
            String token = authHeader.substring(7);
            return Jwts.parserBuilder()
                    .setSigningKey(signKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("JWT parsing failed: " + e.getMessage());
        }
    }
}
