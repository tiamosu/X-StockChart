package com.example.sample.stockchart.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewStub;
import android.widget.LinearLayout;

import com.example.sample.R;
import com.example.sample.stockchart.TimeSharingXAxis;
import com.example.sample.stockchart.TimeSharingYAxis;
import com.example.sample.stockchart.data.TimeSharingDataManage;
import com.example.sample.stockchart.enums.ChartType;
import com.example.sample.stockchart.model.TimeSharingDataModel;
import com.example.sample.stockchart.utils.CommonUtil;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.NumberUtils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * @author weixia
 * @date 2019/1/10.
 */
public class TimeSharingChart extends LinearLayout {
    private final Context mContext;
    private final TimeSharingLineChart mLineChart;
    private final ViewStub mBarChartViewStub;
    private TimeSharingBarChart mBarChart;

    private TimeSharingXAxis mXAxisLine;
    private TimeSharingYAxis mAxisRightLine;
    private TimeSharingYAxis mAxisLeftLine;

    private TimeSharingXAxis mXAxisBar;
    private TimeSharingYAxis mAxisLeftBar;

    private static final int TYPE_LINE_CJ = 0;//最新价/收盘价
    private static final int TYPE_LINE_JJ = 1;//股票均价
    private int mMaxCount = ChartType.HK_ONE_DAY.getPointNum();//最大可见数量，即分时一天最大数据点数
    private SparseArray<String> mXLabels = new SparseArray<>();//X轴刻度label
    private final int[] mColorArray;
    private static final int PRECISION = 3;//小数精度

    public TimeSharingChart(Context context) {
        this(context, null);
    }

    public TimeSharingChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mContext = context;

        View.inflate(context, R.layout.view_chart_time_sharing, this);
        mLineChart = findViewById(R.id.view_chart_time_sharing_line);
        mBarChartViewStub = findViewById(R.id.view_chart_time_sharing_bar_vs);

        mColorArray = new int[]{
                ContextCompat.getColor(mContext, R.color.up_color),
                ContextCompat.getColor(mContext, R.color.equal_color),
                ContextCompat.getColor(mContext, R.color.down_color)
        };
    }

    public void initChart() {
        initChartLine();
        initChartBar();

        //初始化图表线框显示（需要添加一条数据才能显示图表框）
        final TimeSharingDataManage dataManage = new TimeSharingDataManage();
        dataManage.getDatas().add(new TimeSharingDataModel());
        setDataToChart(dataManage);
    }

    private void initChartLine() {
        //主图
        mLineChart.setScaleEnabled(false);
        mLineChart.setDrawBorders(true);
        mLineChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mLineChart.setBorderWidth(0.7f);
        mLineChart.setNoDataText(getResources().getString(R.string.loading));
        mLineChart.setDescription(null);
        //图例
        final Legend lineChartLegend = mLineChart.getLegend();
        lineChartLegend.setEnabled(false);

        //主图X轴
        mXAxisLine = (TimeSharingXAxis) mLineChart.getXAxis();
        mXAxisLine.setDrawAxisLine(false);
        mXAxisLine.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        mXAxisLine.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxisLine.setAvoidFirstLastClipping(true);
        mXAxisLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mXAxisLine.setGridLineWidth(0.7f);
        mXAxisLine.enableGridDashedLine(CommonUtil.dip2px(mContext, 4f),
                CommonUtil.dip2px(mContext, 3f), 0);

        //主图左Y轴
        mAxisLeftLine = (TimeSharingYAxis) mLineChart.getAxisLeft();
        mAxisLeftLine.setLabelCount(3, true);
        mAxisLeftLine.setDrawGridLines(true);
        mAxisLeftLine.setGridLineWidth(0.7f);
        mAxisLeftLine.enableGridDashedLine(CommonUtil.dip2px(mContext, 4f),
                CommonUtil.dip2px(mContext, 3f), 0);
        mAxisLeftLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mAxisLeftLine.setValueLineInside(true);
        mAxisLeftLine.setDrawTopBottomGridLine(false);
        mAxisLeftLine.setDrawAxisLine(false);
        mAxisLeftLine.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisLeftLine.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisLeftLine.setLabelColorArray(mColorArray);
        mAxisLeftLine.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return NumberUtils.keepPrecisionR(value, PRECISION);
            }
        });

        //主图右Y轴
        mAxisRightLine = (TimeSharingYAxis) mLineChart.getAxisRight();
        mAxisRightLine.setLabelCount(2, true);
        mAxisRightLine.setDrawTopBottomGridLine(false);
        mAxisRightLine.setDrawGridLines(false);
        mAxisRightLine.setDrawAxisLine(false);
        mAxisRightLine.setValueLineInside(true);
        mAxisRightLine.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisRightLine.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisRightLine.setLabelColorArray(mColorArray);
        mAxisRightLine.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                final DecimalFormat mFormat = new DecimalFormat("#0.00%");
                return mFormat.format(value);
            }
        });
    }

    private void initChartBar() {
        mBarChart = (TimeSharingBarChart) mBarChartViewStub.inflate();
        //副图
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawBorders(true);
        mBarChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mBarChart.setBorderWidth(0.7f);
        mBarChart.setNoDataText(getResources().getString(R.string.loading));
        mBarChart.setDescription(null);
        //图例
        final Legend barChartLegend = mBarChart.getLegend();
        barChartLegend.setEnabled(false);

        //副图X轴
        mXAxisBar = (TimeSharingXAxis) mBarChart.getXAxis();
        mXAxisBar.setDrawLabels(false);
        mXAxisBar.setDrawAxisLine(false);
        mXAxisBar.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        mXAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        mXAxisBar.setAvoidFirstLastClipping(true);
        mXAxisBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mXAxisBar.setGridLineWidth(0.7f);
        mXAxisBar.enableGridDashedLine(CommonUtil.dip2px(mContext, 4f),
                CommonUtil.dip2px(mContext, 3f), 0);

        //副图左Y轴
        mAxisLeftBar = (TimeSharingYAxis) mBarChart.getAxisLeft();
        mAxisLeftBar.setDrawGridLines(false);
        mAxisLeftBar.setDrawAxisLine(false);
        mAxisLeftBar.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisLeftBar.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisLeftBar.setDrawLabels(false);
        mAxisLeftBar.setLabelCount(2, true);
        mAxisLeftBar.setAxisMinimum(0);
        mAxisLeftBar.setSpaceTop(5);
        mAxisLeftBar.setValueLineInside(true);

        //副图右Y轴
        final YAxis axisRightBar = mBarChart.getAxisRight();
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(true);
        axisRightBar.setDrawAxisLine(false);
        axisRightBar.setLabelCount(3, true);
        axisRightBar.setDrawTopBottomGridLine(false);
        axisRightBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisRightBar.setGridLineWidth(0.7f);
        axisRightBar.enableGridDashedLine(CommonUtil.dip2px(mContext, 4f),
                CommonUtil.dip2px(mContext, 3f), 0);
    }

    /**
     * 设置分时数据
     */
    public void setDataToChart(TimeSharingDataManage dataManage) {
        setMaxCount(ChartType.ONE_DAY.getPointNum());
        setXLabels(dataManage.getTimeSharingXLabels());

        mAxisLeftLine.setAxisMinimum(dataManage.getMin());
        mAxisLeftLine.setAxisMaximum(dataManage.getMax());

        if (Float.isNaN(dataManage.getPercentMax())
                || Float.isNaN(dataManage.getPercentMin())
                || Float.isNaN(dataManage.getVolMaxTime())) {
            mAxisLeftBar.setAxisMaximum(0);
            mAxisRightLine.setAxisMinimum(0);
            mAxisRightLine.setAxisMaximum(0);
        } else {
            mAxisLeftBar.setAxisMaximum(dataManage.getVolMaxTime());
            mAxisRightLine.setAxisMinimum(dataManage.getPercentMin());
            mAxisRightLine.setAxisMaximum(dataManage.getPercentMax());
        }

        final ArrayList<Entry> lineCJEntries = new ArrayList<>();
        final ArrayList<Entry> lineJJEntries = new ArrayList<>();
        final ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0, j = 0; i < dataManage.getDatas().size(); i++, j++) {
            final TimeSharingDataModel t = dataManage.getDatas().get(j);
            if (t == null) {
                lineCJEntries.add(new Entry(i, Float.NaN));
                lineJJEntries.add(new Entry(i, Float.NaN));
                barEntries.add(new BarEntry(i, Float.NaN, 0f));
                continue;
            }
            lineCJEntries.add(new Entry(i, (float) dataManage.getDatas().get(i).getNowPrice()));
            lineJJEntries.add(new Entry(i, (float) dataManage.getDatas().get(i).getAveragePrice()));
            barEntries.add(new BarEntry(i, dataManage.getDatas().get(i).getVolume()));
        }
        final ArrayList<ILineDataSet> lineDataSets = new ArrayList<>();
        lineDataSets.add(setLine(TYPE_LINE_CJ, lineCJEntries));
        if (!dataManage.isBSChart()) {
            lineDataSets.add(setLine(TYPE_LINE_JJ, lineJJEntries));
        }
        if (dataManage.isBSChart()) {
            final int[] bCircles = {15, 15, 118};
            final int[] sCircles = {65, 119};
            final float[] bValues = {77, 88};
            final float[] sValues = {110, 150};

            //            lineDataSets.add(setBSLine(lineCJEntries, bCircles, bValues, sCircles, sValues));
            lineDataSets.add(setBSLine(lineCJEntries, bCircles, bValues, null, null));
            lineDataSets.add(setBSLine(lineCJEntries, null, null, sCircles, sValues));
        }
        final LineData lineData = new LineData(lineDataSets);
        mLineChart.setData(lineData);

        final BarDataSet barDataSet = new BarDataSet(barEntries, "成交量");
        barDataSet.setPriceData(lineCJEntries);
        barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        barDataSet.setHighlightEnabled(false);
        barDataSet.setDrawValues(false);
        barDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        barDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        barDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        barDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setNeutralPaintStyle(Paint.Style.FILL);
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
        mLineChart.moveViewToX(dataManage.getDatas().size() - 1);
        mBarChart.moveViewToX(dataManage.getDatas().size() - 1);
    }

    private LineDataSet setLine(int type, ArrayList<Entry> entries) {
        final LineDataSet lineDataSet = new LineDataSet(entries, "ma" + type);
        lineDataSet.setDrawValues(false);
        lineDataSet.setLineWidth(0.7f);
        lineDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        lineDataSet.setHighlightEnabled(false);
        lineDataSet.setDrawCircles(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setTimeDayType(1);//设置分时图类型
        lineDataSet.setColor(ContextCompat.getColor(mContext, R.color.minute_blue));

        if (type == TYPE_LINE_CJ) {
            lineDataSet.setDrawFilled(true);
            lineDataSet.setFillColor(ContextCompat.getColor(mContext, R.color.fill_Color));
        } else if (type == TYPE_LINE_JJ) {
            lineDataSet.setColor(ContextCompat.getColor(mContext, R.color.minute_yellow));
        }
        return lineDataSet;
    }

    private LineDataSet setBSLine(ArrayList<Entry> entries,
                                  int[] bCircles, float[] bValues,
                                  int[] sCircles, float[] sValues) {
        final LineDataSet lineDataSet = new LineDataSet(entries, "bsLine");
        lineDataSet.setColor(Color.TRANSPARENT);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setCircleHoleRadius(2f);
        lineDataSet.setCircleColors(Color.RED, Color.BLUE);

        final List<Integer> textColors = new ArrayList<>();
        textColors.add(Color.RED);
        textColors.add(Color.BLUE);
        lineDataSet.setValueTextColors(textColors);
        lineDataSet.setValueTextSize(16f);
        lineDataSet.setBSOption(true, bCircles, bValues, sCircles, sValues);
        return lineDataSet;
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
