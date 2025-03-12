package com.mohil_bansal.authorizationserver.dto;

public class LogInResponseDto {

    private String email;
    private String name;
    private Long id;
    private String accessToken;
    private String refreshToken;

    public LogInResponseDto(String email, String name, Long id, String accessToken, String refreshToken) {
        this.email = email;
        this.name = name;
        this.id = id;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}