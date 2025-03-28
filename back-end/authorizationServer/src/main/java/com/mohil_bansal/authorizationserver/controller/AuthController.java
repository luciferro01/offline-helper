package com.mohil_bansal.authorizationserver.controller;

import com.mohil_bansal.authorizationserver.dto.LoginRequestDto;
import com.mohil_bansal.authorizationserver.dto.RefreshRequestDto;
import com.mohil_bansal.authorizationserver.dto.RegisterRequestDto;
import com.mohil_bansal.authorizationserver.dto.TokenResponseDto;
import com.mohil_bansal.authorizationserver.service.UserService;
import com.mohil_bansal.authorizationserver.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<CommonResponse<RegisterRequestDto>> register(@RequestBody RegisterRequestDto user) {
        try {
            RegisterRequestDto registeredUser = userService.register(user);
            return ResponseEntity.ok(
                    CommonResponse.success(registeredUser, 200, "User registered successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.failure(e.getMessage(), 500));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<CommonResponse<TokenResponseDto>> login(@RequestBody LoginRequestDto request) {
        try {
            TokenResponseDto tokenResponse = userService.login(request);
            return ResponseEntity.ok(
                    CommonResponse.success(tokenResponse, 200, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.failure("Invalid credentials", 401));
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<CommonResponse<TokenResponseDto>> refresh(@RequestBody RefreshRequestDto request) {
        try {
            TokenResponseDto tokenResponse = userService.refreshToken(request);
            return ResponseEntity.ok(
                    CommonResponse.success(tokenResponse, 200, "Token refreshed successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(CommonResponse.failure(e.getMessage(), 401));
        }
    }

    @GetMapping("/is-authorized")
    public ResponseEntity<CommonResponse<Boolean>> isAuthorized(@RequestHeader("Authorization") String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = userService.validateToken(token);
            return ResponseEntity.ok(
                    CommonResponse.success(isValid, 200,
                            isValid ? "User is authorized" : "User is not authorized"));
        }
        return ResponseEntity.ok(
                CommonResponse.failure("No valid token provided", 200));
    }

    @GetMapping("/ok")
    public  ResponseEntity<CommonResponse<String>> ok(){
        return ResponseEntity.ok(
                CommonResponse.success("Yes", 200, "Yes"));
    }

    @GetMapping("/validate")
    public ResponseEntity<CommonResponse<Boolean>> validateToken(@RequestParam String token) {
        boolean isValid = userService.validateToken(token);
        return ResponseEntity.ok(
                CommonResponse.success(isValid, 200, isValid ? "Token is valid" : "Token is invalid"));
    }

    @PostMapping("/logout")
    public ResponseEntity<CommonResponse<Boolean>> logout(@RequestParam String refreshToken) {
        boolean loggedOut = userService.logout(refreshToken);
        return ResponseEntity.ok(
                CommonResponse.success(loggedOut, 200, loggedOut ? "Logged out successfully" : "Logout failed"));
    }
}