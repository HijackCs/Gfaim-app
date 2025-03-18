package com.gfaim.utility.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.gfaim.activities.auth.LoginActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.AuthService;
import com.gfaim.api.FamilyService;
import com.gfaim.api.MemberService;
import com.gfaim.auth.TokenManager;
import com.gfaim.models.family.CreateFamilyBody;
import com.gfaim.models.family.FamilyBody;
import com.gfaim.models.RefreshRequest;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.CreateSelfMemberBody;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.models.member.UpdateMember;
import com.gfaim.utility.callback.OnFamilyReceivedListener;
import com.gfaim.utility.callback.OnSessionReceivedListener;
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

    public void updateMember(Long id, String firstName, String lastName){
        MemberService memberService = ApiClient.getClient(context).create(MemberService.class);
        Call<UpdateMember> call = memberService.updateMember(id,"Bearer " + tokenManager.getAccessToken(), new UpdateMember(firstName, lastName));
        call.enqueue(new Callback<UpdateMember>() {
            @Override
            public void onResponse(Call<UpdateMember> call, Response<UpdateMember> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Error update", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<UpdateMember> call, Throwable t) {
                Toast.makeText(context, "Erreur: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateFamily(OnFamilyReceivedListener listener, Long id, String familyName){
        FamilyService familyService = ApiClient.getClient(context).create(FamilyService.class);
        Call<CreateFamilyBody> call = familyService.updateFamily(id,"Bearer " + tokenManager.getAccessToken(), new CreateFamilyBody(familyName));
        call.enqueue(new Callback<CreateFamilyBody>() {
            @Override
            public void onResponse(Call<CreateFamilyBody> call, Response<CreateFamilyBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Toast.makeText(context, "Profil mis à jour avec succès", Toast.LENGTH_SHORT).show();
                    System.out.println("la");
                    System.out.println(response.body());
                    listener.onSuccess(response.body());

                } else {
                    listener.onFailure(new Exception("Réponse invalide"));
                }
            }
            @Override
            public void onFailure(Call<CreateFamilyBody> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }

    public void getSessionMember(OnSessionReceivedListener listener) {
        MemberService memberService = ApiClient.getClient(context).create(MemberService.class);
        Call<MemberSessionBody> call = memberService.getMemberSession();

        call.enqueue(new Callback<MemberSessionBody>() {
            @Override
            public void onResponse(Call<MemberSessionBody> call, Response<MemberSessionBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(new Exception("Réponse invalide"));
                }
            }

            @Override
            public void onFailure(Call<MemberSessionBody> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }


    public void getFamily(OnFamilyReceivedListener listener, Long id) {
        FamilyService familyService = ApiClient.getClient(context).create(FamilyService.class);
        Call<FamilyBody> call = familyService.getFamily(id, "Bearer " + tokenManager.getAccessToken());

        call.enqueue(new Callback<FamilyBody>() {
            @Override
            public void onResponse(Call<FamilyBody> call, Response<FamilyBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(new Exception("Réponse invalide"));
                }
            }

            @Override
            public void onFailure(Call<FamilyBody> call, Throwable t) {
                listener.onFailure(t);
            }
        });
    }


    public void deleteMemberById(OnFamilyReceivedListener listener, Long memberId) {
        MemberService memberService = ApiClient.getClient(context).create(MemberService.class);
        Call<MemberSessionBody> call = memberService.deleteMember(memberId);

        // Appel asynchrone
        call.enqueue(new Callback<MemberSessionBody>() {
            @Override
            public void onResponse(Call<MemberSessionBody> call, Response<MemberSessionBody> response) {
                if (response.isSuccessful()) {
                    // Traitement du succès, le membre a bien été supprimé
                    listener.onSuccess();
                    System.out.println("Membre supprimé avec succès.");
                } else {
                    listener.onFailure(new Exception("Réponse invalide"));
                    // Gestion d'erreur si la réponse est invalides (code HTTP 4xx ou 5xx)
                    System.err.println("Erreur lors de la suppression : " + response.message());
                }
            }

            @Override
            public void onFailure(Call<MemberSessionBody> call, Throwable t) {
                // Gestion des erreurs réseau (problèmes de connexion, etc.)
                listener.onFailure(t);
                System.err.println("Erreur réseau : " + t.getMessage());
            }
        });
    }

    public void createMember(OnSessionReceivedListener listener, String codeFamily, String name ,String familyName) {
        //create member
        MemberService memberService = ApiClient.getClient(context).create(MemberService.class);
        Call<CreateMemberNoAccount> call = memberService.createMember("bearer " + tokenManager.getAccessToken(), new CreateMemberNoAccount(false, codeFamily, name, familyName));
        call.enqueue(new Callback<CreateMemberNoAccount>() {
            @Override
            public void onResponse(Call<CreateMemberNoAccount> call, Response<CreateMemberNoAccount> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listener.onSuccess(response.body());
                } else {
                    listener.onFailure(new Exception("Réponse invalide"));
                }
            }

            @Override
            public void onFailure(Call<CreateMemberNoAccount> call, Throwable t) {
                listener.onFailure(t);            }
        });
    }






}
