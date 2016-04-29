package com.example.sravanthi.popularmoviesapp;

/**
 * Created by Sravanthi on 4/28/2016.
 */
public class PosterImages
{
    String poster_path;

    public PosterImages(String poster_path)
    {
        this.poster_path = poster_path;

    }

    public String getPoster_path() {
        return poster_path;
    }

    public void setPoster_path(String poster_path) {
        this.poster_path = poster_path;
    }
}
