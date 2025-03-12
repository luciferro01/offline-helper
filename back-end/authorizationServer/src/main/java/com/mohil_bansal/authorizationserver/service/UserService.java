package com.mohil_bansal.authorizationserver.service;

import com.mohil_bansal.authorizationserver.dto.*;

public interface UserService {
    RegisterRequestDto register(RegisterRequestDto user);
    boolean existsByEmail(String email);

    LogInResponseDto login(LoginRequestDto user);

    TokenResponseDto refreshToken(RefreshRequestDto refreshToken);

    boolean logout(String token);

    boolean validateToken(String token);

    AuthorizationDto checkAuthorization(String token);
}
