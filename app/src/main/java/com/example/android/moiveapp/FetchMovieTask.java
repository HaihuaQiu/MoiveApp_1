package com.example.android.moiveapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by QHH on 2017/1/12.
 */

public class FetchMovieTask extends AsyncTask<String, Void, MovieData[]> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private ImageAdapter mImageAdapter;

    public FetchMovieTask(ImageAdapter mig) {
        this.mImageAdapter = mig;
    }

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
            String poster, over, yer, scro, title;
            JSONObject movie = movieArray.getJSONObject(i);
            poster = movie.getString(OWM_POSTER);
            title = movie.getString("title");
            over = movie.getString(OWM_OVERVIEW);
            yer = movie.getString(OWM_YEAR);
            scro = movie.getString(OWM_SCORE);
            result[i] = new MovieData();
            result[i].name = title;
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
            if (params[0].equals("popular"))
                pop = "popular";
            else if (params[0].equals("top_rated"))
                pop = "top_rated";
            final String FORECAST_BASE_URL = "http://api.themoviedb.org/3/movie/" + pop;
            final String QUERY_PARAM = "zh";
            final String KEY = "language";
            final String APPID_PARAM = "api_key";
            Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(KEY, QUERY_PARAM)
                    .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_API_KEY)
                    .build();
            URL url = new URL(builtUri.toString());
            // Create the request to OpenWeatherMap, and open the connection

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

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error", e);

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
