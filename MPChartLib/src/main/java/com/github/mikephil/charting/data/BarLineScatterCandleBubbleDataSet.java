package com.github.mikephil.charting.data;

import android.graphics.Color;

import com.github.mikephil.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Baseclass of all DataSets for Bar-, Line-, Scatter- and CandleStickChart.
 *
 * @author Philipp Jahoda
 */
public abstract class BarLineScatterCandleBubbleDataSet<T extends Entry> extends DataSet<T> implements IBarLineScatterCandleBubbleDataSet<T> {

    /**
     * default highlight color
     */
    protected int mHighLightColor = Color.rgb(255, 187, 115);
    /**
     * the width of the highlight indicator lines
     */
    protected float mHighlightLineWidth = 0.5f;

    public BarLineScatterCandleBubbleDataSet(List<T> yVals, String label) {
        super(yVals, label);
    }

    /**
     * Sets the color that is used for drawing the highlight indicators. Dont
     * forget to resolve the color using getResources().getColor(...) or
     * Color.rgb(...).
     */
    public void setHighLightColor(int color) {
        mHighLightColor = color;
    }

    @Override
    public int getHighLightColor() {
        return mHighLightColor;
    }

    /**
     * Sets the width of the highlight line in dp.
     */
    public void setHighlightLineWidth(float width) {
        mHighlightLineWidth = width;
    }

    @Override
    public float getHighlightLineWidth() {
        return mHighlightLineWidth;
    }
}
