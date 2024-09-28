package com.bal.fourthproject.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Character implements Parcelable {


    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private  String name;

    @SerializedName("status")
    private  String status;

    @SerializedName("species")
    private String species;

    @SerializedName("image")
    private String imageUrl;

    protected Character(Parcel in) {
        id = in.readInt();
        name = in.readString();
        status = in.readString();
        species = in.readString();
        imageUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(status);
        dest.writeString(species);
        dest.writeString(imageUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Character> CREATOR = new Creator<Character>() {
        @Override
        public Character createFromParcel(Parcel in) {
            return new Character(in);
        }

        @Override
        public Character[] newArray(int size) {
            return new Character[size];
        }
    };

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecies() {
        return species;
    }

    public String getImageUrl(){
        return  imageUrl;
    }

    @NonNull
    @Override
    public String toString() {
        return "Character{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", species='" + species + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
