package org.example.identityservice.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.example.identityservice.entity.User;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private final String jwtSecret;
    private final long jwtExpiration;

    public JwtUtil(
            @Value("${jwt-secret}") String jwtSecret,
            @Value("${jwt-expiration}") long jwtExpiration
    ) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }

    // Lấy Secret Key để ký JWT
    private Key getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Tạo JWT từ thông tin User
    public String generateToken(User user) {

        Date now = new Date();

        Date expiration = new Date(
                now.getTime() + jwtExpiration
        );

        return Jwts.builder()

                // Subject: username
                .setSubject(user.getUsername())

                // Custom claim: role
                .claim("role", user.getRole())

                // Thời gian tạo token
                .setIssuedAt(now)

                // Thời gian hết hạn
                .setExpiration(expiration)

                // Ký token bằng HS256
                .signWith(
                        getSigningKey(),
                        SignatureAlgorithm.HS256
                )

                // Tạo chuỗi JWT
                .compact();
    }
}
