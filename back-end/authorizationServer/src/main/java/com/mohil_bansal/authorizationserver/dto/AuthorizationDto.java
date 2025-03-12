package com.mohil_bansal.authorizationserver.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AuthorizationDto {
    private String userId;
    private boolean valid;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    public AuthorizationDto(String userId, boolean valid) {
        this.userId = userId;
        this.valid = valid;
    }
}
