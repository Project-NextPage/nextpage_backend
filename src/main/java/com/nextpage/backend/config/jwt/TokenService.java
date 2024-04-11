package com.nextpage.backend.config.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Configuration
@Service
public class TokenService {
    private final Logger log = LoggerFactory.getLogger(getClass());
    private Key secretKey;

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    @Value("${jwt.token.access-expire-length}")
    private Long ACCESS_EXPIRE_LENGTH; // 액세스 토큰의 만료 시간

    @Value("${jwt.token.refresh-expire-length}")
    private Long REFRESH_EXPIRE_LENGTH; // 리프레시 토큰의 만료 시간

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
    }

    public String generateAccessToken(Long userId) { // todo: 액세스, 리프레시 토큰 생성 로직 구현
        Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

        return Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE_LENGTH))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        // refresh에는 별다른 유저 정보가 들어가지 않는다. claims 세팅 하지 않음
        return Jwts.builder()
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRE_LENGTH))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String reGenereteAccessToken(HttpServletRequest request) { // 액세스 토큰 재발급
        String accessToken;
        String refreshToken = resolveRefreshToken(request);

        validateRefreshToken(refreshToken); // 만료 검사
        Claims claims = Jwts.claims().setSubject(String.valueOf(getUserIdFromToken(request))); // id 정보 가져오기
        accessToken = Jwts.builder().setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRE_LENGTH))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();

        return accessToken;
    }

    public String resolveAccessToken(HttpServletRequest request) {
        return request.getHeader("ACCESS-TOKEN");
    }

    public String resolveRefreshToken(HttpServletRequest request) {
        return request.getHeader("REFRESH-TOKEN");
    }

    public void validateAccessToken(String token) { // 만료 여부 반환
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey)
                    .build().parseClaimsJws(token);
            claims.getBody().getExpiration().after(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException ex) {
            log.error("토큰이 만료되었습니다.");
            throw new RuntimeException("토큰이 만료되었습니다.");
        }
    }

    public void validateRefreshToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(secretKey)
                    .build().parseClaimsJws(token);
            claims.getBody().getExpiration().after(new Date(System.currentTimeMillis()));
        } catch (ExpiredJwtException ex) {
            log.error("refresh 토큰이 만료되었습니다.");
            throw new RuntimeException("refresh 토큰이 만료되었습니다.");
        }
    }

    public Long getUserIdFromToken(HttpServletRequest request) { // 토큰에서 userId 정보 꺼내기
        String token = resolveAccessToken(request);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return Long.parseLong(claims.getSubject());
    }
}