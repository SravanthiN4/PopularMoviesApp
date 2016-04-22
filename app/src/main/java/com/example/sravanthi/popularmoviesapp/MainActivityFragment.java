package com.example.sravanthi.popularmoviesapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.mainfragment,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh)
        {
            new FetchPosterTask().execute();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView =  inflater.inflate(R.layout.fragment_main, container, false);
        GridView gridview = (GridView) rootView.findViewById(R.id.gridview_movie_poster);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,
                        Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public class FetchPosterTask extends AsyncTask<Void,Void,Void>
    {
        private final String LOG_TAG = FetchPosterTask.class.getSimpleName();

        @Override
        protected Void doInBackground(Void... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            //will contain the json response as a String
            String posterJsonStr = null;

            try {
                final String POSTER_BASE_URL = "https://api.themoviedb.org/3/discover/movie?";
                final String APIKEY_PARAM = "api_key";

                Uri builtUri = Uri.parse(POSTER_BASE_URL).buildUpon()
                        .appendQueryParameter(APIKEY_PARAM,BuildConfig.THE_MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG,"Built URI "+builtUri.toString());

                urlConnection = (HttpURLConnection)url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if(inputStream == null)
                {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine())!=null)
                {
                    buffer.append(line+"\n");
                }
                if(buffer.length()==0)
                {
                    return null;
                }
                posterJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Poster Json String:"+posterJsonStr);
            }catch (IOException e)
            {
                Log.e(LOG_TAG, "Error",e);
                return null;
            }
            finally {
                if(urlConnection!=null)
                {
                    urlConnection.disconnect();
                }
                if(reader!=null)
                {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG,"Error closing stream",e);
                    }
                }
            }
            return null;
        }
    }
}
