package com.example.android.moiveapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.moiveapp.data.MovieContract;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

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
import static com.example.android.moiveapp.data.MovieContract.MovieEntry.CONTENT_URI;

/**
 * Created by QHH on 2017/1/12.
 */

public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
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
    static final String DETAIL_URI = "URI";
    private static final int DETAIL_LOADER = 0;
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
    @BindView(R.id.year)
    TextView yea;
    @BindView(R.id.movie_name)
    TextView mv_na;
    @BindView(R.id.score)
    TextView sco;
    @BindView(R.id.overview)
    TextView over;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.poster)
    ImageView pos;
    @BindView(R.id.button_collection)
    CheckBox mCheckBox;
    @BindView(R.id.video)
    ImageView videoIm;
    @BindView(R.id.review_one)
    TextView reviewOne;
    @BindView(R.id.review_two)
    TextView reviewTwo;
    @BindView(R.id.review_three)
    TextView reviewThree;
    Uri mData;
    private ScrollView scrollView;
    private int sViewX, sViewY;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            sViewX = savedInstanceState.getInt("sViewX");
            sViewY = savedInstanceState.getInt("sViewY");
            scrollView.post(new Runnable() {
                @Override
                public void run() {
                    scrollView.smoothScrollTo(sViewX, sViewY);
                }
            });
        }
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if (null != mData) {
            return new CursorLoader(getActivity(),
                mData,
                MOVIE_COLUMNS,
                null,
                null,
                null);
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.moveToFirst()) {
            return;
        }
        String movieName = data.getString(COL_MOVIE_NAME);
        String year = data.getString(COL_YEAR);
        String score = data.getString(COL_SCORE);
        String overview = data.getString(COL_OVERVIEW);
        String imagenet = data.getString(COL_RESULT_IMAGE);
        String runtime = data.getString(COL_TIME);
        final String[] video = new String[3];
        video[0] = data.getString(COL_VIDEO_ONE);
        video[1] = data.getString(COL_VIDEO_TWO);
        video[2] = data.getString(COL_VIDEO_THREE);
        final String[] review = new String[3];
        review[0] = data.getString(COL_REVIEW_ONE);
        review[1] = data.getString(COL_REVIEW_TWO);
        review[2] = data.getString(COL_REVIEW_THREE);
        reviewOne.setText(review[0]);
        reviewTwo.setText(review[1]);
        reviewThree.setText(review[2]);
        yea.setText(year);
        time.setText(runtime);
        mv_na.setText(movieName);
        sco.setText(score);
        over.setText(overview);
        Picasso.with(getContext()).load(imagenet).into(pos);
        videoIm.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.youtube.com/watch?v=" + video[0]));
                startActivity(intent);
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("sViewX", scrollView.getScrollX());
        outState.putInt("sViewY", scrollView.getScrollY());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        scrollView = (ScrollView) rootView;
        ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mData = arguments.getParcelable(DetailFragment.DETAIL_URI);
            Cursor cur = getContext().getContentResolver().query(mData, MOVIE_COLUMNS, null, null, null);
            if (cur.moveToFirst()) {
                String select = cur.getString(COL_COLLECT);
                if (select.equals("0")) {
                    mCheckBox.setChecked(false);
                } else {
                    mCheckBox.setChecked(true);
                }
            }
            cur.close();
            mCheckBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Thread(new Runnable() {
                        public void run() {
                            String movieId = MovieContract.MovieEntry.getDetailFromUri(mData);
                            ContentValues movieValues = new ContentValues();
                            if (mCheckBox.isChecked()) {
                                movieValues.put(MovieContract.MovieEntry.COLUMN_COLLECT, "1");
                                getContext().getContentResolver().update(CONTENT_URI, movieValues, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? AND " + MovieContract.MovieEntry.COLUMN_COLLECT + " = ? ", new String[]{movieId, "0"});
                            } else {
                                movieValues.put(MovieContract.MovieEntry.COLUMN_COLLECT, "0");
                                getContext().getContentResolver().update(CONTENT_URI, movieValues, MovieContract.MovieEntry.COLUMN_MOVIE_ID + " = ? AND " + MovieContract.MovieEntry.COLUMN_COLLECT + " = ? ", new String[]{movieId, "1"});
                            }
                        }
                    }).start();
                }
            });

        }
        return rootView;
    }

    void onTypeChanged(String type, int position) {
        // replace the uri, since the location has changed
        String orderType;
        if (type.equals("popular")) {
            orderType = MovieContract.MovieEntry.COLUMN_POPULARITY + " ASC";
        } else {
            orderType = MovieContract.MovieEntry.COLUMN_TOPRATE + " ASC";
        }
        Cursor cur = getContext().getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, MOVIE_COLUMNS, null, null, orderType);
        if (cur.moveToPosition(position)) {
            String movieId = cur.getString(COL_MOVIE_ID);
            Uri updatedUri = MovieContract.MovieEntry.buildMovieWithDetail(movieId);
            mData = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }
}
