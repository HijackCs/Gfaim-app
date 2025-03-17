package com.gfaim.utility.api;

import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.gfaim.R;
import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.activities.settings.SettingsActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.RefreshRequest;
import com.gfaim.utility.auth.JwtDecoder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UtileProfile {

    private Context context;
    private TokenManager tokenManager;

    private AuthService authService;


    public UtileProfile(Context context){
        this.context = context;
        tokenManager = new TokenManager(context);
        this.authService = ApiClient.getClient(context).create(AuthService .class);
    }

    public String getCompleteName() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            assert decodedToken != null;
            JsonObject jsonObject = JsonParser.parseString(decodedToken).getAsJsonObject();
            if (jsonObject.has("first_name") && jsonObject.has("last_name")) {
                String first_name = jsonObject.get("first_name").getAsString();
                String last_name = jsonObject.get("last_name").getAsString();
                return first_name + " " + last_name;

            }
        }
        return "";
    }

    public String getFirstName() {

        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            assert decodedToken != null;
            JsonObject jsonObject = JsonParser.parseString(decodedToken).getAsJsonObject();
            if (jsonObject.has("first_name")) {
                String first_name = jsonObject.get("first_name").getAsString();
                return first_name;
            }
        }
        return "";
    }

    public String getLastName() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            assert decodedToken != null;
            JsonObject jsonObject = JsonParser.parseString(decodedToken).getAsJsonObject();
            if (jsonObject.has("last_name")) {
                return jsonObject.get("last_name").getAsString();
            }
        }
        return "";
    }


    public String getUserEmail() {
        String accessToken = tokenManager.getAccessToken();
        if (accessToken != null) {
            String decodedToken = JwtDecoder.decodeJWT(accessToken);
            assert decodedToken != null;
            JsonObject jsonObject = JsonParser.parseString(decodedToken).getAsJsonObject();
            if (jsonObject.has("upn")) {
                System.out.println(jsonObject);
                return jsonObject.get("upn").getAsString();
            }
        }
        return "";
    }


    public void logout() {
        Call<Void> call = authService.logout(new RefreshRequest(tokenManager.getRefreshToken()));
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                tokenManager.clearTokens();
                Toast.makeText(context, "Déconnexion réussie", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(context, LoginActivity.class);
                context.startActivity(intent);
                if (context instanceof Activity) {
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
