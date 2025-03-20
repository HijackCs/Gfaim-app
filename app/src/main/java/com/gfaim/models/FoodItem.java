package com.gfaim.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Locale;

public class FoodItem implements Parcelable {
    private String nameFr;
    private String nameEn;
    private String ingredientName;
    private Long id;

    public FoodItem(String nameFr, String nameEn, String ingredientName, Long id) {
        this.nameFr = nameFr;
        this.nameEn = nameEn;
        this.ingredientName = ingredientName;
        this.id = id;
    }

    protected FoodItem(Parcel in) {
        nameFr = in.readString();
        nameEn = in.readString();
        ingredientName = in.readString();
        id = (long) in.readInt();
    }

    public static final Creator<FoodItem> CREATOR = new Creator<FoodItem>() {
        @Override
        public FoodItem createFromParcel(Parcel in) {
            return new FoodItem(in);
        }

        @Override
        public FoodItem[] newArray(int size) {
            return new FoodItem[size];
        }
    };

    public String getName() {
        String currentLanguage = Locale.getDefault().getLanguage();
        return currentLanguage.equals("fr") ? nameFr : nameEn;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    @Override
    public String toString() {
        return "FoodItem{" +
                "nameFr='" + nameFr + '\'' +
                ", nameEn='" + nameEn + '\'' +
                ", ingredientName='" + ingredientName + '\'' +
                '}';
    }

    public Long getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(nameFr);
        dest.writeString(nameEn);
        dest.writeString(ingredientName);
        dest.writeInt(Math.toIntExact(id));
    }
}
