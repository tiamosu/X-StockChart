package com.github.mikephil.charting.renderer;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.github.mikephil.charting.animation.ChartAnimator;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointD;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings({"WeakerAccess", "unused"})
public class LineChartRenderer extends LineRadarRenderer {
    protected LineDataProvider mChart;
    protected float mOffSet = 0.5f;

    /**
     * paint for the inner circle of the value indicators
     */
    protected Paint mCirclePaintInner;

    /**
     * Bitmap object used for drawing the paths (otherwise they are too long if
     * rendered directly on the canvas)
     */
    protected WeakReference<Bitmap> mDrawBitmap;

    /**
     * on this canvas, the paths are rendered, it is initialized with the
     * pathBitmap
     */
    protected Canvas mBitmapCanvas;

    /**
     * the bitmap configuration to be used
     */
    protected Bitmap.Config mBitmapConfig = Bitmap.Config.ARGB_8888;

    protected Path cubicPath = new Path();
    protected Path cubicFillPath = new Path();

    public LineChartRenderer(LineDataProvider chart, ChartAnimator animator,
                             ViewPortHandler viewPortHandler) {
        super(animator, viewPortHandler);
        mChart = chart;

        mCirclePaintInner = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCirclePaintInner.setStyle(Paint.Style.FILL);
        mCirclePaintInner.setColor(Color.WHITE);
    }

    @Override
    public void initBuffers() {
    }

    @Override
    public void drawData(Canvas c) {
        final int width = (int) mViewPortHandler.getChartWidth();
        final int height = (int) mViewPortHandler.getChartHeight();

        Bitmap drawBitmap = mDrawBitmap == null ? null : mDrawBitmap.get();
        if (drawBitmap == null
                || (drawBitmap.getWidth() != width)
                || (drawBitmap.getHeight() != height)) {
            if (width > 0 && height > 0) {
                drawBitmap = Bitmap.createBitmap(width, height, mBitmapConfig);
                mDrawBitmap = new WeakReference<>(drawBitmap);
                mBitmapCanvas = new Canvas(drawBitmap);
            } else {
                return;
            }
        }

        drawBitmap.eraseColor(Color.TRANSPARENT);

        final LineData lineData = mChart.getLineData();
        for (ILineDataSet set : lineData.getDataSets()) {
            if (set.isVisible()) {
                drawDataSet(c, set);
            }
        }

        c.drawBitmap(drawBitmap, 0, 0, mRenderPaint);
    }

    protected void drawDataSet(Canvas c, ILineDataSet dataSet) {
        if (dataSet.getEntryCount() < 1) {
            return;
        }

        mRenderPaint.setStrokeWidth(dataSet.getLineWidth());
        mRenderPaint.setPathEffect(dataSet.getDashPathEffect());

        switch (dataSet.getMode()) {
            default:
            case LINEAR:
            case STEPPED:
                drawLinear(c, dataSet);
                break;
            case CUBIC_BEZIER:
                drawCubicBezier(dataSet);
                break;
            case HORIZONTAL_BEZIER:
                drawHorizontalBezier(dataSet);
                break;
        }

        mRenderPaint.setPathEffect(null);
    }

    protected void drawHorizontalBezier(ILineDataSet dataSet) {
        final float phaseY = mAnimator.getPhaseY();
        final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

        mXBounds.set(mChart, dataSet);
        cubicPath.reset();

        if (mXBounds.range >= 1) {
            Entry prev = dataSet.getEntryForIndex(mXBounds.min);
            Entry cur = prev;

            // let the spline start
            cubicPath.moveTo(cur.getX() + mOffSet, cur.getY() * phaseY);

            for (int j = mXBounds.min + 1; j <= mXBounds.range + mXBounds.min; j++) {
                prev = cur;
                cur = dataSet.getEntryForIndex(j);

                final float cpx = (prev.getX() + mOffSet)
                        + (cur.getX() + mOffSet - prev.getX() + mOffSet) / 2.0f;

                cubicPath.cubicTo(
                        cpx, prev.getY() * phaseY,
                        cpx, cur.getY() * phaseY,
                        cur.getX() + mOffSet, cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {
            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);
            // create a new path, this is bad for performance
            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, mXBounds);
        }

        mRenderPaint.setColor(dataSet.getColor());
        mRenderPaint.setStyle(Paint.Style.STROKE);
        trans.pathValueToPixel(cubicPath);
        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);
        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicBezier(ILineDataSet dataSet) {
        final float phaseY = mAnimator.getPhaseY();
        final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        mXBounds.set(mChart, dataSet);

        final float intensity = dataSet.getCubicIntensity();
        cubicPath.reset();

        if (mXBounds.range >= 1) {
            float prevDx;
            float prevDy;
            float curDx;
            float curDy;

            // Take an extra point from the left, and an extra from the right.
            // That's because we need 4 points for a cubic bezier (cubic=4), otherwise we get lines moving and doing weird stuff on the edges of the chart.
            // So in the starting `prev` and `cur`, go -2, -1
            // And in the `lastIndex`, add +1

            final int firstIndex = mXBounds.min + 1;
            final int lastIndex = mXBounds.min + mXBounds.range;

            Entry prevPrev;
            Entry prev = dataSet.getEntryForIndex(Math.max(firstIndex - 2, 0));
            Entry cur = dataSet.getEntryForIndex(Math.max(firstIndex - 1, 0));
            Entry next = cur;
            int nextIndex = -1;

            if (cur == null) {
                return;
            }

            // let the spline start
            cubicPath.moveTo(cur.getX() + mOffSet, cur.getY() * phaseY);

            for (int j = mXBounds.min + 1; j <= mXBounds.range + mXBounds.min; j++) {
                prevPrev = prev;
                prev = cur;
                cur = nextIndex == j ? next : dataSet.getEntryForIndex(j);

                nextIndex = j + 1 < dataSet.getEntryCount() ? j + 1 : j;
                next = dataSet.getEntryForIndex(nextIndex);

                prevDx = (cur.getX() + mOffSet - prevPrev.getX() + mOffSet) * intensity;
                prevDy = (cur.getY() - prevPrev.getY()) * intensity;
                curDx = (next.getX() + mOffSet - prev.getX() + mOffSet) * intensity;
                curDy = (next.getY() - prev.getY()) * intensity;

                cubicPath.cubicTo(prev.getX() + mOffSet + prevDx, (prev.getY() + prevDy) * phaseY,
                        cur.getX() + mOffSet - curDx, (cur.getY() - curDy) * phaseY,
                        cur.getX() + mOffSet, cur.getY() * phaseY);
            }
        }

        // if filled is enabled, close the path
        if (dataSet.isDrawFilledEnabled()) {
            cubicFillPath.reset();
            cubicFillPath.addPath(cubicPath);

            drawCubicFill(mBitmapCanvas, dataSet, cubicFillPath, trans, mXBounds);
        }

        mRenderPaint.setColor(dataSet.getColor());
        mRenderPaint.setStyle(Paint.Style.STROKE);
        trans.pathValueToPixel(cubicPath);
        mBitmapCanvas.drawPath(cubicPath, mRenderPaint);
        mRenderPaint.setPathEffect(null);
    }

    protected void drawCubicFill(Canvas c, ILineDataSet dataSet, Path spline, Transformer trans, XBounds bounds) {
        final float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, mChart);
        spline.lineTo(dataSet.getEntryForIndex(bounds.min + bounds.range).getX() + mOffSet, fillMin);
        spline.lineTo(dataSet.getEntryForIndex(bounds.min).getX() + mOffSet, fillMin);
        spline.close();
        trans.pathValueToPixel(spline);

        final Drawable drawable = dataSet.getFillDrawable();
        if (drawable != null) {
            drawFilledPath(c, spline, drawable);
        } else {
            drawFilledPath(c, spline, dataSet.getFillColor(), dataSet.getFillAlpha());
        }
    }

    protected float[] mLineBuffer = new float[4];

    /**
     * Draws a normal line.
     */
    protected void drawLinear(Canvas c, ILineDataSet dataSet) {
        final int entryCount = dataSet.getEntryCount();
        final boolean isDrawSteppedEnabled = dataSet.getMode() == LineDataSet.Mode.STEPPED;
        final int pointsPerEntryPair = isDrawSteppedEnabled ? 4 : 2;

        final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
        final float phaseY = mAnimator.getPhaseY();

        mRenderPaint.setStyle(Paint.Style.STROKE);

        Canvas canvas;
        // if the data-set is dashed, draw on bitmap-canvas
        if (dataSet.isDashedLineEnabled()) {
            canvas = mBitmapCanvas;
        } else {
            canvas = c;
        }

        mXBounds.set(mChart, dataSet);

        // if drawing filled is enabled
        if (dataSet.isDrawFilledEnabled() && entryCount > 0) {
            drawLinearFill(c, dataSet, trans, mXBounds);
        }

        // more than 1 color
        if (dataSet.getColors().size() > 1) {
            if (mLineBuffer.length <= pointsPerEntryPair * 2) {
                mLineBuffer = new float[pointsPerEntryPair * 4];
            }

            for (int j = mXBounds.min; j <= mXBounds.range + mXBounds.min; j++) {
                Entry e = dataSet.getEntryForIndex(j);
                if (e == null) {
                    continue;
                }

                mLineBuffer[0] = e.getX() + mOffSet;
                mLineBuffer[1] = e.getY() * phaseY;

                if (j < mXBounds.max) {
                    e = dataSet.getEntryForIndex(j + 1);
                    if (e == null) {
                        break;
                    }
                    if (isDrawSteppedEnabled) {
                        mLineBuffer[2] = e.getX() + mOffSet;
                        mLineBuffer[3] = mLineBuffer[1];
                        mLineBuffer[4] = mLineBuffer[2];
                        mLineBuffer[5] = mLineBuffer[3];
                        mLineBuffer[6] = e.getX() + mOffSet;
                        mLineBuffer[7] = e.getY() * phaseY;
                    } else {
                        mLineBuffer[2] = e.getX() + mOffSet;
                        mLineBuffer[3] = e.getY() * phaseY;
                    }
                } else {
                    mLineBuffer[2] = mLineBuffer[0];
                    mLineBuffer[3] = mLineBuffer[1];
                }

                trans.pointValuesToPixel(mLineBuffer);

                if (!mViewPortHandler.isInBoundsRight(mLineBuffer[0])) {
                    break;
                }
                // make sure the lines don't do shitty things outside
                // bounds
                if (!mViewPortHandler.isInBoundsLeft(mLineBuffer[2])
                        || (!mViewPortHandler.isInBoundsTop(mLineBuffer[1]) && !mViewPortHandler
                        .isInBoundsBottom(mLineBuffer[3]))) {
                    continue;
                }

                // get the color that is set for this line-segment
                mRenderPaint.setColor(dataSet.getColor(j));
                canvas.drawLines(mLineBuffer, 0, pointsPerEntryPair * 2, mRenderPaint);
            }
        } else { // only one color per dataset
            if (mLineBuffer.length < Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 2) {
                mLineBuffer = new float[Math.max((entryCount) * pointsPerEntryPair, pointsPerEntryPair) * 4];
            }

            Entry e1, e2;
            e1 = dataSet.getEntryForIndex(mXBounds.min);
            if (e1 != null) {
                int j = 0;
                for (int x = mXBounds.min; x <= mXBounds.range + mXBounds.min; x++) {
                    e1 = dataSet.getEntryForIndex(x == 0 ? 0 : (x - 1));
                    e2 = dataSet.getEntryForIndex(x);
                    if (e1 == null || e2 == null) {
                        continue;
                    }

                    mLineBuffer[j++] = e1.getX() + mOffSet;
                    mLineBuffer[j++] = e1.getY() * phaseY;

                    if (isDrawSteppedEnabled) {
                        mLineBuffer[j++] = e2.getX() + mOffSet;
                        mLineBuffer[j++] = e1.getY() * phaseY;
                        mLineBuffer[j++] = e2.getX() + mOffSet;
                        mLineBuffer[j++] = e1.getY() * phaseY;
                    }

                    mLineBuffer[j++] = e2.getX() + mOffSet;
                    mLineBuffer[j++] = e2.getY() * phaseY;
                }

                if (j > 0) {
                    trans.pointValuesToPixel(mLineBuffer);
                    final int size = Math.max((mXBounds.range + 1) * pointsPerEntryPair, pointsPerEntryPair) * 2;
                    mRenderPaint.setColor(dataSet.getColor());
                    canvas.drawLines(mLineBuffer, 0, size, mRenderPaint);
                }
            }
        }

        mRenderPaint.setPathEffect(null);
    }

    protected Path mGenerateFilledPathBuffer = new Path();

    /**
     * Draws a filled linear path on the canvas.
     */
    protected void drawLinearFill(Canvas c, ILineDataSet dataSet, Transformer trans, XBounds bounds) {
        final Path filled = mGenerateFilledPathBuffer;
        final int startingIndex = bounds.min;
        final int endingIndex = bounds.range + bounds.min;
        final int indexInterval = 128;

        int currentStartIndex;
        int currentEndIndex;
        int iterations = 0;

        // Doing this iteratively in order to avoid OutOfMemory errors that can happen on large bounds sets.
        do {
            currentStartIndex = startingIndex + (iterations * indexInterval);
            currentEndIndex = currentStartIndex + indexInterval;
            currentEndIndex = currentEndIndex > endingIndex ? endingIndex : currentEndIndex;

            if (currentStartIndex <= currentEndIndex) {
                generateFilledPath(dataSet, currentStartIndex, currentEndIndex, filled);

                trans.pathValueToPixel(filled);

                final Drawable drawable = dataSet.getFillDrawable();
                if (drawable != null) {
                    drawFilledPath(c, filled, drawable);
                } else {
                    drawFilledPath(c, filled, dataSet.getFillColor(), dataSet.getFillAlpha());
                }
            }

            iterations++;

        } while (currentStartIndex <= currentEndIndex);
    }

    /**
     * Generates a path that is used for filled drawing.
     *
     * @param dataSet    The dataset from which to read the entries.
     * @param startIndex The index from which to start reading the dataset
     * @param endIndex   The index from which to stop reading the dataset
     * @param outputPath The path object that will be assigned the chart data.
     */
    private void generateFilledPath(final ILineDataSet dataSet, final int startIndex, final int endIndex, final Path outputPath) {
        final float fillMin = dataSet.getFillFormatter().getFillLinePosition(dataSet, mChart);
        final float phaseY = mAnimator.getPhaseY();
        final boolean isDrawSteppedEnabled = dataSet.getMode() == LineDataSet.Mode.STEPPED;
        outputPath.reset();

        final Entry entry = dataSet.getEntryForIndex(startIndex);
        outputPath.moveTo(entry.getX() + mOffSet, fillMin);
        outputPath.lineTo(entry.getX() + mOffSet, entry.getY() * phaseY);

        // create a new path
        Entry currentEntry = null;
        Entry previousEntry = entry;
        for (int x = startIndex + 1; x <= endIndex; x++) {
            currentEntry = dataSet.getEntryForIndex(x);

            if (isDrawSteppedEnabled) {
                outputPath.lineTo(currentEntry.getX() + mOffSet, previousEntry.getY() * phaseY);
            }
            outputPath.lineTo(currentEntry.getX() + mOffSet, currentEntry.getY() * phaseY);
            previousEntry = currentEntry;
        }

        // close up
        if (currentEntry != null) {
            outputPath.lineTo(currentEntry.getX() + mOffSet, fillMin);
        }
        outputPath.close();
    }

    @Override
    public void drawValues(Canvas c) {
        final List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();
        for (int i = 0; i < dataSets.size(); i++) {
            final ILineDataSet dataSet = dataSets.get(i);
            if (!shouldDrawValues(dataSet) || dataSet.getEntryCount() < 1) {
                continue;
            }

            // apply the text-styling defined by the DataSet
            applyValueTextStyle(dataSet);

            final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());

            // make sure the values do not interfear with the circles
            int valOffset = (int) (dataSet.getCircleRadius() * 1.75f);
            if (!dataSet.isDrawCirclesEnabled()) {
                valOffset = valOffset / 2;
            }

            mXBounds.set(mChart, dataSet);
            final float[] positions = trans.generateTransformedValuesLine(dataSet, mAnimator.getPhaseX(), mAnimator
                    .getPhaseY(), mXBounds.min, mXBounds.max);
            final ValueFormatter formatter = dataSet.getValueFormatter();

            final MPPointF iconsOffset = MPPointF.getInstance(dataSet.getIconsOffset());
            iconsOffset.x = Utils.convertDpToPixel(iconsOffset.x);
            iconsOffset.y = Utils.convertDpToPixel(iconsOffset.y);

            final boolean isDrawBS = dataSet.isDrawBS();//是否绘制BS两点图
            final int[] BCircles = dataSet.getBCircles();//B（买）点的位置
            final float[] BValues = dataSet.getBValues();//B（买）点的数值
            final int[] SCircles = dataSet.getSCircles();//S（卖）点的位置
            final float[] SValues = dataSet.getSValues();//S（卖）点的数值

            for (int j = 0; j < positions.length; j += 2) {
                final float x = positions[j];
                final float y = positions[j + 1];

                if (!mViewPortHandler.isInBoundsRight(x)) {
                    break;
                }
                if (!mViewPortHandler.isInBoundsLeft(x) || !mViewPortHandler.isInBoundsY(y)) {
                    continue;
                }

                final Entry entry = dataSet.getEntryForIndex(j / 2 + mXBounds.min);
                String valueText = formatter.getPointLabel(entry);
                if (dataSet.isDrawValuesEnabled()) {
                    if (isDrawBS) {
                        final int xPos = j / 2;
                        if (!isBSCircle(false, BCircles, SCircles, xPos)) {
                            continue;
                        }
                        final boolean isBCircle = isBSCircle(true, BCircles, SCircles, xPos);
                        final int textColorPos = isBCircle ? 0 : 1;
                        if (isBCircle && BValues != null) {
                            for (int k = 0; k < BCircles.length; k++) {
                                if (xPos == BCircles[k] && k < BValues.length) {
                                    valueText = String.valueOf(BValues[k]);
                                }
                            }
                        } else if (SValues != null) {
                            for (int k = 0; k < SCircles.length; k++) {
                                if (xPos == SCircles[k] && k < SValues.length) {
                                    valueText = String.valueOf(SValues[k]);
                                }
                            }
                        }
                        drawValue(c, valueText, x, y - valOffset, dataSet.getValueTextColor(textColorPos));
                    } else {
                        drawValue(c, valueText, x, y - valOffset, dataSet.getValueTextColor(j / 2));
                    }
                }
                if (entry.getIcon() != null && dataSet.isDrawIconsEnabled()) {
                    final Drawable icon = entry.getIcon();
                    Utils.drawImage(
                            c,
                            icon,
                            (int) (x + iconsOffset.x),
                            (int) (y + iconsOffset.y),
                            icon.getIntrinsicWidth(),
                            icon.getIntrinsicHeight());
                }
            }

            MPPointF.recycleInstance(iconsOffset);
        }
    }

    private boolean isBSCircle(boolean isOnlyBCircle, int[] BCircles, int[] SCircles, int position) {
        //是否含有BS（买卖）点的位置
        boolean isBSCircle = false;
        if (BCircles != null) {
            for (int bCircle : BCircles) {
                if (position == bCircle) {
                    isBSCircle = true;
                    break;
                }
            }
        }
        if (!isOnlyBCircle && !isBSCircle && SCircles != null) {
            for (int sCircle : SCircles) {
                if (position == sCircle) {
                    isBSCircle = true;
                    break;
                }
            }
        }
        return isBSCircle;
    }

    private Rect mRect = new Rect();

    //文字超出上下边界进行调整
    private float[] getBSValueTextXY(String valueText, float yValue1, float yValue2,
                                     float x, float y, float valOffset, boolean isFirstPoint) {
        final Paint.FontMetrics fontMetrics = mValuePaint.getFontMetrics();
        final float baseline = mValuePaint.getTextSize() - fontMetrics.descent;
        final float textWidth = mValuePaint.measureText(valueText);
        final float textHeight = fontMetrics.descent - fontMetrics.ascent;
        mValuePaint.getTextBounds(valueText, 0, valueText.length(), mRect);

        float newX = x, newY;
        final float xOffset = Utils.convertDpToPixel(1);
        if (x - textWidth / 2 < mViewPortHandler.contentLeft()) {
            newX = mViewPortHandler.contentLeft() + textWidth / 2 + xOffset;
        } else if (x + textWidth / 2 > mViewPortHandler.contentRight()) {
            newX = mViewPortHandler.contentRight() - textWidth / 2 - xOffset;
        }

        //文字位于圆心点的上方
        final float textTopY = y - valOffset;
        //文字位于圆心点的下方
        final float textBottomY = y + baseline + valOffset;
        //文字是否位于圆心点上方
        final boolean isTextTop = isFirstPoint ? (yValue1 > yValue2) : (yValue1 < yValue2);
        if (isTextTop) {
            newY = textTopY;
            //文字超出上边界
            if (textTopY - baseline < mViewPortHandler.contentTop()) {
                newY = textBottomY;
            }
        } else {
            newY = textBottomY;
            //文字超出下边界
            if (textBottomY > mViewPortHandler.contentBottom()) {
                newY = textTopY;
            }
        }
        return new float[]{newX, newY};
    }

    @Override
    public void drawValue(Canvas c, String valueText, float x, float y, int color) {
        mValuePaint.setColor(color);
        c.drawText(valueText, x, y, mValuePaint);
    }

    @Override
    public void drawExtras(Canvas c) {
        drawCircles(c);
    }

    /**
     * cache for the circle bitmaps of all datasets
     */
    private HashMap<IDataSet, DataSetImageCache> mImageCaches = new HashMap<>();

    /**
     * buffer for drawing the circles
     */
    private float[] mCirclesBuffer = new float[2];

    protected void drawCircles(Canvas c) {
        mRenderPaint.setStyle(Paint.Style.FILL);

        final float phaseY = mAnimator.getPhaseY();
        mCirclesBuffer[0] = 0;
        mCirclesBuffer[1] = 0;

        final List<ILineDataSet> dataSets = mChart.getLineData().getDataSets();
        for (int i = 0; i < dataSets.size(); i++) {
            final ILineDataSet dataSet = dataSets.get(i);
            if (!dataSet.isVisible() || !dataSet.isDrawCirclesEnabled() || dataSet.getEntryCount() == 0) {
                continue;
            }

            mCirclePaintInner.setColor(dataSet.getCircleHoleColor());

            final Transformer trans = mChart.getTransformer(dataSet.getAxisDependency());
            mXBounds.set(mChart, dataSet);

            final float circleRadius = dataSet.getCircleRadius();
            final float circleHoleRadius = dataSet.getCircleHoleRadius();
            final boolean drawCircleHole = dataSet.isDrawCircleHoleEnabled() &&
                    circleHoleRadius < circleRadius && circleHoleRadius > 0.f;
            final boolean drawTransparentCircleHole = drawCircleHole &&
                    dataSet.getCircleHoleColor() == ColorTemplate.COLOR_NONE;

            DataSetImageCache imageCache;
            if (mImageCaches.containsKey(dataSet)) {
                imageCache = mImageCaches.get(dataSet);
            } else {
                imageCache = new DataSetImageCache();
                mImageCaches.put(dataSet, imageCache);
            }

            boolean changeRequired = false;
            if (imageCache != null) {
                changeRequired = imageCache.init(dataSet);
            }

            // only fill the cache with new bitmaps if a change is required
            if (changeRequired) {
                imageCache.fill(dataSet, drawCircleHole, drawTransparentCircleHole);
            }

            final boolean isDrawBS = dataSet.isDrawBS();//是否绘制BS两点图
            final int[] BCircles = dataSet.getBCircles();//B（买）点的位置
            final int[] SCircles = dataSet.getSCircles();//S（卖）点的位置
            final int boundsRangeCount = mXBounds.range + mXBounds.min;
            for (int j = mXBounds.min; j <= boundsRangeCount; j++) {
                final Entry e = dataSet.getEntryForIndex(j);
                if (e == null) {
                    break;
                }
                if (isDrawBS && !isBSCircle(false, BCircles, SCircles, j)) {
                    continue;
                }

                mCirclesBuffer[0] = e.getX() + mOffSet;
                mCirclesBuffer[1] = e.getY() * phaseY;
                trans.pointValuesToPixel(mCirclesBuffer);

                if (!mViewPortHandler.isInBoundsRight(mCirclesBuffer[0])) {
                    break;
                }
                if (!mViewPortHandler.isInBoundsLeft(mCirclesBuffer[0]) ||
                        !mViewPortHandler.isInBoundsY(mCirclesBuffer[1])) {
                    continue;
                }

                Bitmap circleBitmap = null;
                if (imageCache != null) {
                    if (isDrawBS) {
                        final boolean isBCircle = isBSCircle(true, BCircles, SCircles, j);
                        circleBitmap = imageCache.getBitmap(isBCircle ? 0 : 1);
                    } else {
                        circleBitmap = imageCache.getBitmap(j);
                    }
                }
                if (circleBitmap != null) {
                    c.drawBitmap(circleBitmap, mCirclesBuffer[0] - circleRadius, mCirclesBuffer[1] - circleRadius, null);
                }
            }
        }
    }

    @Override
    public void drawHighlighted(Canvas c, Highlight[] indices) {
        final LineData lineData = mChart.getLineData();
        for (Highlight high : indices) {
            final ILineDataSet set = lineData.getDataSetByIndex(high.getDataSetIndex());
            if (set == null || !set.isHighlightEnabled()) {
                continue;
            }

            final Entry e = set.getEntryForXValue(high.getX(), high.getY());
            if (!isInBoundsX(e, set)) {
                continue;
            }

            final MPPointD pix = mChart.getTransformer(set.getAxisDependency())
                    .getPixelForValues(e.getX() + mOffSet, e.getY() * mAnimator.getPhaseY());
            high.setDraw((float) pix.x, (float) pix.y);
            // draw the lines
            drawHighlightLines(c, (float) pix.x, (float) pix.y, set);
        }
    }

    /**
     * Sets the Bitmap.Config to be used by this renderer.
     * Default: Bitmap.Config.ARGB_8888
     * Use Bitmap.Config.ARGB_4444 to consume less memory.
     */
    public void setBitmapConfig(Bitmap.Config config) {
        mBitmapConfig = config;
        releaseBitmap();
    }

    /**
     * Returns the Bitmap.Config that is used by this renderer.
     */
    public Bitmap.Config getBitmapConfig() {
        return mBitmapConfig;
    }

    /**
     * Releases the drawing bitmap. This should be called when {@link LineChart#onDetachedFromWindow()}.
     */
    @SuppressWarnings("JavadocReference")
    public void releaseBitmap() {
        if (mBitmapCanvas != null) {
            mBitmapCanvas.setBitmap(null);
            mBitmapCanvas = null;
        }
        if (mDrawBitmap != null) {
            final Bitmap drawBitmap = mDrawBitmap.get();
            if (drawBitmap != null) {
                drawBitmap.recycle();
            }
            mDrawBitmap.clear();
            mDrawBitmap = null;
        }
    }

    private class DataSetImageCache {
        private Path mCirclePathBuffer = new Path();
        private Bitmap[] circleBitmaps;

        /**
         * Sets up the cache, returns true if a change of cache was required.
         */
        protected boolean init(ILineDataSet set) {
            final int size = set.getCircleColorCount();
            boolean changeRequired = false;

            if (circleBitmaps == null) {
                circleBitmaps = new Bitmap[size];
                changeRequired = true;
            } else if (circleBitmaps.length != size) {
                circleBitmaps = new Bitmap[size];
                changeRequired = true;
            }
            return changeRequired;
        }

        /**
         * Fills the cache with bitmaps for the given dataset.
         */
        protected void fill(ILineDataSet set, boolean drawCircleHole, boolean drawTransparentCircleHole) {
            final int colorCount = set.getCircleColorCount();
            final float circleRadius = set.getCircleRadius();
            final float circleHoleRadius = set.getCircleHoleRadius();

            for (int i = 0; i < colorCount; i++) {
                final Bitmap.Config conf = Bitmap.Config.ARGB_4444;
                final Bitmap circleBitmap = Bitmap.createBitmap((int) (circleRadius * 2.1), (int) (circleRadius * 2.1), conf);
                final Canvas canvas = new Canvas(circleBitmap);
                circleBitmaps[i] = circleBitmap;
                mRenderPaint.setColor(set.getCircleColor(i));

                if (drawTransparentCircleHole) {
                    // Begin path for circle with hole
                    mCirclePathBuffer.reset();

                    mCirclePathBuffer.addCircle(
                            circleRadius,
                            circleRadius,
                            circleRadius,
                            Path.Direction.CW);

                    // Cut hole in path
                    mCirclePathBuffer.addCircle(
                            circleRadius,
                            circleRadius,
                            circleHoleRadius,
                            Path.Direction.CCW);

                    // Fill in-between
                    canvas.drawPath(mCirclePathBuffer, mRenderPaint);
                } else {
                    canvas.drawCircle(
                            circleRadius,
                            circleRadius,
                            circleRadius,
                            mRenderPaint);

                    if (drawCircleHole) {
                        canvas.drawCircle(
                                circleRadius,
                                circleRadius,
                                circleHoleRadius,
                                mCirclePaintInner);
                    }
                }
            }
        }

        /**
         * Returns the cached Bitmap at the given index.
         */
        protected Bitmap getBitmap(int index) {
            return circleBitmaps[index % circleBitmaps.length];
        }
    }
}
