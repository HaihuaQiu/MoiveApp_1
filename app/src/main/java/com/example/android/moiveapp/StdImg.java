package com.example.android.moiveapp;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by QHH on 2017/1/5.
 */

public class StdImg extends ImageView {
    StdImg(Context c){
        super(c);
    }
    StdImg(Context context,  AttributeSet attr){
        super(context,attr);
    }

    StdImg(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // set the iamgeview's aspect ratio manually ratio: 3 : 2
        // the drawable inside the iamgeview's ratio may be different
        int refactoredWidth = widthMeasureSpec;
        int refactoredHeight = widthMeasureSpec * 3 / 2;
        setMeasuredDimension(refactoredWidth, refactoredHeight);
    }
}
