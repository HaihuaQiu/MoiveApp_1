package com.example.android.moiveapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.android.moiveapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static com.example.android.moiveapp.BuildConfig.MOVIE_API_KEY;

/**
 * Created by QHH on 2017/1/12.
 */

public class FetchMovieTask extends AsyncTask<Void, Void, Void> {
    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();
    private final Context mContext;

    public FetchMovieTask(Context context) {
        mContext = context;
    }

    private void getMoviePosterFromJson(String movieJsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_RESULT = "results";
        final String OWM_POSTER = "poster_path";
        final String OWM_OVERVIEW = "overview";
        final String OWM_YEAR = "release_date";
        final String OWM_SCORE = "vote_average";
        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULT);
            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());
            for (int i = 0; i < movieArray.length(); i++) {

                String poster, over, yer, scro, title, pop, top, movieId, author;
                String[] movie_video, review, time;
                JSONObject movie = movieArray.getJSONObject(i);
                poster = "https://image.tmdb.org/t/p/w185" + movie.getString(OWM_POSTER);
                title = movie.getString("title");
                movieId = movie.getString("id");
                review = storeData(movieId, "review");
                movie_video = storeData(movieId, "video");
                time = storeData(movieId, "time");
                pop = movie.getString("popularity");
                top = movie.getString("vote_average");
                over = movie.getString(OWM_OVERVIEW);
                yer = movie.getString(OWM_YEAR);
                scro = movie.getString(OWM_SCORE);
                ContentValues movieValues = new ContentValues();
                movieValues.put(MovieContract.MovieEntry.COLUMN_RESULT_IMAGE, poster);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_NAME, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, movieId);
                movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, over);
                movieValues.put(MovieContract.MovieEntry.COLUMN_YEAR, yer);
                movieValues.put(MovieContract.MovieEntry.COLUMN_SCORE, scro);
                movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, pop);
                movieValues.put(MovieContract.MovieEntry.COLUMN_TOPRATE, top);
                movieValues.put(MovieContract.MovieEntry.COLUMN_COLLECT, "0");
                if (movie_video != null && movie_video.length > 0) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_PARTONE, movie_video[0]);
                }
                if (movie_video.length > 1) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_PARTTWO, movie_video[1]);
                }
                if (movie_video.length > 2) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_VIDEO_PARTTHREE, movie_video[2]);
                }
                if (review != null && review.length > 0) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_PARTONE, review[0]);
                }
                if (review.length > 1) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_PARTTWO, review[1]);
                }
                if (review.length > 2) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_REVIEW_PARTTHREE, review[2]);
                }
                if (time != null && time.length > 0) {
                    movieValues.put(MovieContract.MovieEntry.COLUMN_TIME, time[0] + "min");
                }
                cVVector.add(movieValues);
            }
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                Cursor cur = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
                if (cur.getCount() < 40) {
                    inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                }
            }

            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private String[] getMovieVideo(String movieJsonStr) {
        final String OWM_RESULT = "results";
        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULT);
            String[] movie_video = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                movie_video[i] = movie.getString("key");
            }
            return movie_video;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    private String[] getMovieReview(String movieJsonStr) {
        final String OWM_RESULT = "results";
        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_RESULT);
            String[] movie_review = new String[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                JSONObject movie = movieArray.getJSONObject(i);
                movie_review[i] = movie.getString("content");
            }
            return movie_review;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
            return null;
        }
    }

    private String getMovieTime(String movieJsonStr) {
        try {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            if (movieJson.has("runtime")) {
                String time = movieJson.getString("runtime");
                return time;
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String[] storeData(String movieUri, String pop) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String movieJsonStr = null;
        try {
            final String FORECAST_BASE_URL = movieUri + pop;
            final String QUERY_PARAM = "zh";
            final String KEY = "language";
            final String APPID_PARAM = "api_key";
            Uri builtUri;
            if (pop.equals("video")) {
                builtUri = Uri.parse("http://api.themoviedb.org/3/movie/" + movieUri + "/videos?api_key=" + MOVIE_API_KEY);
            } else if (pop.equals("review")) {
                builtUri = Uri.parse("http://api.themoviedb.org/3/movie/" + movieUri + "/reviews?api_key=" + MOVIE_API_KEY);
            } else if (pop.equals("time")) {
                builtUri = Uri.parse("http://api.themoviedb.org/3/movie/" + movieUri + "?api_key=" + MOVIE_API_KEY);
            } else
                builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY, QUERY_PARAM)
                        .appendQueryParameter(APPID_PARAM, MOVIE_API_KEY)
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
            if (pop.equals("video")) {
                return getMovieVideo(movieJsonStr);
            } else if (pop.equals("review")) {
                return getMovieReview(movieJsonStr);
            } else if (pop.equals("time")) {
                return new String[]{getMovieTime(movieJsonStr)};
            } else {
                getMoviePosterFromJson(movieJsonStr);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);

            movieJsonStr = null;
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream ", e);
                }
            }
        }
        return null;
    }

    @Override
    protected Void doInBackground(Void... params) {
        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String[] type = new String[2];
        type[0] = "popular";
        type[1] = "top_rated";
        String pop = "";
        for (String p : type) {
            pop = p;
            storeData(MOVIE_BASE_URL, pop);
        }
        return null;
    }
}
