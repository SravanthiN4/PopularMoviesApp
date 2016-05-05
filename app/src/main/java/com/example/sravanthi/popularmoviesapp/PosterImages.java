package com.example.sravanthi.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sravanthi on 4/28/2016.
 */
public class PosterImages implements Parcelable {
    // You can include parcel data types
    String poster_path;
    String overview;

    public PosterImages(String poster_path, String overview) {
        this.poster_path = poster_path;
        this.overview = overview;

    }

    public PosterImages()
    {

    }

    // Using the `in` variable, we can retrieve the values that
    // we originally wrote into the `Parcel`.  This constructor is usually
    // private so that only the `CREATOR` field can access.

    private PosterImages(Parcel in) {
        poster_path = in.readString();
        overview = in.readString();
    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }

    public String getOverview()
    {
        return overview;
    }

    public void setOverview(String overview)
    {
        this.overview = overview;
    }

    @Override
    public int describeContents() {
        return 0;
    }

//    public String toString() {
//        return poster_path;
//
//    }


    // This is where you write the values you want to save to the `Parcel`.
    // The `Parcel` class has methods defined to help you save all of your values.
    // Note that there are only methods defined for simple values, lists, and other Parcelable objects.
    // You may need to make several classes Parcelable to send the data you want.
    @Override
    public void writeToParcel(Parcel parcel, int i) {

        parcel.writeString(poster_path);
        parcel.writeString(overview);

    }

    // After implementing the `Parcelable` interface, we need to create the
    // `Parcelable.Creator<MyParcelable> CREATOR` constant for our class;
    // Notice how it has our class specified as its type.

    public static final Parcelable.Creator<PosterImages> CREATOR = new Parcelable.Creator<PosterImages>() {
        // This simply calls our new constructor (typically private) and
        // passes along the unmarshalled `Parcel`, and then returns the new object!
        @Override
        public PosterImages createFromParcel(Parcel parcel) {
            return new PosterImages(parcel);

        }

        // We just need to copy this and change the type to match our class.

        @Override
        public PosterImages[] newArray(int i) {
            return new PosterImages[i];
        }
    };
}


