package com.mohil_bansal.authorizationserver.service.impl;

import com.mohil_bansal.authorizationserver.dto.LoginRequestDto;
import com.mohil_bansal.authorizationserver.dto.RefreshRequestDto;
import com.mohil_bansal.authorizationserver.dto.RegisterRequestDto;
import com.mohil_bansal.authorizationserver.dto.TokenResponseDto;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
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
        User user = new User();
//        user.setName(registerRequest.getName());
//        user.setEmail(registerRequest.getEmail());
//        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        BeanUtils.copyProperties(registerRequest, user);
        if (existsByEmail(registerRequest.getEmail())) {
            throw new DataAlreadyExistsException("User already exists with email: " + registerRequest.getEmail());
        }
        userRepository.save(user);
        return registerRequest;
    }

    @Override
    public boolean existsByEmail(String email) {
        User user = userRepository.findByEmail(email);
        return user != null;
    }

    @Override
    public TokenResponseDto login(LoginRequestDto loginRequest) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
//        );

        User user = userRepository.findByEmail(loginRequest.getEmail());
        String accessToken = jwtTokenUtil.generateToken(user);
        String refreshToken = UUID.randomUUID().toString();

        // Store refresh token in Redis with user ID as value
        redisTemplate.opsForValue().set(refreshToken, user.getId().toString(), 7, TimeUnit.DAYS);

        return new TokenResponseDto(accessToken, refreshToken);
    }

    @Override
    public TokenResponseDto refreshToken(RefreshRequestDto refreshRequest) {
        String userId = redisTemplate.opsForValue().get(refreshRequest.getRefreshToken());
        if (userId == null) {
            throw new ResourceNotFoundException("Invalid refresh token");
        }

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newAccessToken = jwtTokenUtil.generateToken(user);
        return new TokenResponseDto(newAccessToken, refreshRequest.getRefreshToken());
    }

    @Override
    public boolean logout(String token) {
        // Delete refresh token from Redis
        return Boolean.TRUE.equals(redisTemplate.delete(token));
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
}