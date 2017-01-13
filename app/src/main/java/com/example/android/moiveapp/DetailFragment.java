package com.example.android.moiveapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by QHH on 2017/1/12.
 */

public class DetailFragment extends Fragment {
    @BindView(R.id.year)
    TextView yea;
    @BindView(R.id.movie_name)
    TextView mv_na;
    @BindView(R.id.score)
    TextView sco;
    @BindView(R.id.overview)
    TextView over;
    private MovieData myData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, rootView);
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("custom data")) {
            myData = intent.getParcelableExtra("custom data");
            Picasso.with(getContext()).load(myData.resultStrs).into((ImageView) rootView.findViewById(R.id.poster));
            yea.setText(myData.year);
            mv_na.setText(myData.name);
            sco.setText(myData.score);
            over.setText(myData.overview);
        }
        return rootView;
    }
}
