package com.example.sravanthi.popularmoviesapp;

//background network call in AsyncTask

import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

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



public class FetchPosterTask extends AsyncTask<String, Void, ArrayList<PosterImages>> {
    ArrayList<PosterImages> posterImagesArrayList = new ArrayList<PosterImages>();
    ImageAdapter adapter;
        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();


        private ArrayList<PosterImages> getPosterFromJson(String posterJsonStr) throws JSONException {
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
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject posterPathObject = movieArray.getJSONObject(i);
                String postersName = "http://image.tmdb.org/t/p/w185/" + posterPathObject.getString(MDB_POSTER_PATH);
                String overView = posterPathObject.getString(MDB_OVERVIEW);
                String posterTitle = posterPathObject.getString(MDB_TITLE);
                String releaseDate = posterPathObject.getString(MDB_RELEASE_DATE);
                String userRating = posterPathObject.getString(MDB_USER_RATING);
                String popularity = posterPathObject.getString(MDB_POPULARITY);
                String id = posterPathObject.getString(MDB_ID);
                posterImagesArrayList.add(new PosterImages(postersName, overView, posterTitle, releaseDate, userRating, popularity, id));
            }
            return posterImagesArrayList;
        }


        @Override
        protected ArrayList<PosterImages> doInBackground(String... params) {

            if (params.length == 0) {
                return null;

            }

            //Used SharedPreferences to select the popular or top rated movies depending on users choice

            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sort = pref.getString(getString(R.string.movieKey), getString(R.string.defaultValue));
            switch (sort) {
                case "0":
                    sort = "popularity";
                    break;
                case "1":
                    sort = "top_rated";
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
        protected void onPostExecute(ArrayList<PosterImages> result) {


            adapter.updateData(result);

        }
    }
