package com.github.mikephil.charting.renderer;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

/**
 * Created by Philipp Jahoda on 25/01/16.
 */
@SuppressWarnings("WeakerAccess")
public abstract class LineRadarRenderer extends LineScatterCandleRadarRenderer {

    public LineRadarRenderer(ChartAnimator animator, ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
    }

    /**
     * Draws the provided path in filled mode with the provided drawable.
     */
    protected void drawFilledPath(Canvas c, Path filledPath, Drawable drawable) {
        if (clipPathSupported()) {
            final int save = c.save();
            c.clipPath(filledPath);

            drawable.setBounds((int) mViewPortHandler.contentLeft(),
                    (int) mViewPortHandler.contentTop(),
                    (int) mViewPortHandler.contentRight(),
                    (int) mViewPortHandler.contentBottom());
            drawable.draw(c);

            c.restoreToCount(save);
        } else {
            throw new RuntimeException("Fill-drawables not (yet) supported below API level 18, " +
                    "this code was run on API level " + Utils.getSDKInt() + ".");
        }
    }

    /**
     * Draws the provided path in filled mode with the provided color and alpha.
     * Special thanks to Angelo Suzuki (https://github.com/tinsukE) for this.
     */
    protected void drawFilledPath(Canvas c, Path filledPath, int fillColor, int fillAlpha) {
        final int color = (fillAlpha << 24) | (fillColor & 0xffffff);
        if (clipPathSupported()) {
            final int save = c.save();
            c.clipPath(filledPath);
            c.drawColor(color);
            c.restoreToCount(save);
        } else {
            // save
            final Paint.Style previous = mRenderPaint.getStyle();
            final int previousColor = mRenderPaint.getColor();

            // set
            mRenderPaint.setStyle(Paint.Style.FILL);
            mRenderPaint.setColor(color);

            c.drawPath(filledPath, mRenderPaint);

            // restore
            mRenderPaint.setColor(previousColor);
            mRenderPaint.setStyle(previous);
        }
    }

    /**
     * Clip path with hardware acceleration only working properly on API level 18 and above.
     */
    private boolean clipPathSupported() {
        return Utils.getSDKInt() >= 18;
    }
}
