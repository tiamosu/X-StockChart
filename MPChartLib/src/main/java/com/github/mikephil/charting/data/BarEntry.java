package com.github.mikephil.charting.data;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.highlight.Range;

/**
 * Entry class for the BarChart. (especially stacked bars)
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ParcelCreator")
public class BarEntry extends Entry {

    /**
     * the values the stacked barchart holds
     */
    private float[] mYVals;

    /**
     * the ranges for the individual stack values - automatically calculated
     */
    private Range[] mRanges;

    /**
     * the sum of all negative values this entry (if stacked) contains
     */
    private float mNegativeSum;

    /**
     * the sum of all positive values this entry (if stacked) contains
     */
    private float mPositiveSum;

    /**
     * Constructor for normal bars (not stacked).
     */
    public BarEntry(int mXIndex, float x, float y) {
        super(mXIndex, x, y);
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(int mXIndex, float x, float y, Object data) {
        super(mXIndex, x, y, data);
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param icon - icon image
     */
    public BarEntry(int mXIndex, float x, float y, Drawable icon) {
        super(mXIndex, x, y, icon);
    }

    /**
     * Constructor for normal bars (not stacked).
     *
     * @param icon - icon image
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(int mXIndex, float x, float y, Drawable icon, Object data) {
        super(mXIndex, x, y, icon, data);
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param vals - the stack values, use at least 2
     */
    public BarEntry(int mXIndex, float x, float[] vals) {
        super(mXIndex, x, calcSum(vals));

        this.mYVals = vals;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param vals - the stack values, use at least 2
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(int mXIndex, float x, float[] vals, Object data) {
        super(mXIndex, x, calcSum(vals), data);

        this.mYVals = vals;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param vals - the stack values, use at least 2
     * @param icon - icon image
     */
    public BarEntry(int mXIndex, float x, float[] vals, Drawable icon) {
        super(mXIndex, x, calcSum(vals), icon);

        this.mYVals = vals;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Constructor for stacked bar entries. One data object for whole stack
     *
     * @param vals - the stack values, use at least 2
     * @param icon - icon image
     * @param data - Spot for additional data this Entry represents.
     */
    public BarEntry(int mXIndex, float x, float[] vals, Drawable icon, Object data) {
        super(mXIndex, x, calcSum(vals), icon, data);

        this.mYVals = vals;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Returns an exact copy of the BarEntry.
     */
    @Override
    public BarEntry copy() {
        BarEntry copied = new BarEntry(getXIndex(), getX(), getY(), getData());
        copied.setVals(mYVals);
        return copied;
    }

    /**
     * Returns the stacked values this BarEntry represents, or null, if only a single value is represented (then, use
     * getY()).
     */
    public float[] getYVals() {
        return mYVals;
    }

    /**
     * Set the array of values this BarEntry should represent.
     */
    public void setVals(float[] vals) {
        setY(calcSum(vals));
        mYVals = vals;
        calcPosNegSum();
        calcRanges();
    }

    /**
     * Returns the value of this BarEntry. If the entry is stacked, it returns the positive sum of all values.
     */
    @Override
    public float getY() {
        return super.getY();
    }

    /**
     * Returns the ranges of the individual stack-entries. Will return null if this entry is not stacked.
     */
    public Range[] getRanges() {
        return mRanges;
    }

    /**
     * Returns true if this BarEntry is stacked (has a values array), false if not.
     */
    public boolean isStacked() {
        return mYVals != null;
    }

    /**
     * Use `getSumBelow(stackIndex)` instead.
     */
    @Deprecated
    public float getBelowSum(int stackIndex) {
        return getSumBelow(stackIndex);
    }

    public float getSumBelow(int stackIndex) {
        if (mYVals == null) {
            return 0;
        }

        float remainder = 0f;
        int index = mYVals.length - 1;

        while (index > stackIndex && index >= 0) {
            remainder += mYVals[index];
            index--;
        }

        return remainder;
    }

    /**
     * Reuturns the sum of all positive values this entry (if stacked) contains.
     */
    public float getPositiveSum() {
        return mPositiveSum;
    }

    /**
     * Returns the sum of all negative values this entry (if stacked) contains. (this is a positive number)
     */
    public float getNegativeSum() {
        return mNegativeSum;
    }

    private void calcPosNegSum() {
        if (mYVals == null) {
            mNegativeSum = 0;
            mPositiveSum = 0;
            return;
        }

        float sumNeg = 0f;
        float sumPos = 0f;

        for (float f : mYVals) {
            if (f <= 0f) {
                sumNeg += Math.abs(f);
            } else {
                sumPos += f;
            }
        }

        mNegativeSum = sumNeg;
        mPositiveSum = sumPos;
    }

    /**
     * Calculates the sum across all values of the given stack.
     */
    private static float calcSum(float[] vals) {
        if (vals == null) {
            return 0f;
        }

        float sum = 0f;

        for (float f : vals) {
            sum += f;
        }

        return sum;
    }

    protected void calcRanges() {
        float[] values = getYVals();
        if (values == null || values.length == 0) {
            return;
        }

        mRanges = new Range[values.length];

        float negRemain = -getNegativeSum();
        float posRemain = 0f;

        for (int i = 0; i < mRanges.length; i++) {

            float value = values[i];

            if (value < 0) {
                mRanges[i] = new Range(negRemain, negRemain - value);
                negRemain -= value;
            } else {
                mRanges[i] = new Range(posRemain, posRemain + value);
                posRemain += value;
            }
        }
    }
}
