package com.gfaim.models.auth;

import lombok.Getter;

@Getter
public class ForgotPasswordRequest {
    private String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

}
