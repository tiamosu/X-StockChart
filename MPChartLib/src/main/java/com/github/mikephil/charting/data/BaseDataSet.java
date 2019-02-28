package com.github.mikephil.charting.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Typeface;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * Created by Philipp Jahoda on 21/10/15.
 * This is the base dataset of all DataSets. It's purpose is to implement critical methods
 * provided by the IDataSet interface.
 */
@SuppressWarnings({"WeakerAccess", "unused", "DanglingJavadoc"})
public abstract class BaseDataSet<T extends Entry> implements IDataSet<T> {

    /**
     * List representing all colors that are used for this DataSet
     */
    protected List<Integer> mColors;

    protected GradientColor mGradientColor = null;

    protected List<GradientColor> mGradientColors = null;

    /**
     * List representing all colors that are used for drawing the actual values for this DataSet
     */
    protected List<Integer> mValueColors;

    /**
     * label that describes the DataSet or the data the DataSet represents
     */
    private String mLabel = "DataSet";

    /**
     * this specifies which axis this DataSet should be plotted against
     */
    protected YAxis.AxisDependency mAxisDependency = YAxis.AxisDependency.LEFT;

    /**
     * if true, value highlightning is enabled
     */
    protected boolean mHighlightEnabled = true;

    /**
     * custom formatter that is used instead of the auto-formatter if set
     */
    protected transient ValueFormatter mValueFormatter;

    /**
     * the typeface used for the value text
     */
    protected Typeface mValueTypeface;

    private Legend.LegendForm mForm = Legend.LegendForm.DEFAULT;
    private float mFormSize = Float.NaN;
    private float mFormLineWidth = Float.NaN;
    private DashPathEffect mFormLineDashEffect = null;

    /**
     * if true, y-values are drawn on the chart
     */
    protected boolean mDrawValues = true;

    /**
     * if true, y-icons are drawn on the chart
     */
    protected boolean mDrawIcons = true;

    /**
     * the offset for drawing icons (in dp)
     */
    protected MPPointF mIconsOffset = new MPPointF();

    /**
     * the size of the value-text labels
     */
    protected float mValueTextSize = 17f;

    /**
     * flag that indicates if the DataSet is visible or not
     */
    protected boolean mVisible = true;

    //分时图类型，区分当日分时和多日分时
    protected int mTimeDayType = 1;
    //是否绘制BS（买卖）两点图
    protected boolean mIsDrawBS;
    //BS（买卖）两点位置
    protected int[] mBSCircles = new int[2];
    //K线蜡烛图数值颜色
    protected int mCandleDataTextColor = Color.BLUE;
    //分时图成交价数据，用于分时图副图柱形颜色填充判断
    protected List<Entry> mPriceList;

    /**
     * Default constructor.
     */
    public BaseDataSet() {
        mColors = new ArrayList<>();
        mValueColors = new ArrayList<>();

        // default color
        mColors.add(Color.rgb(140, 234, 255));
        mValueColors.add(Color.BLACK);
    }

    /**
     * Constructor with label.
     */
    public BaseDataSet(String label) {
        this();
        this.mLabel = label;
    }

    /**
     * Use this method to tell the data set that the underlying data has changed.
     */
    public void notifyDataSetChanged() {
        calcMinMax();
    }

    /**
     * ###### ###### COLOR GETTING RELATED METHODS ##### ######
     */

    @Override
    public List<Integer> getColors() {
        return mColors;
    }

    public List<Integer> getValueColors() {
        return mValueColors;
    }

    @Override
    public int getColor() {
        return mColors.get(0);
    }

    @Override
    public int getColor(int index) {
        return mColors.get(index % mColors.size());
    }

    @Override
    public GradientColor getGradientColor() {
        return mGradientColor;
    }

    @Override
    public List<GradientColor> getGradientColors() {
        return mGradientColors;
    }

    @Override
    public GradientColor getGradientColor(int index) {
        return mGradientColors.get(index % mGradientColors.size());
    }

    /**
     * ###### ###### COLOR SETTING RELATED METHODS ##### ######
     */

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     */
    public void setColors(List<Integer> colors) {
        this.mColors = colors;
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. If you are using colors from the resources,
     * make sure that the colors are already prepared (by calling
     * getResources().getColor(...)) before adding them to the DataSet.
     */
    public void setColors(int... colors) {
        this.mColors = ColorTemplate.createColors(colors);
    }

    /**
     * Sets the colors that should be used fore this DataSet. Colors are reused
     * as soon as the number of Entries the DataSet represents is higher than
     * the size of the colors array. You can use
     * "new int[] { R.color.red, R.color.green, ... }" to provide colors for
     * this method. Internally, the colors are resolved using
     * getResources().getColor(...)
     */
    public void setColors(int[] colors, Context c) {
        if (mColors == null) {
            mColors = new ArrayList<>();
        }
        mColors.clear();

        for (int color : colors) {
            mColors.add(c.getResources().getColor(color));
        }
    }

    /**
     * Adds a new color to the colors array of the DataSet.
     */
    public void addColor(int color) {
        if (mColors == null) {
            mColors = new ArrayList<>();
        }
        mColors.add(color);
    }

    /**
     * Sets the one and ONLY color that should be used for this DataSet.
     * Internally, this recreates the colors array and adds the specified color.
     */
    public void setColor(int color) {
        resetColors();
        mColors.add(color);
    }

    /**
     * Sets the start and end color for gradient color, ONLY color that should be used for this DataSet.
     */
    public void setGradientColor(int startColor, int endColor) {
        mGradientColor = new GradientColor(startColor, endColor);
    }

    /**
     * Sets the start and end color for gradient colors, ONLY color that should be used for this DataSet.
     */
    public void setGradientColors(List<GradientColor> gradientColors) {
        this.mGradientColors = gradientColors;
    }

    /**
     * Sets a color with a specific alpha value.
     *
     * @param alpha from 0-255
     */
    public void setColor(int color, int alpha) {
        setColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
    }

    /**
     * Sets colors with a specific alpha value.
     */
    public void setColors(int[] colors, int alpha) {
        resetColors();
        for (int color : colors) {
            addColor(Color.argb(alpha, Color.red(color), Color.green(color), Color.blue(color)));
        }
    }

    /**
     * Resets all colors of this DataSet and recreates the colors array.
     */
    public void resetColors() {
        if (mColors == null) {
            mColors = new ArrayList<>();
        }
        mColors.clear();
    }

    /**
     * ###### ###### OTHER STYLING RELATED METHODS ##### ######
     */

    @Override
    public void setLabel(String label) {
        mLabel = label;
    }

    @Override
    public String getLabel() {
        return mLabel;
    }

    @Override
    public void setHighlightEnabled(boolean enabled) {
        mHighlightEnabled = enabled;
    }

    @Override
    public boolean isHighlightEnabled() {
        return mHighlightEnabled;
    }

    @Override
    public void setValueFormatter(ValueFormatter f) {
        if (f != null) {
            mValueFormatter = f;
        }
    }

    @Override
    public ValueFormatter getValueFormatter() {
        if (needsFormatter()) {
            return Utils.getDefaultValueFormatter();
        }
        return mValueFormatter;
    }

    @Override
    public boolean needsFormatter() {
        return mValueFormatter == null;
    }

    @Override
    public void setValueTextColor(int color) {
        mValueColors.clear();
        mValueColors.add(color);
    }

    @Override
    public void setValueTextColors(List<Integer> colors) {
        mValueColors = colors;
    }

    @Override
    public void setValueTypeface(Typeface tf) {
        mValueTypeface = tf;
    }

    @Override
    public void setValueTextSize(float size) {
        mValueTextSize = Utils.convertDpToPixel(size);
    }

    @Override
    public int getValueTextColor() {
        return mValueColors.get(0);
    }

    @Override
    public int getValueTextColor(int index) {
        return mValueColors.get(index % mValueColors.size());
    }

    @Override
    public Typeface getValueTypeface() {
        return mValueTypeface;
    }

    @Override
    public float getValueTextSize() {
        return mValueTextSize;
    }

    public void setForm(Legend.LegendForm form) {
        mForm = form;
    }

    @Override
    public Legend.LegendForm getForm() {
        return mForm;
    }

    public void setFormSize(float formSize) {
        mFormSize = formSize;
    }

    @Override
    public float getFormSize() {
        return mFormSize;
    }

    public void setFormLineWidth(float formLineWidth) {
        mFormLineWidth = formLineWidth;
    }

    @Override
    public float getFormLineWidth() {
        return mFormLineWidth;
    }

    public void setFormLineDashEffect(DashPathEffect dashPathEffect) {
        mFormLineDashEffect = dashPathEffect;
    }

    @Override
    public DashPathEffect getFormLineDashEffect() {
        return mFormLineDashEffect;
    }

    @Override
    public void setDrawValues(boolean enabled) {
        this.mDrawValues = enabled;
    }

    @Override
    public boolean isDrawValuesEnabled() {
        return mDrawValues;
    }

    @Override
    public void setDrawIcons(boolean enabled) {
        mDrawIcons = enabled;
    }

    @Override
    public boolean isDrawIconsEnabled() {
        return mDrawIcons;
    }

    @Override
    public void setIconsOffset(MPPointF offsetDp) {
        mIconsOffset.x = offsetDp.x;
        mIconsOffset.y = offsetDp.y;
    }

    @Override
    public MPPointF getIconsOffset() {
        return mIconsOffset;
    }

    @Override
    public void setVisible(boolean visible) {
        mVisible = visible;
    }

    @Override
    public boolean isVisible() {
        return mVisible;
    }

    @Override
    public YAxis.AxisDependency getAxisDependency() {
        return mAxisDependency;
    }

    @Override
    public void setAxisDependency(YAxis.AxisDependency dependency) {
        mAxisDependency = dependency;
    }

    public void setTimeDayType(int timeDayType) {
        this.mTimeDayType = timeDayType;
    }

    @Override
    public int getTimeDayType() {
        return mTimeDayType;
    }

    public void setDrawBS(boolean drawBS) {
        mIsDrawBS = drawBS;
    }

    @Override
    public boolean isDrawBS() {
        return mIsDrawBS;
    }

    @Override
    public int[] getBSCircles() {
        return mBSCircles;
    }

    public void setBSCircles(@NonNull int... BSCircles) {
        mBSCircles = BSCircles;
    }

    @Override
    public int getCandleDataTextColor() {
        return mCandleDataTextColor;
    }

    public void setCandleDataTextColor(@ColorInt int candleDataTextColor) {
        mCandleDataTextColor = candleDataTextColor;
    }

    @Override
    public List<Entry> getPriceList() {
        if (mPriceList == null) {
            mPriceList = new ArrayList<>();
        }
        return mPriceList;
    }

    public void setPriceData(List<Entry> entries) {
        mPriceList = entries;
    }

    /**
     * ###### ###### DATA RELATED METHODS ###### ######
     */
    @Override
    public int getIndexInEntries(int xIndex) {
        for (int i = 0; i < getEntryCount(); i++) {
            if (xIndex == getEntryForIndex(i).getX()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean removeFirst() {
        if (getEntryCount() > 0) {
            final T entry = getEntryForIndex(0);
            return removeEntry(entry);
        } else {
            return false;
        }
    }

    @Override
    public boolean removeLast() {
        if (getEntryCount() > 0) {
            final T e = getEntryForIndex(getEntryCount() - 1);
            return removeEntry(e);
        } else {
            return false;
        }
    }

    @Override
    public boolean removeEntryByXValue(float xValue) {
        final T e = getEntryForXValue(xValue, Float.NaN);
        return removeEntry(e);
    }

    @Override
    public boolean removeEntry(int index) {
        final T e = getEntryForIndex(index);
        return removeEntry(e);
    }

    @Override
    public boolean contains(T e) {
        for (int i = 0; i < getEntryCount(); i++) {
            if (getEntryForIndex(i).equals(e)) {
                return true;
            }
        }
        return false;
    }

    protected void copy(BaseDataSet baseDataSet) {
        baseDataSet.mColors = mColors;
        baseDataSet.mGradientColor = mGradientColor;
        baseDataSet.mGradientColors = mGradientColors;
        baseDataSet.mValueColors = mValueColors;
        baseDataSet.mLabel = mLabel;
        baseDataSet.mAxisDependency = mAxisDependency;
        baseDataSet.mHighlightEnabled = mHighlightEnabled;
        baseDataSet.mValueFormatter = mValueFormatter;
        baseDataSet.mValueTypeface = mValueTypeface;
        baseDataSet.mForm = mForm;
        baseDataSet.mFormSize = mFormSize;
        baseDataSet.mFormLineWidth = mFormLineWidth;
        baseDataSet.mFormLineDashEffect = mFormLineDashEffect;
        baseDataSet.mDrawValues = mDrawValues;
        baseDataSet.mDrawIcons = mDrawIcons;
        baseDataSet.mIconsOffset = mIconsOffset;
        baseDataSet.mValueTextSize = mValueTextSize;
        baseDataSet.mVisible = mVisible;

        baseDataSet.mTimeDayType = mTimeDayType;
        baseDataSet.mIsDrawBS = mIsDrawBS;
        baseDataSet.mBSCircles = mBSCircles;
        baseDataSet.mCandleDataTextColor = mCandleDataTextColor;
        baseDataSet.mPriceList = mPriceList;
    }
}
