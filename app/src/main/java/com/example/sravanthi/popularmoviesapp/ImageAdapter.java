package com.example.sravanthi.popularmoviesapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Sravanthi on 4/21/2016.
 */
public class ImageAdapter extends ArrayAdapter<PosterImages> {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();
    private Context mContext;
    ArrayList<PosterImages> posterImages;
    int layoutResourceId;

    public ImageAdapter(Context context, int resource, ArrayList<PosterImages>images) {
        super(context, resource, images);
        this.mContext = context;
        this.layoutResourceId = resource;
        this.posterImages = images;
    }

    public void updateData(ArrayList<PosterImages>newPosters)
    {
        this.posterImages = newPosters;
        notifyDataSetChanged();
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {

        PosterImages posterImages = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_item,parent,false);
        }

        ImageView posterView = (ImageView)convertView.findViewById(R.id.poster_image);
        Picasso.with(getContext()).load(posterImages.getPoster_path()).into(posterView);
        return posterView;




    }



}