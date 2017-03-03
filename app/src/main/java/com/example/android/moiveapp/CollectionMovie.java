package com.example.android.moiveapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.android.moiveapp.adapter.MyListCursorAdapter;
import com.example.android.moiveapp.data.MovieContract;

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

/**
 * Created by QHH on 2017/2/26.
 */

public class CollectionMovie extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
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
    private static final int MOVIE_LOADER = 0;
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
    MyListCursorAdapter myAdapter;

    public CollectionMovie() {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String type = prefs.getString(getString(R.string.pref_key),
                getString(R.string.pref_popular));
        String orderType;
        if (type.equals("popular")) {
            orderType = MovieContract.MovieEntry.COLUMN_POPULARITY + " ASC";
        } else {
            orderType = MovieContract.MovieEntry.COLUMN_TOPRATE + " ASC";
        }
        Uri MovieUri = MovieContract.MovieEntry.CONTENT_URI;
        return new CursorLoader(this,
                MovieUri,
                MOVIE_COLUMNS,
                MovieContract.MovieEntry.COLUMN_COLLECT + " = ? ",
                new String[]{"1"},
                orderType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        myAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        myAdapter.swapCursor(null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportLoaderManager().initLoader(MOVIE_LOADER, null, this);
        // Add this line in order for this fragment to handle menu events.
        setContentView(R.layout.fragment);
        myAdapter = new MyListCursorAdapter(this, null);
        RecyclerView gridview = (RecyclerView) findViewById(R.id.gridview);
        gridview.setAdapter(myAdapter);
        gridview.setLayoutManager(new GridLayoutManager(this, 2));
        myAdapter.setOnItemClickListener(new MyListCursorAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int postion) {
                Cursor cursor = myAdapter.getItem(postion);
                Intent launcher = new Intent(getBaseContext(), DetailActivity.class)
                        .setData(MovieContract.MovieEntry.buildMovieWithDetail(cursor.getString(COL_MOVIE_ID)));
                startActivity(launcher);

            }
        });
    }
}