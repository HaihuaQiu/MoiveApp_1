package com.example.android.moiveapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import com.example.android.moiveapp.data.MovieContract;
import com.example.android.moiveapp.sync.MovieSyncAdapter;

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

public class MainActivity extends AppCompatActivity implements MovieFragment.Callback {
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
    private static boolean init = false;
    private final String MOVIEFRAGMENT_TAG = "FFTAG";
    private String mType;
    private boolean mTwoPane;
    private int mPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mType = prefs.getString(getString(R.string.pref_key),
                getString(R.string.pref_popular));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.detail_movie) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            mTwoPane = true;
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            if (!init) {
                String orderType;
                if (mType.equals("popular")) {
                    orderType = COLUMN_POPULARITY + " ASC";
                } else {
                    orderType = COLUMN_TOPRATE + " ASC";
                }
                Cursor cur = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, orderType);
                if (cur.moveToFirst()) {
                    onItemSelected(MovieContract.MovieEntry.buildMovieWithDetail(cur.getString(MovieFragment.COL_MOVIE_ID)), 0);
                }
                cur.close();
                init = true;
            }
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.detail_movie, new DetailFragment(), MOVIEFRAGMENT_TAG)
//                        .commit();
//            }
        } else {
            mTwoPane = false;
            getSupportActionBar().setElevation(0f);
        }
        MovieSyncAdapter.initializeSyncAdapter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String type = prefs.getString(getString(R.string.pref_key),
                getString(R.string.pref_popular));
        if (type != null && !type.equals(mType)) {
            MovieFragment mf = (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movie);
            if (null != mf) {
                mf.onOrderChanged();
            }
            DetailFragment df = (DetailFragment) getSupportFragmentManager().findFragmentByTag(MOVIEFRAGMENT_TAG);
            if (null != df) {
                df.onTypeChanged(type, mPosition);
            }
            mType = type;
        }
    }

    @Override
    public void onItemSelected(Uri contentUri, int position) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAIL_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.detail_movie, fragment, MOVIEFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }
        mPosition = position;
    }
}
