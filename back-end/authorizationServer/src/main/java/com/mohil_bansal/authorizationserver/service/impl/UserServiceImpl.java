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

    @Override
    public LogInResponseDto login(LoginRequestDto loginRequest) {
        // Find user by email
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        // Verifying passwords using matching

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid password");
        }


        String accessToken;
        String refreshToken;

        String userId = user.getId().toString();
        Set<String> existingRefreshTokens = redisTemplate.keys("*");


        if (existingRefreshTokens != null) {
            // Find existing refresh token for user
            Optional<String> existingRefreshToken = existingRefreshTokens.stream()
                    .filter(key -> userId.equals(redisTemplate.opsForValue().get(key)))
                    .findFirst();

            if (existingRefreshToken.isPresent()) {
                refreshToken = existingRefreshToken.get();
                // Generate new access token since it has shorter validity
                accessToken = jwtTokenUtil.generateToken(user);
                redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
            } else {
                // Create new tokens if none exist
                accessToken = jwtTokenUtil.generateToken(user);
                refreshToken = UUID.randomUUID().toString();
                redisTemplate.opsForValue().set(refreshToken, userId, 7, TimeUnit.DAYS);
                redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
            }
        } else {
            // Create new tokens if Redis is empty
            accessToken = jwtTokenUtil.generateToken(user);
            refreshToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(refreshToken, userId, 7, TimeUnit.DAYS);
            redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
        }


//        // Generate tokens
//        String accessToken = jwtTokenUtil.generateToken(user);
//        String refreshToken = UUID.randomUUID().toString();
//
//        // Store tokens in Redis
//        redisTemplate.opsForValue().set(refreshToken, user.getId().toString(), 7, TimeUnit.DAYS);
//        redisTemplate.opsForValue().set(accessToken, user.getId().toString(), 1, TimeUnit.HOURS);

        return new LogInResponseDto(user.getEmail(), user.getName(), user.getId(), accessToken, refreshToken);
    }

    //Old Refresh Token method
//    @Override
//    public TokenResponseDto refreshToken(RefreshRequestDto refreshRequest) {
//        String userId = redisTemplate.opsForValue().get(refreshRequest.getRefreshToken());
//        if (userId == null) {
//            throw new ResourceNotFoundException("Invalid refresh token");
//        }
//
//        User user = userRepository.findById(Long.parseLong(userId))
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//
//        String newAccessToken = jwtTokenUtil.generateToken(user);
//        return new TokenResponseDto(newAccessToken, refreshRequest.getRefreshToken());
//    }
    @Override
    public TokenResponseDto refreshToken(RefreshRequestDto refreshRequest) {
        // Validate refresh token
        String userId = redisTemplate.opsForValue().get(refreshRequest.getRefreshToken());
        if (userId == null) {
            throw new ResourceNotFoundException("Invalid refresh token");
        }

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check for existing access token
        Set<String> existingTokens = redisTemplate.keys("*");
        String accessToken;

        if (existingTokens != null) {
            Optional<String> existingAccessToken = existingTokens.stream()
                    .filter(key -> userId.equals(redisTemplate.opsForValue().get(key)) && !key.equals(refreshRequest.getRefreshToken()))
                    .findFirst();

            if (existingAccessToken.isPresent()) {
                accessToken = existingAccessToken.get();
            } else {
                accessToken = jwtTokenUtil.generateToken(user);
                redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
            }
        } else {
            // I don't think it will ever run just in case
            // Even though it is wrong
            accessToken = jwtTokenUtil.generateToken(user);
            redisTemplate.opsForValue().set(accessToken, userId, 1, TimeUnit.HOURS);
        }

        return new TokenResponseDto(accessToken, refreshRequest.getRefreshToken());
    }

    //TODO: Update Access Token and Refresh Token
    @Override
    public boolean logout(String token) {
        // Delete refresh token from Redis
        try {
            String userId = redisTemplate.opsForValue().get(redisTemplate.opsForValue().get(token));

            return Boolean.TRUE.equals(redisTemplate.delete(token));

        } catch (Exception e) {
//            log.error("Error while deleting token from redis: {}", e.getMessage());
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