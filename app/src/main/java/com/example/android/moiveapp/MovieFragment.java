package com.example.android.moiveapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

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
 * Created by QHH on 2016/12/12.
 */

public class MovieFragment extends Fragment{
    private ImageAdapter mImageAdapter;

    public MovieFragment() {
    }

    public static String[] moviePoster = new String[20];


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovie();
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
    public void updateMovie() {
        FetchMovieTask movieTask = new FetchMovieTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = prefs.getString(getString(R.string.pref_key), getString(R.string.pref_popular));
        Log.v("update", order);
        if(isOnline()){
        movieTask.execute(order);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateMovie();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageAdapter = new ImageAdapter(getActivity(), R.layout.grid_item_movie,R.id.grid_item_movie_imageview,new ArrayList<MovieData>());
        View rootView = inflater.inflate(R.layout.fragment, container, false);
        RecyclerView gridview = (RecyclerView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(mImageAdapter);
        gridview.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mImageAdapter.setOnItemClickListener(new ImageAdapter.MyItemClickListener(){
            @Override
            public void onItemClick(View view, int postion) {
                MovieData data = mImageAdapter.getpositiondata(postion);
                Intent launcher = new Intent(getActivity(), DetailActivity.class)
                        .putExtra("custom data", data);

                startActivity(launcher);

            }
        });

        return rootView;
    }

    public class FetchMovieTask extends AsyncTask<String, Void, MovieData[]> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private MovieData[] getMoviePosterFromJson(String movieJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_RESULT = "results";
            final String OWM_POSTER = "poster_path";
            final String OWM_OVERVIEW = "overview";
            final String OWM_YEAR = "release_date";
            final String OWM_SCORE = "vote_average";
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULT);
            MovieData[] result = new MovieData[20];
            for (int i = 0; i < movieArray.length(); i++) {
                // For now, using the format "Day, poster, hi/low"
                String poster,over,yer,scro,title;
                JSONObject movie = movieArray.getJSONObject(i);
                poster = movie.getString(OWM_POSTER);
                title=movie.getString("title");
                over = movie.getString(OWM_OVERVIEW);
                yer = movie.getString(OWM_YEAR);
                scro = movie.getString(OWM_SCORE);
                result[i]= new MovieData();
                result[i].name=title;
                result[i].resultStrs = "https://image.tmdb.org/t/p/w185" + poster;
                result[i].overview = over;
                result[i].year = yer;
                result[i].score = scro + "/10";
            }

            return result;

        }

        @Override
        protected MovieData[] doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            if (params.length == 0) {
                return null;
            }
            // Will contain the raw JSON response as a string.
            String movieJsonStr = null;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                String pop = "";
                if (params[0].equals("popular") )
                    pop = "popular";
                else if (params[0].equals("top_rated") )
                    pop = "top_rated";
                final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + pop;
                final String QUERY_PARAM = "zh";
                final String KEY = "language";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY, QUERY_PARAM)
                        .appendQueryParameter(APPID_PARAM, "f48361b04340a2a3afce1ced25ef4288")
                        .build();
                URL url = new URL(builtUri.toString());
                // Create the request to OpenWeatherMap, and open the connection
                Log.i("website", builtUri.toString());
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieJsonStr = buffer.toString();
                Log.v(LOG_TAG, "Forecast JSON String: " + movieJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                movieJsonStr = null;
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
                return getMoviePosterFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(MovieData[] result) {
            if (result != null) {
                mImageAdapter.clearData();
                for (MovieData dayForecastStr : result) {
                    mImageAdapter.add(dayForecastStr);
                }
            }
        }
    }
}


