package com.github.mikephil.charting.data;

/**
 * Created by philipp on 13/06/16.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class RadarEntry extends Entry {

    public RadarEntry(float value) {
        super(0, 0f, value);
    }

    public RadarEntry(float value, Object data) {
        super(0, 0f, value, data);
    }

    /**
     * This is the same as getY(). Returns the value of the RadarEntry.
     */
    public float getValue() {
        return getY();
    }

    @Override
    public RadarEntry copy() {
        return new RadarEntry(getY(), getData());
    }

    @Deprecated
    @Override
    public void setX(float x) {
        super.setX(x);
    }

    @Deprecated
    @Override
    public float getX() {
        return super.getX();
    }
}
