package com.example.android.moiveapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

import java.util.ArrayList;

/**
 * Created by QHH on 2016/12/12.
 */

public class MovieFragment extends Fragment{
    public static String[] moviePoster = new String[20];
    private ImageAdapter mImageAdapter;

    public MovieFragment() {
    }

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
        FetchMovieTask movieTask = new FetchMovieTask(mImageAdapter);
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


}


