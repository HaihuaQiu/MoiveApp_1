package com.example.android.moiveapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by QHH on 2017/1/5.
 */

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    private int mCount;
    private LayoutInflater mLayoutInflater;
    private ArrayList<MovieData> mTitles;
    private boolean mNotifyOnChange = true;
    private MyItemClickListener mItemClickListener;
    private int gridItem;
    private int imgId;
    public ImageAdapter(Context context, int item, int id, ArrayList<MovieData> data) {
        mLayoutInflater = LayoutInflater.from(context);
        gridItem=item;
        imgId=id;
        mTitles = data;
    }

    @Override
    public int getItemCount() {
        return mTitles == null ? 0 : mTitles.size();
    }
    public MovieData getpositiondata(int position){
        return mTitles.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int ViewType) {
        View view = mLayoutInflater.inflate(gridItem, viewGroup, false);
        ImageAdapter.ViewHolder vh = new ImageAdapter.ViewHolder(view,mItemClickListener);

        return vh;
    }
    public void setOnItemClickListener(MyItemClickListener listener){
        this.mItemClickListener = listener;
    }
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageView colorView = holder.imageView;
        Picasso.with(mLayoutInflater.getContext()).load(mTitles.get(position).resultStrs).into(colorView);
    }

    public void clearData() {
        int size = this.mTitles.size();
        if (size > 0) {
            this.mTitles.clear();
            this.notifyItemRangeRemoved(0, size);
        }
    }

    public void add(MovieData c) {
        mTitles.add(c);
        this.notifyDataSetChanged();
    }
    public interface MyItemClickListener {
        void onItemClick(View view,int postion);
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imageView;
        private MyItemClickListener mListener;
        public ViewHolder(View view,MyItemClickListener listener) {
            super(view);
            view.setOnClickListener(this);
            this.mListener=listener;
            imageView = (ImageView) view.findViewById(imgId);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                mListener.onItemClick(v,getPosition());
            }
        }
    }

}
