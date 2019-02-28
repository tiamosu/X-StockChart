package com.github.mikephil.charting.listener;

import android.annotation.SuppressLint;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

@SuppressWarnings("unused")
public class OnDrawLineChartTouchListener extends SimpleOnGestureListener implements OnTouchListener {

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
