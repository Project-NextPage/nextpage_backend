package com.nextpage.backend.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Service
public class TokenService {
    // JWT 토큰 생성
    private Key secretKey;

    @Value("${jwt.token.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.token.expire-length}")
    private Long EXPIRE_LENGTH; // Jwt 토큰의 만료 시간

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateToken(Long userId) {
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));
//        claims.put("email", email);
//
//        // 사용자의 이름을 Base64형식에서 UTF-8로 인코딩
//        String encodedName = Base64.getEncoder().encodeToString(name.getBytes(StandardCharsets.UTF_8));
//        claims.put("name", encodedName);

        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_LENGTH))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey)
                    .build().parseClaimsJws(token);

            return claims.getBody().getExpiration()
                    .after(new Date(System.currentTimeMillis()));

        } catch (Exception e) {
            return false;
        }
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build().parseClaimsJws(token).getBody().getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
}
