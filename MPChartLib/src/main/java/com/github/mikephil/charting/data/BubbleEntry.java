package com.github.mikephil.charting.data;

import android.graphics.drawable.Drawable;

/**
 * Subclass of Entry that holds a value for one entry in a BubbleChart. Bubble
 * chart implementation: Copyright 2015 Pierre-Marc Airoldi Licensed under
 * Apache License 2.0
 *
 * @author Philipp Jahoda
 */
@SuppressWarnings({"unused"})
public class BubbleEntry extends Entry {

    /**
     * size value
     */
    private float mSize;

    /**
     * Constructor.
     *
     * @param x    The value on the x-axis.
     * @param y    The value on the y-axis.
     * @param size The size of the bubble.
     */
    public BubbleEntry(float x, float y, float size) {
        super(x, y);
        this.mSize = size;
    }

    /**
     * Constructor.
     *
     * @param x    The value on the x-axis.
     * @param y    The value on the y-axis.
     * @param size The size of the bubble.
     * @param data Spot for additional data this Entry represents.
     */
    public BubbleEntry(float x, float y, float size, Object data) {
        super(x, y, data);
        this.mSize = size;
    }

    /**
     * Constructor.
     *
     * @param x    The value on the x-axis.
     * @param y    The value on the y-axis.
     * @param size The size of the bubble.
     * @param icon Icon image
     */
    public BubbleEntry(float x, float y, float size, Drawable icon) {
        super(x, y, icon);
        this.mSize = size;
    }

    /**
     * Constructor.
     *
     * @param x    The value on the x-axis.
     * @param y    The value on the y-axis.
     * @param size The size of the bubble.
     * @param icon Icon image
     * @param data Spot for additional data this Entry represents.
     */
    public BubbleEntry(float x, float y, float size, Drawable icon, Object data) {
        super(x, y, icon, data);
        this.mSize = size;
    }

    @Override
    public BubbleEntry copy() {
        return new BubbleEntry(getX(), getY(), mSize, getData());
    }

    /**
     * Returns the size of this entry (the size of the bubble).
     */
    public float getSize() {
        return mSize;
    }

    public void setSize(float size) {
        this.mSize = size;
    }
}
