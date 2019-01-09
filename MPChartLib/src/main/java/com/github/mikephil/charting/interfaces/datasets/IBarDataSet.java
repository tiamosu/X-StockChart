package com.github.mikephil.charting.interfaces.datasets;

import android.graphics.Paint;

import com.github.mikephil.charting.data.BarEntry;

/**
 * Created by philipp on 21/10/15.
 */
public interface IBarDataSet extends IBarLineScatterCandleBubbleDataSet<BarEntry> {

    /**
     * Returns true if this DataSet is stacked (stacksize > 1) or not.
     */
    boolean isStacked();

    /**
     * Returns the maximum number of bars that can be stacked upon another in
     * this DataSet. This should return 1 for non stacked bars, and > 1 for stacked bars.
     */
    int getStackSize();

    /**
     * Returns the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum value.
     */
    int getBarShadowColor();

    /**
     * Returns the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     */
    float getBarBorderWidth();

    /**
     * Returns the color drawing borders around the bars.
     */
    int getBarBorderColor();

    /**
     * Returns the alpha value (transparency) that is used for drawing the
     * highlight indicator.
     */
    int getHighLightAlpha();


    /**
     * Returns the labels used for the different value-stacks in the legend.
     * This is only relevant for stacked bar entries.
     */
    String[] getStackLabels();

    /**
     * Returns the neutral color (for open == close)
     */
    int getNeutralColor();

    /**
     * Returns the increasing color (for open < close).
     */
    int getIncreasingColor();

    /**
     * Returns the decreasing color (for open > close).
     */
    int getDecreasingColor();

    /**
     * Returns paint style when open < close
     */
    Paint.Style getIncreasingPaintStyle();

    /**
     * Returns paint style when open > close
     */
    Paint.Style getDecreasingPaintStyle();
}
