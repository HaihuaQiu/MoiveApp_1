package com.example.android.moiveapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.text.format.Time;
import android.util.Log;

import com.example.android.moiveapp.MainActivity;
import com.example.android.moiveapp.R;
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
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_COLLECT;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_MOVIE_ID;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_MOVIE_NAME;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_OVERVIEW;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_POPULARITY;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_RESULT_IMAGE;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_REVIEW_PARTONE;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_REVIEW_PARTTHREE;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_REVIEW_PARTTWO;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_SCORE;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_TIME;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_TOPRATE;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_VIDEO_PARTONE;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_VIDEO_PARTTHREE;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_VIDEO_PARTTWO;
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.COLUMN_YEAR;

public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    static final int COL_ID = 0;
    static final int COL_MOVIE_NAME = 1;
    static final int COL_MOVIE_ID = 2;
    static final int COL_COLLECT = 3;
    static final int COL_POPULARITY = 4;
    static final int COL_TOPRATE = 5;
    static final int COL_RESULT_IMAGE = 6;
    static final int COL_YEAR = 7;
    static final int COL_SCORE = 8;
    static final int COL_OVERVIEW = 9;
    static final int COL_VIDEO_ONE = 10;
    static final int COL_VIDEO_TWO = 11;
    static final int COL_VIDEO_THREE = 12;
    static final int COL_REVIEW_ONE = 13;
    static final int COL_REVIEW_TWO = 14;
    static final int COL_REVIEW_THREE = 15;
    static final int COL_TIME = 16;
    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            COLUMN_MOVIE_NAME,
            COLUMN_MOVIE_ID,
            COLUMN_COLLECT,
            COLUMN_POPULARITY,
            COLUMN_TOPRATE,
            COLUMN_RESULT_IMAGE,
            COLUMN_YEAR,
            COLUMN_SCORE,
            COLUMN_OVERVIEW,
            COLUMN_VIDEO_PARTONE,
            COLUMN_VIDEO_PARTTWO,
            COLUMN_VIDEO_PARTTHREE,
            COLUMN_REVIEW_PARTONE,
            COLUMN_REVIEW_PARTTWO,
            COLUMN_REVIEW_PARTTHREE,
            COLUMN_TIME
    };
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int NOTIFICATION_ID = 3004;
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String syncInterval = prefs.getString(context.getString(R.string.pref_frequency_key),
                context.getString(R.string.pref_frequency_default));
        MovieSyncAdapter.configurePeriodicSync(context, Integer.parseInt(syncInterval), Integer.parseInt(syncInterval) / 3);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
//        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
        String[] type = new String[2];
        type[0] = "popular";
        type[1] = "top_rated";
        String pop = "";
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String lastNotificationKey = getContext().getString(R.string.pref_last_notification);
        long lastSync = prefs.getLong(lastNotificationKey, 0);
        String syncInterval = prefs.getString(getContext().getString(R.string.pref_frequency_key),
                getContext().getString(R.string.pref_frequency_default));
        if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS || Integer.parseInt(syncInterval) < 86400) {
            for (String p : type) {
            pop = p;
            storeData(MOVIE_BASE_URL, pop);
            }
            SharedPreferences.Editor editor = prefs.edit();
            editor.putLong(lastNotificationKey, System.currentTimeMillis());
            editor.commit();
        }
        notifyMovieChange();
        return;
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
            Time dayTime = new Time();
            dayTime.setToNow();

            // we start at the day returned by local time. Otherwise this is a mess.
            int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            dayTime = new Time();
            for (int i = 0; i < movieArray.length(); i++) {
                String poster, over, yer, scro, title, pop, top, movieId, author;
                String[] movie_video, review, time;
                long dateTime = dayTime.setJulianDay(julianStartDay);
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
                movieValues.put(MovieContract.MovieEntry.COLUMN_DATE, dateTime);
                movieValues.put(COLUMN_RESULT_IMAGE, poster);
                movieValues.put(COLUMN_MOVIE_NAME, title);
                movieValues.put(COLUMN_MOVIE_ID, movieId);
                movieValues.put(COLUMN_OVERVIEW, over);
                movieValues.put(COLUMN_YEAR, yer);
                movieValues.put(COLUMN_SCORE, scro);
                movieValues.put(COLUMN_POPULARITY, pop);
                movieValues.put(COLUMN_TOPRATE, top);
                movieValues.put(COLUMN_COLLECT, "0");
                if (movie_video != null && movie_video.length > 0) {
                    movieValues.put(COLUMN_VIDEO_PARTONE, movie_video[0]);
                }
                if (movie_video != null && movie_video.length > 1) {
                    movieValues.put(COLUMN_VIDEO_PARTTWO, movie_video[1]);
                }
                if (movie_video != null && movie_video.length > 2) {
                    movieValues.put(COLUMN_VIDEO_PARTTHREE, movie_video[2]);
                }
                if (review != null && review.length > 0) {
                    movieValues.put(COLUMN_REVIEW_PARTONE, review[0]);
                }
                if (review != null && review.length > 1) {
                    movieValues.put(COLUMN_REVIEW_PARTTWO, review[1]);
                }
                if (review != null && review.length > 2) {
                    movieValues.put(COLUMN_REVIEW_PARTTHREE, review[2]);
                }
                if (time != null && time.length > 0) {
                    movieValues.put(COLUMN_TIME, time[0] + "min");
                }
                cVVector.add(movieValues);
            }
            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                inserted = getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);
                getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
                        MovieContract.MovieEntry.COLUMN_DATE + " <= ?",
                        new String[]{Long.toString(dayTime.setJulianDay(julianStartDay - 1))});
            }
            Log.d(LOG_TAG, "FetchMovieTask Complete. " + inserted + " Inserted");
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
    }

    private void notifyMovieChange() {
        Context context = getContext();
        //checking the last update and notify if it' the first of the day
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String displayNotificationsKey = context.getString(R.string.pref_enable_notifications_key);
        boolean displayNotifications = prefs.getBoolean(displayNotificationsKey,
                Boolean.parseBoolean(context.getString(R.string.pref_enable_notifications_default)));

        if (displayNotifications) {
            String orderType, movieIdNow;
            String notificationKey = context.getString(R.string.pref_movieId);
            String movieIdPast = prefs.getString(notificationKey, "");
            String type = prefs.getString(context.getString(R.string.pref_key), context.getString(R.string.pref_popular));
            if (type.equals("popular")) {
                orderType = MovieContract.MovieEntry.COLUMN_POPULARITY + " ASC";
            } else {
                orderType = MovieContract.MovieEntry.COLUMN_TOPRATE + " ASC";
            }
            Cursor cursor = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, orderType);
            if (cursor.moveToFirst()) {
                movieIdNow = cursor.getString(COL_MOVIE_ID);
                if (movieIdNow != null && !movieIdPast.equals("") && !movieIdNow.equals(movieIdPast)) {
                    int iconId = R.mipmap.ic_launcher;
                    Resources resources = context.getResources();
                    Bitmap largeIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
                    String title = context.getString(R.string.app_name);
                    String contentText = cursor.getString(COL_MOVIE_NAME);
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(getContext())
                                    .setColor(resources.getColor(R.color.cyan))
                                    .setSmallIcon(iconId)
                                    .setLargeIcon(largeIcon)
                                    .setAutoCancel(true) //点击通知，通知自动消失
                                    .setContentTitle(title)
                                    .setContentText(contentText);
                    Intent resultIntent = new Intent(context, MainActivity.class);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // WEATHER_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(notificationKey, movieIdNow);
                    editor.commit();
                } else if (movieIdPast.equals("")) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(notificationKey, movieIdNow);
                    editor.commit();
                }
                cursor.close();
            }
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
}