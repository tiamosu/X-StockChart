package com.github.mikephil.charting.formatter;

import java.text.DecimalFormat;

/**
 * Created by philipp on 02/06/16.
 */
@SuppressWarnings("WeakerAccess")
public class DefaultAxisValueFormatter extends ValueFormatter {

    /**
     * decimalformat for formatting
     */
    protected DecimalFormat mFormat;

    /**
     * the number of decimal digits this formatter uses
     */
    protected int digits;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     */
    public DefaultAxisValueFormatter(int digits) {
        this.digits = digits;

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < digits; i++) {
            if (i == 0) {
                builder.append(".");
            }
            builder.append("0");
        }

        mFormat = new DecimalFormat("###,###,###,##0" + builder.toString());
    }

    @Override
    public String getFormattedValue(float value) {
        // avoid memory allocations here (for performance)
        return mFormat.format(value);
    }

    /**
     * Returns the number of decimal digits this formatter uses or -1, if unspecified.
     */
    public int getDecimalDigits() {
        return digits;
    }
}
