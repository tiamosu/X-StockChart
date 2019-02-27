package com.github.mikephil.charting.components;

import android.graphics.Paint;

import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

/**
 * Created by Philipp Jahoda on 17/09/16.
 */
@SuppressWarnings("unused")
public class Description extends ComponentBase {

    /**
     * the text used in the description
     */
    private String text = "Description Label";

    /**
     * the custom position of the description text
     */
    private MPPointF mPosition;

    /**
     * the alignment of the description text
     */
    private Paint.Align mTextAlign = Paint.Align.RIGHT;

    public Description() {
        super();

        // default size
        mTextSize = Utils.convertDpToPixel(8f);
    }

    /**
     * Sets the text to be shown as the description.
     * Never set this to null as this will cause nullpointer exception when drawing with Android Canvas.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Returns the description text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets a custom position for the description text in pixels on the screen.
     */
    public void setPosition(float x, float y) {
        if (mPosition == null) {
            mPosition = MPPointF.getInstance(x, y);
        } else {
            mPosition.x = x;
            mPosition.y = y;
        }
    }

    /**
     * Returns the customized position of the description, or null if none set.
     */
    public MPPointF getPosition() {
        return mPosition;
    }

    /**
     * Sets the text alignment of the description text. Default RIGHT.
     */
    public void setTextAlign(Paint.Align align) {
        this.mTextAlign = align;
    }

    /**
     * Returns the text alignment of the description.
     */
    public Paint.Align getTextAlign() {
        return mTextAlign;
    }
}
