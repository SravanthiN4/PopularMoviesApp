package com.example.sravanthi.popularmoviesapp;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    String movieType;
    final int REQ_CODE = 1;



    ArrayList<PosterImages> imagesL = new ArrayList<PosterImages>();
    ImageAdapter adapter;

    public MainActivityFragment() {
    }



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        if(savedInstanceState == null || !savedInstanceState.containsKey("posters")) {
            imagesL = new ArrayList<PosterImages>();
        }
        else {
            imagesL = savedInstanceState.getParcelableArrayList("posters");
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("posters", imagesL);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivityForResult(new Intent(getActivity(), SettingsActivity.class),REQ_CODE);
            return true;
        }



        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.movieKey),
                getString(R.string.defaultValue));
        if(requestCode == REQ_CODE)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                updateMovies();
            }
        }
    }

    @Override
    public void onStart() {
        adapter.clear();
        new FetchPosterTask().execute("posterJsonStr");
        super.onStart();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movie_poster);
        adapter = new ImageAdapter(getContext(),imagesL);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PosterImages imagesP = (PosterImages) adapter.getItem(position);
                Log.d("imagesP","iP "+imagesP);

                Intent intent = new Intent(getActivity(),DetailActivity.class);
                PosterImages posterImages1 = new PosterImages(imagesP.getPoster_path(),imagesP.getOverview(),imagesP.getTitle(),imagesP.getRelease_date(),imagesP.getVote_average(),imagesP.getPopularity());
                Log.d("releasedate","releasedate "+imagesP.getRelease_date());
                intent.putExtra("posterimages",posterImages1);
                startActivity(intent);



            }
        });
        return rootView;
    }

    private void updateMovies(){
        FetchPosterTask getMovie = new FetchPosterTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.movieKey),
                getString(R.string.defaultValue));
        Log.i("sort1", sortOrder);
        getMovie.execute(sortOrder);
    }




    public class FetchPosterTask extends AsyncTask<String, Void, ArrayList<PosterImages>> {
        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();



        private ArrayList<PosterImages> getPosterFromJson(String posterJsonStr) throws JSONException
        {
            final String MDB_RESULTS = "results";
            final String MDB_POSTER_PATH = "poster_path";
            final String MDB_OVERVIEW = "overview";
            final String MDB_TITLE = "title";
            final String MDB_RELEASE_DATE = "release_date";
            final String MDB_USER_RATING = "vote_average";
            final String MDB_POPULARITY = "popularity";
            //String[] resultStrs = new String[20];
            JSONObject posterJson = new JSONObject(posterJsonStr);
            JSONArray movieArray = posterJson.getJSONArray(MDB_RESULTS);
            Log.v(LOG_TAG,"movieArray "+movieArray.length());
            for(int i=0; i<movieArray.length();i++)
            {
                JSONObject posterPathObject = movieArray.getJSONObject(i);
                Log.v(LOG_TAG,"pPath"+posterPathObject);
                String postersName =  "http://image.tmdb.org/t/p/w185/"+posterPathObject.getString(MDB_POSTER_PATH);
                Log.v(LOG_TAG,"pName:"+postersName);
                String overView = posterPathObject.getString(MDB_OVERVIEW);
                Log.v(LOG_TAG,"overView:"+overView);
                String posterTitle = posterPathObject.getString(MDB_TITLE);
                Log.v(LOG_TAG,"posterTitle:"+posterTitle);
                String releaseDate = posterPathObject.getString(MDB_RELEASE_DATE);
                Log.v(LOG_TAG,"releaseDate:"+releaseDate);
//                String a = releaseDate.substring(0,4);
//                Log.v(LOG_TAG,"a:"+a);
                String userRating = posterPathObject.getString(MDB_USER_RATING);
                Log.v(LOG_TAG,"userRating:"+userRating);

                String popularity = posterPathObject.getString(MDB_POPULARITY);
                Log.v(LOG_TAG,"popularity:"+popularity);

                imagesL.add(new PosterImages(postersName,overView,posterTitle,releaseDate,userRating,popularity));




                Log.v(LOG_TAG,"pImages"+imagesL);

            }

            Log.v(LOG_TAG,"imageL"+imagesL);
            return imagesL;




        }



        @Override
        protected ArrayList<PosterImages> doInBackground(String... params) {

            if (params.length == 0) {
                Log.v(LOG_TAG,"error:"+params);
                return null;

            }
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            //will contain the json response as a String
            String posterJsonStr = null;

            try {
                final String POSTER_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String APIKEY_PARAM = "api_key";
                final String SORT_PARAM = "sort_by";

                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM,params[0])
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    Log.v(LOG_TAG,"iStream:"+inputStream);
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {
                    Log.v(LOG_TAG,"buffer"+buffer);
                    return null;
                }
                posterJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Poster Json String:" + posterJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getPosterFromJson(posterJsonStr);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(LOG_TAG,"Error"+e);
            }




            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PosterImages>result) {


            adapter.updateData(result);
            Log.v(LOG_TAG,"Result:"+result);
        }
    }
}