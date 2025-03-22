package com.gfaim.auth;

import static com.gfaim.api.ApiClient.BASE_URL;
import static com.gfaim.utility.auth.ResponseStatus.UNAUTHORIZED;

import android.util.Log;

import com.gfaim.api.AuthService;
import com.gfaim.models.AuthResponse;
import com.gfaim.models.RefreshRequest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthInterceptor implements Interceptor {

    private static final String AUTH_INTERCEPTOR = "AuthInterceptor";
    private final TokenManager tokenManager;
    private final AuthService authService;

    private boolean isRefreshing = false;
    private final Object lock = new Object();

    public AuthInterceptor(TokenManager tokenManager) {
        this.tokenManager = tokenManager;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient.Builder().build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        this.authService = retrofit.create(AuthService.class);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        String accessToken = tokenManager.getAccessToken();

        Log.d(AUTH_INTERCEPTOR, "Tentative d'appel API avec accessToken: " + accessToken);

        Request request = originalRequest;
        if (accessToken != null) {
            request = originalRequest.newBuilder()
                    .header("Authorization", "Bearer " + accessToken)
                    .build();
        }

        Response response = chain.proceed(request);
        Log.d(AUTH_INTERCEPTOR, "Réponse reçue: " + response.code());

        if (response.code() == UNAUTHORIZED) {
            Log.w(AUTH_INTERCEPTOR, "401 détecté, tentative de refresh token...");
            response.close();

            synchronized (lock) {
                if (!isRefreshing) {
                    isRefreshing = true;
                    try {
                        String refreshToken = tokenManager.getRefreshToken();
                        Log.d(AUTH_INTERCEPTOR, "Token de refresh trouvé: " + refreshToken);

                        if (refreshToken != null) {
                            Call<AuthResponse> refreshCall = authService.refreshToken(new RefreshRequest(refreshToken));
                            retrofit2.Response<AuthResponse> refreshResponse = refreshCall.execute();

                            Log.d(AUTH_INTERCEPTOR, "Réponse du refresh: " + refreshResponse.code());

                            if (refreshResponse.isSuccessful() && refreshResponse.body() != null) {
                                AuthResponse newTokens = refreshResponse.body();
                                tokenManager.saveTokens(newTokens.getAccessToken(), newTokens.getRefreshToken());
                                Log.d(AUTH_INTERCEPTOR, "Nouveaux tokens sauvegardés.");

                                Request newRequest = originalRequest.newBuilder()
                                        .header("Authorization", "Bearer " + newTokens.getAccessToken())
                                        .build();

                                return chain.proceed(newRequest);
                            } else {
                                Log.e(AUTH_INTERCEPTOR, "Échec du refresh: " + refreshResponse.errorBody());
                                tokenManager.clearTokens();
                            }
                        }
                    } finally {
                        isRefreshing = false;
                    }
                }
            }
        }
        return response;
    }
}
