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

/**
 * Service class responsible for handling JWT-based authentication,
 * including token creation, validation, and user extraction from token.
 */
@Service
public class JwtService {
    /**
     * Secret key used for signing JWT tokens, configured via application properties.
     */
    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Generates a JWT token for the given user and returns authentication response.
     *
     * @param user The user to generate token for.
     * @return Authentication response containing user info and token.
     */
    public AuthentificationResponse signUp(User user) {
        String accessToken = generateAccessToken(user.getId());
        return new AuthentificationResponse(user.getId(), user.getName(), user.getRole(), accessToken);
    }

    /**
     * Generates a JWT access token for the specified user ID.
     *
     * @param userId The ID of the user.
     * @return Signed JWT token.
     */
    public String generateAccessToken(Long userId) {
        Key key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(userId.toString())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 360000000)) // 10 година
                .signWith(key)
                .compact();
    }

    /**
     * Authenticates a user based on email and password, then returns authentication response.
     *
     * @param signInRequest Object containing email and password.
     * @return Authentication response containing user info and JWT token.
     * @throws IllegalArgumentException If email is not found or password is incorrect.
     */
    public AuthentificationResponse signIn(SignInRequest signInRequest) {
        User user = userRepository.findByEmail(signInRequest.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User with this email does not exist"));
        if (!passwordEncoder.matches(signInRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Password incorrect");
        }
        String accessToken = generateAccessToken(user.getId());
        return new AuthentificationResponse(user.getId(), user.getName(), user.getRole(), accessToken);
    }

    /**
     * Extracts user ID from the given JWT token after verifying its validity and expiration.
     *
     * @param token JWT token.
     * @return User ID extracted from token.
     * @throws IllegalArgumentException If token is invalid or expired.
     */
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

    /**
     * Checks whether the given token claims indicate an expired token.
     *
     * @param claims JWT claims.
     * @return True if the token is expired, false otherwise.
     */
    public boolean isTokenExpired(Claims claims) {
        Date expiration = claims.getExpiration();
        return expiration.before(new Date());
    }

    /**
     * Retrieves the full user data from a valid JWT token.
     *
     * @param token JWT token.
     * @return User corresponding to the token.
     * @throws Exception If token is invalid, expired, or user not found.
     */
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

    /**
     * Parses the JWT token and returns its claims.
     *
     * @param token JWT token.
     * @return Claims object if parsing succeeds; otherwise null.
     */
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