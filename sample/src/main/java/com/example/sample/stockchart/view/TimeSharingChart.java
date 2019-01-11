package com.example.sample.stockchart.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sample.R;
import com.example.sample.stockchart.ColorContentYAxisRenderer;
import com.example.sample.stockchart.TimeSharingLineChart;
import com.example.sample.stockchart.TimeSharingXAxis;
import com.example.sample.stockchart.data.TimeSharingDataManage;
import com.example.sample.stockchart.enums.ChartType;
import com.example.sample.stockchart.model.TimeSharingDataModel;
import com.example.sample.stockchart.utils.CommonUtil;
import com.example.sample.stockchart.utils.VolFormatter;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Transformer;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingChart extends LinearLayout {
    private final Context mContext;
    private final TimeSharingLineChart mLineChart;
    private final TimeSharingBarChart mBarChart;

    private TimeSharingXAxis mXAxisLine;
    private YAxis mAxisRightLine;
    private YAxis mAxisLeftLine;

    private TimeSharingXAxis mXAxisBar;
    private YAxis mAxisLeftBar;
    private YAxis mAxisRightBar;

    private int mMaxCount = ChartType.HK_ONE_DAY.getPointNum();//最大可见数量，即分时一天最大数据点数
    private SparseArray<String> mXLabels = new SparseArray<>();//X轴刻度label
    private final int[] mColorArray;
    private static final int PRECISION = 3;//小数精度

    public TimeSharingChart(Context context) {
        this(context, null);
    }

    public TimeSharingChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        View.inflate(context, R.layout.view_chart_time_sharing, this);
        mLineChart = findViewById(R.id.view_chart_time_sharing_line);
        mBarChart = findViewById(R.id.view_chart_time_sharing_bar);

        mColorArray = new int[]{
                ContextCompat.getColor(mContext, R.color.up_color),
                ContextCompat.getColor(mContext, R.color.equal_color),
                ContextCompat.getColor(mContext, R.color.down_color)
        };
    }

    public void initChart() {
        //主图
        mLineChart.setScaleEnabled(false);
        mLineChart.setDrawBorders(true);
        mLineChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mLineChart.setBorderWidth(0.7f);
        mLineChart.setNoDataText(getResources().getString(R.string.loading));
        //图例
        final Legend lineChartLegend = mLineChart.getLegend();
        lineChartLegend.setEnabled(false);
        mLineChart.setDescription(null);

        //副图
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawBorders(true);
        mBarChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mBarChart.setBorderWidth(0.7f);
        mBarChart.setNoDataText(getResources().getString(R.string.loading));
        //图例
        final Legend barChartLegend = mBarChart.getLegend();
        barChartLegend.setEnabled(false);
        mBarChart.setDescription(null);

        //主图X轴
        mXAxisLine = (TimeSharingXAxis) mLineChart.getXAxis();
        mXAxisLine.setDrawAxisLine(false);
        mXAxisLine.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        mXAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxisLine.setAvoidFirstLastClipping(true);
        mXAxisLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mXAxisLine.setGridLineWidth(0.7f);

        //主图左Y轴
        mAxisLeftLine = mLineChart.getAxisLeft();
        mAxisLeftLine.setLabelCount(5, true);
        mAxisLeftLine.setDrawGridLines(false);
        mAxisLeftLine.setValueLineInside(true);
        mAxisLeftLine.setDrawTopBottomGridLine(false);
        mAxisLeftLine.setDrawAxisLine(false);
        mAxisLeftLine.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisLeftLine.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisLeftLine.setValueFormatter((value, axis) -> NumberUtils.keepPrecisionR(value, PRECISION));

        //主图右Y轴
        mAxisRightLine = mLineChart.getAxisRight();
        mAxisRightLine.setLabelCount(5, true);
        mAxisRightLine.setDrawTopBottomGridLine(false);
        mAxisRightLine.setDrawGridLines(true);
        mAxisRightLine.setGridLineWidth(0.7f);
        mAxisRightLine.enableGridDashedLine(
                CommonUtil.dip2px(mContext, 4),
                CommonUtil.dip2px(mContext, 3),
                0);
        mAxisRightLine.setDrawAxisLine(false);
        mAxisRightLine.setValueLineInside(true);
        mAxisRightLine.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisRightLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mAxisRightLine.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisRightLine.setValueFormatter((value, axis) -> {
            final DecimalFormat mFormat = new DecimalFormat("#0.00%");
            return mFormat.format(value);
        });

        //副图X轴
        mXAxisBar = (TimeSharingXAxis) mBarChart.getXAxis();
        mXAxisBar.setDrawLabels(false);
        mXAxisBar.setDrawAxisLine(false);
        mXAxisBar.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        mXAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxisBar.setAvoidFirstLastClipping(true);
        mXAxisBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mXAxisBar.setGridLineWidth(0.7f);

        //副图左Y轴
        mAxisLeftBar = mBarChart.getAxisLeft();
        mAxisLeftBar.setDrawGridLines(false);
        mAxisLeftBar.setDrawAxisLine(false);
        mAxisLeftBar.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisLeftBar.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisLeftBar.setDrawLabels(true);
        mAxisLeftBar.setLabelCount(2, true);
        mAxisLeftBar.setAxisMinimum(0);
        mAxisLeftBar.setSpaceTop(5);
        mAxisLeftBar.setValueLineInside(true);

        //副图右Y轴
        mAxisRightBar = mBarChart.getAxisRight();
        mAxisRightBar.setDrawLabels(false);
        mAxisRightBar.setDrawGridLines(true);
        mAxisRightBar.setDrawAxisLine(false);
        mAxisRightBar.setLabelCount(3, true);
        mAxisRightBar.setDrawTopBottomGridLine(false);
        mAxisRightBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mAxisRightBar.setGridLineWidth(0.7f);
        mAxisRightBar.enableGridDashedLine(
                CommonUtil.dip2px(mContext, 4),
                CommonUtil.dip2px(mContext, 3),
                0);
    }

    /**
     * 是否显示坐标轴label
     */
    private void setShowLabels(boolean isShow) {
        mLineChart.getAxisLeft().setDrawLabels(isShow);
        mLineChart.getAxisRight().setDrawLabels(isShow);
        mLineChart.getXAxis().setDrawLabels(isShow);
        mBarChart.getAxisLeft().setDrawLabels(isShow);
    }

    /**
     * 设置分时数据
     */
    public void setDataToChart(TimeSharingDataManage mData) {
        if (mData.getDatas().size() == 0) {
            mLineChart.setNoDataText(getResources().getString(R.string.no_data));
            mBarChart.setNoDataText(getResources().getString(R.string.no_data));
        }

        setMaxCount(ChartType.ONE_DAY.getPointNum());
        setXLabels(mData.getTimeSharingXLabels());
        setShowLabels(true);

        mAxisLeftLine.setAxisMinimum(mData.getMin());
        mAxisLeftLine.setAxisMaximum(mData.getMax());

        //Y轴label渲染颜色
        final Transformer leftYTransformer = mLineChart.getRendererLeftYAxis().getTransformer();
        final ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(
                mLineChart.getViewPortHandler(), mAxisLeftLine, leftYTransformer);
        leftColorContentYAxisRenderer.setLabelColor(mColorArray);
        leftColorContentYAxisRenderer.setClosePrice(mData.getPreClose());
        mLineChart.setRendererLeftYAxis(leftColorContentYAxisRenderer);

        //Y轴label渲染颜色
        final Transformer rightYTransformer = mLineChart.getRendererRightYAxis().getTransformer();
        final ColorContentYAxisRenderer rightColorContentYAxisRenderer = new ColorContentYAxisRenderer(
                mLineChart.getViewPortHandler(), mAxisRightLine, rightYTransformer);
        rightColorContentYAxisRenderer.setLabelColor(mColorArray);
        rightColorContentYAxisRenderer.setClosePrice(mData.getPreClose());
        mLineChart.setRendererRightYAxis(rightColorContentYAxisRenderer);

        if (Float.isNaN(mData.getPercentMax())
                || Float.isNaN(mData.getPercentMin())
                || Float.isNaN(mData.getVolMaxTime())) {
            mAxisLeftBar.setAxisMaximum(0);
            mAxisRightLine.setAxisMinimum(-0.01f);
            mAxisRightLine.setAxisMaximum(0.01f);
        } else {
            mAxisLeftBar.setAxisMaximum(mData.getVolMaxTime());
            mAxisRightLine.setAxisMinimum(mData.getPercentMin());
            mAxisRightLine.setAxisMaximum(mData.getPercentMax());
        }

        mAxisLeftBar.setValueFormatter(new VolFormatter(mContext));

        final ArrayList<Entry> lineCJEntries = new ArrayList<>();
        final ArrayList<Entry> lineJJEntries = new ArrayList<>();
        final ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0, j = 0; i < mData.getDatas().size(); i++, j++) {
            final TimeSharingDataModel t = mData.getDatas().get(j);
            if (t == null) {
                lineCJEntries.add(new Entry(i, i, Float.NaN));
                lineJJEntries.add(new Entry(i, i, Float.NaN));
                barEntries.add(new BarEntry(i, i, Float.NaN));
                continue;
            }
            lineCJEntries.add(new Entry(i, i, (float) mData.getDatas().get(i).getNowPrice()));
            lineJJEntries.add(new Entry(i, i, (float) mData.getDatas().get(i).getAveragePrice()));
            barEntries.add(new BarEntry(i, i, mData.getDatas().get(i).getVolume()));
        }
        final LineDataSet d1 = new LineDataSet(lineCJEntries, "分时线");
        final LineDataSet d2 = new LineDataSet(lineJJEntries, "均价");
        d1.setDrawCircleDashMarker(false);
        d2.setDrawCircleDashMarker(false);
        d1.setDrawValues(false);
        d2.setDrawValues(false);
        d1.setLineWidth(0.7f);
        d2.setLineWidth(0.7f);
        d1.setColor(ContextCompat.getColor(mContext, R.color.minute_blue));
        d2.setColor(ContextCompat.getColor(mContext, R.color.minute_yellow));
        d1.setDrawFilled(true);
        d1.setFillColor(ContextCompat.getColor(mContext, R.color.fill_Color));
        d1.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        d1.setHighlightEnabled(false);
        d2.setHighlightEnabled(false);
        d1.setDrawCircles(false);
        d2.setDrawCircles(false);
        d1.setAxisDependency(YAxis.AxisDependency.LEFT);
        d1.setPrecision(PRECISION);
        d1.setTimeDayType(1);//设置分时图类型
        d2.setTimeDayType(1);

        final ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(d1);
        sets.add(d2);
        final LineData cd = new LineData(sets);
        mLineChart.setData(cd);

        final BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        barDataSet.setHighlightEnabled(false);
        barDataSet.setDrawValues(false);
        barDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        barDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        barDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        barDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        final BarData barData = new BarData(barDataSet);
        mBarChart.setData(barData);

        //请注意，修改视口的所有方法需要在为Chart设置数据之后调用。
        //设置当前视图四周的偏移量。 设置这个，将阻止图表自动计算它的偏移量。使用 resetViewPortOffsets()撤消此设置。
        mLineChart.setViewPortOffsets(
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 15));
        mBarChart.setViewPortOffsets(
                CommonUtil.dip2px(mContext, 5),
                0,
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 5));

        //下面方法需在填充数据后调用
        mXAxisLine.setXLabels(getXLabels());
        mXAxisLine.setLabelCount(getXLabels().size(), false);
        mXAxisBar.setXLabels(getXLabels());
        mXAxisBar.setLabelCount(getXLabels().size(), false);
        mLineChart.setVisibleXRange(mMaxCount, mMaxCount);
        mBarChart.setVisibleXRange(mMaxCount, mMaxCount);
        //moveViewTo(...) 方法会自动调用 invalidate()
        mLineChart.moveViewToX(mData.getDatas().size() - 1);
        mBarChart.moveViewToX(mData.getDatas().size() - 1);
    }

    public void dynamicsAddOne(TimeSharingDataModel dataModel, int length) {
        final int index = length - 1;
        final LineData lineData = mLineChart.getData();
        final ILineDataSet d1 = lineData.getDataSetByIndex(0);
        d1.addEntry(new Entry(index, index, (float) dataModel.getNowPrice()));
        final ILineDataSet d2 = lineData.getDataSetByIndex(1);
        d2.addEntry(new Entry(index, index, (float) dataModel.getAveragePrice()));

        final BarData barData = mBarChart.getData();
        final IBarDataSet barDataSet = barData.getDataSetByIndex(0);
        barDataSet.addEntry(new BarEntry(index, index, dataModel.getVolume()));
        lineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        barData.notifyDataChanged();
        mBarChart.notifyDataSetChanged();
        mLineChart.setVisibleXRange(mMaxCount, mMaxCount);
        mBarChart.setVisibleXRange(mMaxCount, mMaxCount);
        //动态添加或移除数据后， 调用invalidate()刷新图表之前 必须调用 notifyDataSetChanged() .
        mLineChart.moveViewToX(index);
        mBarChart.moveViewToX(index);
    }

    public void dynamicsUpdateOne(TimeSharingDataModel dataModel, int length) {
        final int index = length - 1;
        final LineData lineData = mLineChart.getData();
        final ILineDataSet d1 = lineData.getDataSetByIndex(0);
        final Entry e = d1.getEntryForIndex(index);
        d1.removeEntry(e);
        d1.addEntry(new Entry(index, index, (float) dataModel.getNowPrice()));

        final ILineDataSet d2 = lineData.getDataSetByIndex(1);
        final Entry e2 = d2.getEntryForIndex(index);
        d2.removeEntry(e2);
        d2.addEntry(new Entry(index, index, (float) dataModel.getAveragePrice()));

        final BarData barData = mBarChart.getData();
        final IBarDataSet barDataSet = barData.getDataSetByIndex(0);
        barDataSet.removeEntry(index);
        barDataSet.addEntry(new BarEntry(index, index, dataModel.getVolume()));

        lineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        mLineChart.moveViewToX(index);

        barData.notifyDataChanged();
        mBarChart.notifyDataSetChanged();
        mBarChart.moveViewToX(index);
    }

    public void cleanData() {
        if (mLineChart != null && mLineChart.getLineData() != null) {
            setShowLabels(false);
            mLineChart.clearValues();
            mBarChart.clearValues();
        }
    }

    public void setXLabels(SparseArray<String> xLabels) {
        this.mXLabels = xLabels;
    }

    private SparseArray<String> getXLabels() {
        return mXLabels;
    }

    public void setMaxCount(int maxCount) {
        this.mMaxCount = maxCount;
    }
}
