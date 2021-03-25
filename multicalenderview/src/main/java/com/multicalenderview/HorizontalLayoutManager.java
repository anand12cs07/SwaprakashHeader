package com.multicalenderview;

import android.content.Context;
import android.graphics.PointF;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by aman on 11-08-2018.
 */

public class HorizontalLayoutManager extends LinearLayoutManager {

    public static final float SPEED_NORMAL = 1;
    public static final float SPEED_FAST = 5;
    public static final float SPEED_SLOW = 0.6f;

    private final float Y = 1;
    private float smoothScrollSpeed = SPEED_NORMAL;

    HorizontalLayoutManager(Context context, boolean reverseLayout) {
        super(context, HORIZONTAL, reverseLayout);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return HorizontalLayoutManager.this.
                        computeScrollVectorForPosition(targetPosition);
            }

            // This is the important method. This code will return the amount of time it takes to scroll 1 pixel.
            // This code will request Y milliseconds for every <code>smoothScrollSpeed</code> DP units.
            @Override
            protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                return Y / TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, smoothScrollSpeed, displayMetrics);
            }

        };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    public float getSmoothScrollSpeed() {
        return smoothScrollSpeed;
    }

    public void setSmoothScrollSpeed(float smoothScrollSpeed) {
        this.smoothScrollSpeed = smoothScrollSpeed;
    }

}
