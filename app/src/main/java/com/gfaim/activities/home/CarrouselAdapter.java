package com.gfaim.activities.home;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;
import com.gfaim.activities.calendar.adapter.MealAdapter;
import com.gfaim.activities.recipe.fragments.RecipeActivity;
import com.gfaim.api.ApiClient;
import com.gfaim.api.MealService;
import com.gfaim.models.MealResponseBody;
import com.gfaim.models.member.CreateMember;
import com.gfaim.models.member.CreateMemberNoAccount;
import com.gfaim.models.member.MemberSessionBody;
import com.gfaim.utility.api.UtileProfile;
import com.gfaim.utility.callback.OnMemberReceivedListener;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarrouselAdapter extends RecyclerView.Adapter<CarrouselAdapter.CarrouselViewHolder> {

    private final List<Integer> images;
    private final List<String> textes;
    private UtileProfile utileProfile;
    private MemberSessionBody member;
    private Context context;
    private String selectedDate;

    public CarrouselAdapter(List<Integer> images, List<String> texts) {
        this.images = images;
        this.textes = texts;
        this.selectedDate = getCurrentDate();
    }

    private String getCurrentDate() {
        java.time.LocalDate today = java.time.LocalDate.now();
        return today.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    @NonNull
    @Override
    public CarrouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrousel, parent, false);
        return new CarrouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarrouselViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.imageView.setImageResource(images.get(position));
        holder.textView.setText(textes.get(position));

        utileProfile = new UtileProfile(context);

        utileProfile.getSessionMember(new OnMemberReceivedListener() {
            @Override
            public void onSuccess(CreateMemberNoAccount session) {}
            @Override
            public void onSuccess(MemberSessionBody session) {
                member = session;
                holder.imageView.setOnClickListener(v -> {
                    Toast.makeText(v.getContext(), textes.get(position), Toast.LENGTH_SHORT).show();

                    getMeal(textes.get(position));
                });
            }
            @Override
            public void onFailure(Throwable error) {}
            @Override
            public void onSuccess(CreateMember body) {}
        });

        // Ajouter un gestionnaire de clic sur l'image

    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    private String convertMealType(String mealType) {
        switch (mealType) {
            case "Breakfast":
                return "BREAKFAST";
            case "Lunch":
                return "LUNCH";
            case "Dinner":
                return "DINNER";
            default:
                return "BREAKFAST";
        }
    }

    public void getMeal(String mealType) {
        MealService service = ApiClient.getClient(context).create(MealService.class);
        System.out.println("date " + selectedDate);

        String apiMealType = convertMealType(mealType);
        Log.d(TAG, "Début du chargement des repas pour le type: " + apiMealType + " à la date: " + selectedDate);
        Call<List<MealResponseBody>> call = service.getMeal(member.getFamilyId(), selectedDate, apiMealType);

        call.enqueue(new Callback<List<MealResponseBody>>() {
            @Override
            public void onResponse(Call<List<MealResponseBody>> call, Response<List<MealResponseBody>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    MealResponseBody meal = response.body().get(0);
                    Intent intent = new Intent(context, RecipeActivity.class);
                    intent.putExtra("id", meal.getRecipe().getId());
                    context.startActivity(intent);
                } else {
                }
            }

            @Override
            public void onFailure(Call<List<MealResponseBody>> call, Throwable t) {
                Log.e(TAG, "Erreur de connexion: " + t.getMessage());
            }
        });
    }

    public static class CarrouselViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;
        public CarrouselViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);
        }
    }
}