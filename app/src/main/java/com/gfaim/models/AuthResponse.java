package com.gfaim.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AuthResponse {
    private String accessToken;
    private String refreshToken;

}

