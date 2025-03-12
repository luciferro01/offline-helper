package com.mohil_bansal.authorizationserver.service.impl;

import com.mohil_bansal.authorizationserver.dto.*;
import com.mohil_bansal.authorizationserver.entity.User;
import com.mohil_bansal.authorizationserver.exception.DataAlreadyExistsException;
import com.mohil_bansal.authorizationserver.exception.ResourceNotFoundException;
import com.mohil_bansal.authorizationserver.repository.UserRepository;
import com.mohil_bansal.authorizationserver.service.UserService;
import com.mohil_bansal.authorizationserver.util.JwtTokenUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.mohil_bansal.authorizationserver.util.JwtTokenUtil.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public RegisterRequestDto register(RegisterRequestDto registerRequest) {

        if (existsByEmail(registerRequest.getEmail())) {
            throw new DataAlreadyExistsException("User already exists with email: " + registerRequest.getEmail());
        }

        User user = new User();
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = passwordEncoder.encode(registerRequest.getPassword());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(encodedPassword);
        user.setName(registerRequest.getName());

        userRepository.save(user);
        return registerRequest;
    }

    @Override
    public boolean existsByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    //Brute Force Way

//    @Override
//    public LogInResponseDto login(LoginRequestDto loginRequest) {
//        // Find user by email
//        User user = userRepository.findByEmail(loginRequest.getEmail());
//        if (user == null) {
//            throw new ResourceNotFoundException("User not found");
//        }
//
//        // Verifying passwords using matching
//
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
//            throw new ResourceNotFoundException("Invalid password");
//        }
//
//
//        String accessToken;
//        String refreshToken;
//
//        String userId = user.getId().toString();
//        Set<String> existingRefreshTokens = redisTemplate.keys("*");
//
//
//        if (existingRefreshTokens != null) {
//            // Find existing refresh token for user
//            Optional<String> existingRefreshToken = existingRefreshTokens.stream()
//                    .filter(key -> userId.equals(redisTemplate.opsForValue().get(key)))
//                    .findFirst();
//
//            if (existingRefreshToken.isPresent()) {
//                refreshToken = existingRefreshToken.get();
//                // Generate new access token since it has shorter validity
//                accessToken = jwtTokenUtil.generateToken(user);
//                redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
//            } else {
//                // Create new tokens if none exist
//                accessToken = jwtTokenUtil.generateToken(user);
//                refreshToken = UUID.randomUUID().toString();
//                redisTemplate.opsForValue().set(refreshToken, userId, 7, TimeUnit.DAYS);
//                redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
//            }
//        } else {
//            // Create new tokens if Redis is empty
//            accessToken = jwtTokenUtil.generateToken(user);
//            refreshToken = UUID.randomUUID().toString();
//            redisTemplate.opsForValue().set(refreshToken, userId, 7, TimeUnit.DAYS);
//            redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
//        }
//
//

    /// /        // Generate tokens
    /// /        String accessToken = jwtTokenUtil.generateToken(user);
    /// /        String refreshToken = UUID.randomUUID().toString();
    /// /
    /// /        // Store tokens in Redis
    /// /        redisTemplate.opsForValue().set(refreshToken, user.getId().toString(), 7, TimeUnit.DAYS);
    /// /        redisTemplate.opsForValue().set(accessToken, user.getId().toString(), 1, TimeUnit.HOURS);
//
//        return new LogInResponseDto(user.getEmail(), user.getName(), user.getId(), accessToken, refreshToken);
//    }


    // Optimal Approach

    @Override
    public LogInResponseDto login(LoginRequestDto loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid credentials");
        }

        String userId = user.getId().toString();
        String userKey = USER_TOKENS_PREFIX + userId;

        String existingRefreshToken = redisTemplate.opsForValue().get(userKey);
        String existingAccessToken = null;

        if (existingRefreshToken != null) {
            Set<String> accessKeys = redisTemplate.keys(ACCESS_TOKEN_PREFIX + "*");
            if (accessKeys != null) {
                Optional<String> validAccessToken = accessKeys.stream()
                        .filter(key -> userId.equals(redisTemplate.opsForValue().get(key)))
                        .findFirst();

                if (validAccessToken.isPresent()) {
                    existingAccessToken = validAccessToken.get().substring(ACCESS_TOKEN_PREFIX.length());
                }
            }
        }

        String accessToken = existingAccessToken != null ? existingAccessToken : jwtTokenUtil.generateToken(user);
        String refreshToken;

        if (existingRefreshToken == null) {
            refreshToken = UUID.randomUUID().toString();
            // Storing refresh token reference
            redisTemplate.opsForValue().set(userKey, refreshToken, 7, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(REFRESH_TOKEN_PREFIX + refreshToken, userId, 7, TimeUnit.DAYS);
        } else {
            refreshToken = existingRefreshToken;
        }

        // Storing access token if it is not available
        if (existingAccessToken == null) {
            redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + accessToken, userId, 1, TimeUnit.HOURS);
        }

        return new LogInResponseDto(user.getEmail(), user.getName(), user.getId(), accessToken, refreshToken);
    }

    @Override
    public TokenResponseDto refreshToken(RefreshRequestDto refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();
        String userId = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);

        if (userId == null) {
            throw new ResourceNotFoundException("Invalid refresh token");
        }

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));


        String existingAccessToken = null;
        Set<String> accessKeys = redisTemplate.keys(ACCESS_TOKEN_PREFIX + "*");

        if (accessKeys != null) {
            Optional<String> validAccessToken = accessKeys.stream()
                    .filter(key -> userId.equals(redisTemplate.opsForValue().get(key)))
                    .findFirst();

            if (validAccessToken.isPresent()) {
                existingAccessToken = validAccessToken.get().substring(ACCESS_TOKEN_PREFIX.length());
            }
        }

        String accessToken = existingAccessToken != null ? existingAccessToken : jwtTokenUtil.generateToken(user);

        // Store new access token only if created
        if (existingAccessToken == null) {
            redisTemplate.opsForValue().set(ACCESS_TOKEN_PREFIX + accessToken, userId, 1, TimeUnit.HOURS);
        }

        return new TokenResponseDto(accessToken, refreshToken);
    }


    @Override
    public boolean logout(String refreshToken) {
        try {
            String userId = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + refreshToken);
            if (userId == null) {
                return false;
            }

            redisTemplate.delete(USER_TOKENS_PREFIX + userId);

            redisTemplate.delete(REFRESH_TOKEN_PREFIX + refreshToken);

            Set<String> accessKeys = redisTemplate.keys(ACCESS_TOKEN_PREFIX + "*");
            if (accessKeys != null) {
                accessKeys.stream()
                        .filter(key -> userId.equals(redisTemplate.opsForValue().get(key)))
                        .forEach(redisTemplate::delete);
            }

            return true;
        } catch (Exception e) {
//            log.error("Error during logout: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validateToken(String token) {
        if (!jwtTokenUtil.validateToken(token)) {
            return false;
        }
        Long userId = Long.parseLong(jwtTokenUtil.getUserIdFromToken(token));
        userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return true;
    }

    @Override
    public AuthorizationDto checkAuthorization(String token) {
        try {
            if (!jwtTokenUtil.validateToken(token)) {
                return new AuthorizationDto(null, false);
            }
            String userId = jwtTokenUtil.getUserIdFromToken(token);
            userRepository.findById(Long.parseLong(userId))
                    .orElseThrow(() -> new ResourceNotFoundException("User not found"));
            return new AuthorizationDto(userId, true);
        } catch (Exception e) {
//            log.error("Error checking authorization: {}", e.getMessage());
            return new AuthorizationDto(null, false);
        }
    }
}