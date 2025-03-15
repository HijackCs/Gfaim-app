package com.gfaim.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class FoodItem implements Parcelable {
    private String name;
    private int id;

    public FoodItem(String name, int id) {
        this.name = name;
        this.id=id;
    }

    protected FoodItem(Parcel in) {
        name = in.readString();
        id = in.readInt();
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

    public String getName() { return name; }

    @Override
    public String toString() {
        return "FoodItem{" +
                "name='" + name + '\'' +
                '}';
    }

    public int getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(id);
    }
}
