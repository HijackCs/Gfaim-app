package com.gfaim.auth;

import com.gfaim.api.AuthService;
import com.gfaim.models.AuthResponse;
import com.gfaim.models.RefreshRequest;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

public class TokenAuthenticator implements Authenticator {
    private TokenManager tokenManager;
    private AuthService authService;

    public TokenAuthenticator(TokenManager tokenManager, AuthService authService) {
        this.tokenManager = tokenManager;
        this.authService = authService;
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        String refreshToken = tokenManager.getRefreshToken();

        if (refreshToken != null) {
            Call<AuthResponse> call = authService.refreshToken(new RefreshRequest(refreshToken));
            retrofit2.Response<AuthResponse> refreshResponse = call.execute();

            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                AuthResponse newTokens = refreshResponse.body();
                tokenManager.saveTokens(newTokens.getAccessToken(), newTokens.getRefreshToken());

                return response.request().newBuilder()
                        .header("Authorization", "Bearer " + newTokens.getAccessToken())
                        .build();
            }
        }
        return null; // Échec de l'authentification
    }

}
