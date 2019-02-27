package com.github.mikephil.charting.formatter;

import java.text.DecimalFormat;

/**
 * Default formatter used for formatting values inside the chart. Uses a DecimalFormat with
 * pre-calculated number of digits (depending on max and min value).
 *
 * @author Philipp Jahoda
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class DefaultValueFormatter extends ValueFormatter {

    /**
     * DecimalFormat for formatting
     */
    protected DecimalFormat mFormat;

    protected int mDecimalDigits;

    /**
     * Constructor that specifies to how many digits the value should be
     * formatted.
     */
    public DefaultValueFormatter(int digits) {
        setup(digits);
    }

    /**
     * Sets up the formatter with a given number of decimal digits.
     */
    public void setup(int digits) {
        this.mDecimalDigits = digits;

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
        // put more logic here ...
        // avoid memory allocations here (for performance reasons)
        return mFormat.format(value);
    }

    /**
     * Returns the number of decimal digits this formatter uses.
     */
    public int getDecimalDigits() {
        return mDecimalDigits;
    }
}
