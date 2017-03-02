package com.example.android.moiveapp.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.moiveapp.R;
import com.example.android.moiveapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * Created by QHH on 2017/1/30.
 */

public class MyListCursorAdapter extends CursorRecyclerViewAdapter<MyListCursorAdapter.ViewHolder> {
    private MyListCursorAdapter.MyItemClickListener mItemClickListener;
    private int gridItem;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public MyListCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor);
        mContext = context;
        gridItem = R.layout.grid_item_movie;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return super.getItemCount();
    }

    public void setOnItemClickListener(MyListCursorAdapter.MyItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(gridItem, parent, false);
        ViewHolder vh = new ViewHolder(itemView, mItemClickListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.bindData(cursor);
    }

    public interface MyItemClickListener {
        void onItemClick(View view, int postion);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        private MyListCursorAdapter.MyItemClickListener mListener;

        public ViewHolder(View view, MyListCursorAdapter.MyItemClickListener listener) {
            super(view);
            view.setOnClickListener(this);
            this.mListener = listener;
            mImageView = (ImageView) view.findViewById(R.id.grid_item_movie_imageview);
        }

        public void bindData(final Cursor cursor) {
            final String imageNet = cursor.getString(cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_RESULT_IMAGE));
            Picasso.with(mLayoutInflater.getContext()).load(imageNet).into(mImageView);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onItemClick(v, getPosition());
            }
        }
    }
}
