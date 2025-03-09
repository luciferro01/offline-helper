package com.mohil_bansal.authorizationserver.service;

import com.mohil_bansal.authorizationserver.dto.LoginRequestDto;
import com.mohil_bansal.authorizationserver.dto.RefreshRequestDto;
import com.mohil_bansal.authorizationserver.dto.RegisterRequestDto;
import com.mohil_bansal.authorizationserver.dto.TokenResponseDto;

public interface UserService {
    RegisterRequestDto register(RegisterRequestDto user);
    boolean existsByEmail(String email);

    TokenResponseDto login(LoginRequestDto user);

    TokenResponseDto refreshToken(RefreshRequestDto refreshToken);

    boolean logout(String token);

    boolean validateToken(String token);
}
