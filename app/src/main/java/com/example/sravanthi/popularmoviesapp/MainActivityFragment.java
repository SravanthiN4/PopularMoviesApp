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
    String sort;



    ArrayList<PosterImages> posterImagesArrayList = new ArrayList<PosterImages>();
    ImageAdapter adapter;

    public MainActivityFragment() {
    }


    /*
        On rotation of device the state is saved
    */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);


        if(savedInstanceState == null || !savedInstanceState.containsKey("posters")) {
            posterImagesArrayList = new ArrayList<PosterImages>();
        }
        else {
            posterImagesArrayList = savedInstanceState.getParcelableArrayList("posters");
        }
    }
    

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList("posters", posterImagesArrayList);
        super.onSaveInstanceState(outState);
    }


        //Creating options menu and inflating it
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main,menu);
    }

    /*on option selected,request,get the result from the SettingsActivity and update the movie
    based on user preference
    */

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



    private void updateMovies(){
        FetchPosterTask getPoster = new FetchPosterTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortOrder = prefs.getString(getString(R.string.movieKey),
                getString(R.string.defaultValue));
        getPoster.execute(sortOrder);
    }

    //Update Data received from the server in onStart

    @Override
    public void onStart() {
        adapter.clear();
        new FetchPosterTask().execute("posterJsonStr");
        super.onStart();
    }


    //inflate gridview and on selecting each poster, it goes to the details activity with extras
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        final View rootView =  inflater.inflate(R.layout.fragment_main, container, false);

        // Get a reference to the GridView, and attach this adapter to it.
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movie_poster);
        adapter = new ImageAdapter(getContext(), posterImagesArrayList);
        gridview.setAdapter(adapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PosterImages posterDetail = (PosterImages) adapter.getItem(position);
                Intent intent = new Intent(getActivity(),DetailActivity.class);
                PosterImages posterImageDetails = new PosterImages(posterDetail.getPoster_path(),posterDetail.getOverview(),posterDetail.getTitle(),posterDetail.getRelease_date(),posterDetail.getVote_average(),posterDetail.getPopularity(),posterDetail.getId());
                intent.putExtra("posterimages",posterImageDetails);
                startActivity(intent);



            }
        });
        return rootView;
    }

    





    //background network call in AsyncTask

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
            final String MDB_ID = "id";
            JSONObject posterJson = new JSONObject(posterJsonStr);
            JSONArray movieArray = posterJson.getJSONArray(MDB_RESULTS);
            for(int i=0; i<movieArray.length();i++)
            {
                JSONObject posterPathObject = movieArray.getJSONObject(i);
                String postersName =  "http://image.tmdb.org/t/p/w185/"+posterPathObject.getString(MDB_POSTER_PATH);
                String overView = posterPathObject.getString(MDB_OVERVIEW);
                String posterTitle = posterPathObject.getString(MDB_TITLE);
                String releaseDate = posterPathObject.getString(MDB_RELEASE_DATE);
                String userRating = posterPathObject.getString(MDB_USER_RATING);
                String popularity = posterPathObject.getString(MDB_POPULARITY);
                String id = posterPathObject.getString(MDB_ID);
                posterImagesArrayList.add(new PosterImages(postersName,overView,posterTitle,releaseDate,userRating,popularity,id));
            }
            return posterImagesArrayList;
        }



        @Override
        protected ArrayList<PosterImages> doInBackground(String... params) {

            if (params.length == 0) {
                return null;

            }
            
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = pref.getString(getString(R.string.movieKey),getString(R.string.defaultValue));
            switch (sort)
            {
                case "0": sort = "popularity";
                    break;
                case "1": sort = "top_rated";
                    break;
            }

          
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String posterJsonStr = null;

            try {
                final String POSTER_BASE_URL = "http://api.themoviedb.org/3/movie";
                final String APIKEY_PARAM = "api_key";


                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendPath(sort)
                        .appendQueryParameter(APIKEY_PARAM, BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {

                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                if (buffer.length() == 0) {

                    return null;
                }
                posterJsonStr = buffer.toString();

            } catch (IOException e) {

                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {

                    }
                }
            }

            try {
                return getPosterFromJson(posterJsonStr);

            } catch (JSONException e) {
                e.printStackTrace();

            }




            return null;
        }

        //Update UI in OnPostExecute

        @Override
        protected void onPostExecute(ArrayList<PosterImages>result) {


            adapter.updateData(result);

        }
    }
}