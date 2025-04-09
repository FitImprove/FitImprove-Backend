package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.AuthentificationResponse;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import io.jsonwebtoken.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

import static io.jsonwebtoken.Jwts.*;


@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    public AuthentificationResponse signUp(User user) {
        String accessToken = generateAccessToken(user.getId(), user.getEmail());
        return new AuthentificationResponse(user.getId(), user.getName(),accessToken);
    }
    private String generateAccessToken(Long userId, String email) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return builder()
                .setSubject(userId.toString())
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000))
                .signWith(key)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public Long extractUserId(String token) {
        JwtParserBuilder parserBuilder = Jwts.parser().setSigningKey(getSigningKey());
        JwtParser parser = parserBuilder.build();
        Claims claims = parser.parseClaimsJws(token).getPayload();
        return Long.parseLong(claims.getSubject());
    }
}
