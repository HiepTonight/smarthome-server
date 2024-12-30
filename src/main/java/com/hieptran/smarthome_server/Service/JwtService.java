package com.hieptran.smarthome_server.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hieptran.smarthome_server.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.LinkedHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    private ObjectMapper objectMapper;

    public String getUserIdFromToken(String authToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(authToken)
                    .getBody();

            return claims.get("user").toString();
        } catch (Exception e) {
            log.error("Decode token fail by error: ", e.getMessage());

            return null;
        }
    }

    public boolean validateToken(String authToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(authToken)
                .getPayload();

        System.out.println(claims.getExpiration());

        return !claims.getExpiration().before(new Date());
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("user", user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 15 * 60 * 1000)) // 15 minutes
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String refreshToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("user", user.getId().toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 1 day
                .signWith(SignatureAlgorithm.HS512, secretKey)
                .compact();
    }

    public String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }

    public void saveJwtTokenToRedis(String jwtToken, String userId) {

    }
}