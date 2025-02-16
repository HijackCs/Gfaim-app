package com.gfaim.activities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gfaim.R;

import java.util.List;

public class CarrouselAdapter extends RecyclerView.Adapter<CarrouselAdapter.CarrouselViewHolder> {

    private final List<Integer> images;
    private final List<String> textes;

    public CarrouselAdapter(List<Integer> images, List<String> texts) {
        this.images = images;
        this.textes = texts;
    }

    @NonNull
    @Override
    public CarrouselViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_carrousel, parent, false);
        return new CarrouselViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CarrouselViewHolder holder, int position) {
        holder.imageView.setImageResource(images.get(position));
        holder.textView.setText(textes.get(position));
    }

    @Override
    public int getItemCount() {
        return images.size();
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