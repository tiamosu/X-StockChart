package com.example.sample.stockchart;

import android.graphics.Canvas;

import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingYAxisRenderer extends YAxisRenderer {
    private TimeSharingYAxis mYAxis;

    public TimeSharingYAxisRenderer(ViewPortHandler viewPortHandler, TimeSharingYAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
        mYAxis = yAxis;
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {
        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled() ? mYAxis.mEntryCount : (mYAxis.mEntryCount - 1);
        //取YLabelEntry的中间数位置
        final double averagePos = (double) (to - 1) / 2;
        final int[] labelColorArray = mYAxis.getLabelColorArray();

        // draw
        if (mYAxis.isValueLineInside()) {
            for (int i = from; i < to; i++) {
                String text = mYAxis.getFormattedLabel(i);
                text = getLabelText(text, averagePos, i, labelColorArray);
                if (i == 0) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentBottom()
                            - Utils.convertDpToPixel(1), mAxisLabelPaint);
                } else if (i == to - 1) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentTop()
                            + Utils.convertDpToPixel(8), mAxisLabelPaint);
                } else {
                    c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
                }
            }
        } else {
            for (int i = from; i < to; i++) {
                String text = mYAxis.getFormattedLabel(i);
                text = getLabelText(text, averagePos, i, labelColorArray);
                c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
            }
        }
    }

    private String getLabelText(String text, double averagePos, int pos, int[] labelColorArray) {
        //YLabelEntry的数据是从下往上填充的
        if (labelColorArray != null && labelColorArray.length >= 3) {
            final int labelColor = pos > averagePos ? labelColorArray[0]
                    : (pos < averagePos ? labelColorArray[2] : labelColorArray[1]);
            mAxisLabelPaint.setColor(labelColor);
        }
        if (pos > averagePos && text.contains("%")) {
            text = "+" + text;
        }
        return text;
    }
}
