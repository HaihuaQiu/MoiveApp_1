package com.example.android.moiveapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Created by QHH on 2017/1/7.
 */

public class DetailActivity extends AppCompatActivity {
    private ShareActionProvider mShareActionProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_detail);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new DetailFragment())
                    .commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class DetailFragment extends Fragment {
        private MovieData myData;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
            Intent intent = getActivity().getIntent();
            if (intent != null && intent.hasExtra("custom data")) {
                myData = (MovieData) intent.getSerializableExtra("custom data");
                Picasso.with(getContext()).load(myData.resultStrs).into((ImageView) rootView.findViewById(R.id.poster));
                ((TextView) rootView.findViewById(R.id.year))
                        .setText(myData.year);
                ((TextView) rootView.findViewById(R.id.movie_name))
                        .setText(myData.name);
                ((TextView) rootView.findViewById(R.id.score))
                        .setText(myData.score);
                ((TextView) rootView.findViewById(R.id.overview))
                        .setText(myData.overview);
            }
            return rootView;
        }


    }
}
