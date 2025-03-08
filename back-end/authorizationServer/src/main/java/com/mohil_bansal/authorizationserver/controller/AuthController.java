package com.mohil_bansal.authorizationserver.controller;


import com.mohil_bansal.authorizationserver.dto.LoginRequest;
import com.mohil_bansal.authorizationserver.dto.RefreshRequest;
import com.mohil_bansal.authorizationserver.dto.TokenResponse;
import com.mohil_bansal.authorizationserver.entity.User;
import com.mohil_bansal.authorizationserver.exception.ResourceNotFoundException;
import com.mohil_bansal.authorizationserver.repository.UserRepository;
import com.mohil_bansal.authorizationserver.util.CommonResponse;
import com.mohil_bansal.authorizationserver.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return ResponseEntity.ok("User registered");
    }

    //    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
//        );
//        User user = userRepository.findByEmail(request.getEmail());
//        String accessToken = jwtTokenUtil.generateToken(user);
//        String refreshToken = UUID.randomUUID().toString();
//        redisTemplate.opsForValue().set(refreshToken, user.getId().toString(), 7, TimeUnit.DAYS);
//        return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
//    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            User user = userRepository.findByEmail(request.getEmail());
            String accessToken = jwtTokenUtil.generateToken(user);
            String refreshToken = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set(refreshToken, user.getId().toString(), 7, TimeUnit.DAYS);

            return ResponseEntity.ok(new TokenResponse(accessToken, refreshToken));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CommonResponse<>(false, 500, null, e.getMessage()));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        String userId = redisTemplate.opsForValue().get(request.getRefreshToken());
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
        }
        User user = userRepository.findById(Long.parseLong(userId)).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        String newAccessToken = jwtTokenUtil.generateToken(user);
        return ResponseEntity.ok(new TokenResponse(newAccessToken, request.getRefreshToken()));
    }
}
