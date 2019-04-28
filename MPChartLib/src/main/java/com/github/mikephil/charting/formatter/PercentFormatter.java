package com.github.mikephil.charting.formatter;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DecimalFormat;

/**
 * This IValueFormatter is just for convenience and simply puts a "%" sign after
 * each value. (Recommeded for PieChart)
 *
 * @author Philipp Jahoda
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class PercentFormatter extends ValueFormatter {
    public DecimalFormat mFormat;
    private PieChart mPieChart;
    private boolean mPercentSignSeparated;

    public PercentFormatter() {
        mFormat = new DecimalFormat("###,###,##0.0");
        mPercentSignSeparated = true;
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public PercentFormatter(PieChart pieChart) {
        this();
        this.mPieChart = pieChart;
    }

    // Can be used to remove percent signs if the chart isn't in percent mode
    public PercentFormatter(PieChart pieChart, boolean percentSignSeparated) {
        this(pieChart);
        this.mPercentSignSeparated = percentSignSeparated;
    }

    @Override
    public String getFormattedValue(float value) {
        return mFormat.format(value) + (mPercentSignSeparated ? " %" : "%");
    }

    @Override
    public String getPieLabel(float value, PieEntry pieEntry) {
        if (mPieChart != null && mPieChart.isUsePercentValuesEnabled()) {
            // Converted to percent
            return getFormattedValue(value);
        } else {
            // raw value, skip percent sign
            return mFormat.format(value);
        }
    }
}
