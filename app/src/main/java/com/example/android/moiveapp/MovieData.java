package com.example.android.moiveapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by QHH on 2017/1/7.
 */

public class MovieData implements Parcelable {
    public static final Parcelable.Creator CREATOR
            = new Parcelable.Creator() {

        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public MovieData createFromParcel(Parcel in) {
            return new MovieData(in);
        }

        // We just need to copy this and change the type to match our class.
        @Override
        public MovieData[] newArray(int size) {
            return new MovieData[size];
        }
    };
    public String name;
    public String resultStrs;
    public String year;
    public String score;
    public String overview;

    public MovieData() {

    }

    private MovieData(Parcel in) {
        String[] data = new String[5];
        in.readStringArray(data);
        this.name = data[0];
        this.resultStrs = data[1];
        this.year = data[2];
        this.score = data[3];
        this.overview = data[4];
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{this.name,
                this.resultStrs, this.year, this.score, this.overview});
    }
}