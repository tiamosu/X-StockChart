package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.components.AxisBase;

import java.util.Collection;

/**
 * This formatter is used for passing an array of x-axis labels, on whole x steps.
 */
@SuppressWarnings("unused")
public class IndexAxisValueFormatter implements IAxisValueFormatter {
    private String[] mValues = new String[]{};
    private int mValueCount = 0;

    /**
     * An empty constructor.
     * Use `setValues` to set the axis labels.
     */
    public IndexAxisValueFormatter() {
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public IndexAxisValueFormatter(String[] values) {
        if (values != null) {
            setValues(values);
        }
    }

    /**
     * Constructor that specifies axis labels.
     *
     * @param values The values string array
     */
    public IndexAxisValueFormatter(Collection<String> values) {
        if (values != null) {
            setValues(values.toArray(new String[0]));
        }
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        final int index = Math.round(value);
        if (index < 0 || index >= mValueCount || index != (int) value) {
            return "";
        }
        return mValues[index];
    }

    public String[] getValues() {
        return mValues;
    }

    public void setValues(String[] values) {
        if (values == null) {
            values = new String[]{};
        }

        this.mValues = values;
        this.mValueCount = values.length;
    }
}
