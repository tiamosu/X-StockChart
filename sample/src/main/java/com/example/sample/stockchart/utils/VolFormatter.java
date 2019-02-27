package com.example.sample.stockchart.utils;

import android.content.Context;

import com.example.sample.R;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.text.DecimalFormat;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public final class VolFormatter extends ValueFormatter {
    private Context mContext;

    public VolFormatter(Context context) {
        this.mContext = context;
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        final int e = (int) Math.floor(Math.log10(value));
        int unit;
        if (e >= 8) {
            unit = 8;
        } else if (e >= 4) {
            unit = 4;
        } else {
            unit = 1;
        }

        DecimalFormat format;
        if (e == 1) {
            format = new DecimalFormat("#0");
        } else {
            format = new DecimalFormat("#0.00");
        }
        value = value / (int) Math.pow(10, unit);
        if (value == 0) {
            int e2 = (int) Math.floor(Math.log10(axis.getAxisMaximum()));
            String u;
            if (e2 >= 8) {
                u = mContext.getResources().getString(R.string.billions_shou);
            } else if (e2 >= 4) {
                u = mContext.getResources().getString(R.string.millions_shou);
            } else {
                u = mContext.getResources().getString(R.string.shou);
            }
            return u;
        }
        return format.format(value);
    }
}
