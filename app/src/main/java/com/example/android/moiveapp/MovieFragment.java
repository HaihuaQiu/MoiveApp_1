package com.example.android.moiveapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.moiveapp.adapter.MyListCursorAdapter;
import com.example.android.moiveapp.data.MovieContract;
import com.example.android.moiveapp.sync.MovieSyncAdapter;

import static com.example.android.moiveapp.R.id.gridview;
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
 * Created by QHH on 2016/12/12.
 */

public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
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
    private static final String SELECTED_KEY = "selected_position";
    private static final String SELECTED_X = "x_shift";
    private static final String SELECTED_Y = "y_shift";
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
    private int ddx = 0, ddy = 0;
    private RecyclerView recyclerView;
    //    private int mPosition = RecyclerView.NO_POSITION;
    private boolean mOrderChange = false;
    public MovieFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String type = prefs.getString(getActivity().getString(R.string.pref_key),
                getActivity().getString(R.string.pref_popular));
        String orderType;
        if (type.equals("popular")) {
            orderType = MovieContract.MovieEntry.COLUMN_POPULARITY + " ASC";
        } else {
            orderType = MovieContract.MovieEntry.COLUMN_TOPRATE + " ASC";
        }
        Uri MovieUri = MovieContract.MovieEntry.CONTENT_URI;
//        Cursor cur = getActivity().getContentResolver().query(MovieUri, MOVIE_COLUMNS, null,null, orderType);
//        if (cur.moveToFirst()) {
//            ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieWithDetail(cur.getString(MovieFragment.COL_MOVIE_ID)),0);
//        }
        return new CursorLoader(getActivity(),
                MovieUri,
                MOVIE_COLUMNS,
                null,
                null,
                orderType);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        myAdapter.swapCursor(cursor);
        if (mOrderChange) {
            recyclerView.scrollToPosition(RecyclerView.HORIZONTAL);
            ddx = ddy = 0;
            mOrderChange = false;

        }
        if (ddx != 0 && ddy != 0 && !mOrderChange) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            recyclerView.scrollTo(ddx, ddy);
//            recyclerView.smoothScrollToPosition(mPosition);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        myAdapter.swapCursor(null);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment_main to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.moviefragment, menu);
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
//            case R.id.action_refresh:
//                updateMovie();
//                return true;
            case R.id.action_settings:
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            case R.id.action_collection:
                startActivity(new Intent(getActivity(), CollectionMovie.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        myAdapter = new MyListCursorAdapter(getActivity(), null);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = (RecyclerView) rootView.findViewById(gridview);
        recyclerView.setAdapter(myAdapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                ddx = dx;
                ddy = dy;
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        myAdapter.setOnItemClickListener(new MyListCursorAdapter.MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Cursor cursor = myAdapter.getItem(position);
                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieWithDetail(cursor.getString(COL_MOVIE_ID)), position);
                }
//                mPosition = position;
            }
        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
//            mPosition = savedInstanceState.getInt(SELECTED_KEY);
            ddx = savedInstanceState.getInt(SELECTED_X);
            ddy = savedInstanceState.getInt(SELECTED_Y);
        }
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (ddx != 0 && ddy != 0) {
            outState.putInt(SELECTED_X, ddx);
            outState.putInt(SELECTED_Y, ddy);
        }
        super.onSaveInstanceState(outState);
    }

    void onOrderChanged() {
        updateMovie();
        mOrderChange = true;
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);

    }

    public void updateMovie() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void onItemSelected(Uri dateUri, int position);
    }
}


