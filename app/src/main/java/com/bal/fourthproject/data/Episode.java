package com.bal.fourthproject.data;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Episode implements Parcelable {

    public Episode(String name, String airDate) {
        this.name = name;
        this.airDate = airDate;
    }

    public String getName() {
        return name;
    }

    public String getAirDate() {
        return airDate;
    }

    private String name;
    private String airDate;

    protected Episode(Parcel in) {
        name = in.readString();
        airDate = in.readString();
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(airDate);
    }
}
