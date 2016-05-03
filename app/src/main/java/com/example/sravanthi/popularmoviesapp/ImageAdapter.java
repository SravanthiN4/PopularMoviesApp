package com.example.sravanthi.popularmoviesapp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sravanthi on 4/21/2016.
 */
public class ImageAdapter extends ArrayAdapter<PosterImages> {

    private final String LOG_TAG = ImageAdapter.class.getSimpleName();
    //private Context mContext;
    List<PosterImages> posterImages;


    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data we want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param images A List of AndroidFlavor objects to display in a list
     */



    public ImageAdapter(Context context, List<PosterImages> images) {
        super(context,0, images);
        //this.mContext = context;
        //this.posterImages = images;
    }

    public void updateData(ArrayList<PosterImages>newPosters)
    {
        this.posterImages = newPosters;
        notifyDataSetChanged();
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */


    public View getView(int position, View convertView, ViewGroup parent) {

        // Gets the PosterImage object from the ArrayAdapter at the appropriate position

        PosterImages posterImages = getItem(position);
        Log.v(LOG_TAG,"items:"+posterImages);


        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.poster_item,parent,false);
        }

        ImageView posterView = (ImageView)convertView.findViewById(R.id.poster_image);
        Picasso.with(getContext()).load(posterImages.getPoster_path()).into(posterView);
        return posterView;

    }



}