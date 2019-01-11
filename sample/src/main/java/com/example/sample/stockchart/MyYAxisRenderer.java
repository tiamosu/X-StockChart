package com.example.sample.stockchart;

import android.graphics.Canvas;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.renderer.YAxisRenderer;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class MyYAxisRenderer extends YAxisRenderer {
    private int[] mLabelColorArray;

    public MyYAxisRenderer(ViewPortHandler viewPortHandler, YAxis yAxis, Transformer trans) {
        super(viewPortHandler, yAxis, trans);
    }

    /**
     * 给每个label单独设置颜色
     */
    public void setLabelColor(int[] labelColorArray) {
        mLabelColorArray = labelColorArray;
    }

    @Override
    protected void drawYLabels(Canvas c, float fixedPosition, float[] positions, float offset) {
        final int from = mYAxis.isDrawBottomYLabelEntryEnabled() ? 0 : 1;
        final int to = mYAxis.isDrawTopYLabelEntryEnabled() ? mYAxis.mEntryCount : (mYAxis.mEntryCount - 1);
        //取YLabelEntry的中间数位置
        final double averagePos = (double) (to - 1) / 2;

        // draw
        if (mYAxis.isValueLineInside()) {
            for (int i = from; i < to; i++) {
                String text = mYAxis.getFormattedLabel(i);
                text = getLabelText(text, averagePos, i);
                if (i == 0) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentBottom() - Utils.convertDpToPixel(1), mAxisLabelPaint);
                } else if (i == to - 1) {
                    c.drawText(text, fixedPosition, mViewPortHandler.contentTop() + Utils.convertDpToPixel(8), mAxisLabelPaint);
                } else {
                    c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
                }
            }
        } else {
            for (int i = from; i < to; i++) {
                String text = mYAxis.getFormattedLabel(i);
                text = getLabelText(text, averagePos, i);
                c.drawText(text, fixedPosition, positions[i * 2 + 1] + offset, mAxisLabelPaint);
            }
        }
    }

    private String getLabelText(String text, double averagePos, int pos) {
        //YLabelEntry的数据是从下往上填充的
        if (mLabelColorArray != null && mLabelColorArray.length >= 3) {
            final int labelColor = pos > averagePos ? mLabelColorArray[0]
                    : (pos < averagePos ? mLabelColorArray[2] : mLabelColorArray[1]);
            mAxisLabelPaint.setColor(labelColor);
        }
        if (pos > averagePos && text.contains("%")) {
            text = "+" + text;
        }
        return text;
    }
}
