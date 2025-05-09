package com.fiitimprove.backend.services;

import com.fiitimprove.backend.dto.AuthentificationResponse;
import com.fiitimprove.backend.requests.SignInRequest;
import com.fiitimprove.backend.models.User;
import com.fiitimprove.backend.repositories.UserRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthentificationResponse signUp(User user) {
        String accessToken = generateAccessToken(user.getId());
        return new AuthentificationResponse(user.getId(), user.getName(), user.getRole(), accessToken);
    }

    public String generateAccessToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 360000000)) // 10 година
                .signWith(key)
                .compact();
    }

    public AuthentificationResponse signIn(SignInRequest signInRequest) {
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with this email does not exist"));
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password incorrect");
        }
        String accessToken = generateAccessToken(user.getId());
        return new AuthentificationResponse(user.getId(), user.getName(), user.getRole(), accessToken);
    }



    public Long extractUserId(String token) {
        Claims claims = parseToken(token);
        if (claims == null) {
            throw new IllegalArgumentException("Invalid JWT token");
        }
        if (isTokenExpired(claims)) {
            throw new IllegalArgumentException("Token has expired");
        }
        return Long.parseLong(claims.getSubject());
    }


    public boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    public User getUserData(String token) throws Exception {
        Claims claims = parseToken(token);
        if (claims == null) {
            throw new Exception("Invalid token");
        }
        if (isTokenExpired(claims)) {
            throw new Exception("Token has expired");
        }

        Long userId = Long.parseLong(claims.getSubject());
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new Exception("User not found");
        }
        return userOptional.get();
    }

    public Claims parseToken(String token) {
        try {
            Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            return null;
        }
    }
}