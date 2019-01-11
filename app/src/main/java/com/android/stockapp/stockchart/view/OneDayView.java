package com.android.stockapp.stockchart.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.android.stockapp.R;
import com.android.stockapp.stockchart.BarBottomMarkerView;
import com.android.stockapp.stockchart.ColorContentYAxisRenderer;
import com.android.stockapp.stockchart.CoupleChartGestureListener;
import com.android.stockapp.stockchart.LeftMarkerView;
import com.android.stockapp.stockchart.TimeBarChart;
import com.android.stockapp.stockchart.TimeLineChart;
import com.android.stockapp.stockchart.TimeRightMarkerView;
import com.android.stockapp.stockchart.TimeXAxis;
import com.android.stockapp.stockchart.data.TimeDataManage;
import com.android.stockapp.stockchart.enums.ChartType;
import com.android.stockapp.stockchart.event.BaseEvent;
import com.android.stockapp.stockchart.model.CirclePositionTime;
import com.android.stockapp.stockchart.model.TimeDataModel;
import com.android.stockapp.stockchart.utils.CommonUtil;
import com.android.stockapp.stockchart.utils.VolFormatter;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.NumberUtils;
import com.github.mikephil.charting.utils.Transformer;
import com.github.mikephil.charting.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.text.DecimalFormat;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * 当日分时图view
 */
public class OneDayView extends BaseView {
    private Context mContext;
    TimeLineChart mLineChart;
    TimeBarChart mBarChart;
    FrameLayout mCirCleView;

    private LineDataSet mD1, mD2;
    private BarDataSet mBarDataSet;

    TimeXAxis mXAxisLine;
    YAxis mAxisRightLine;
    YAxis mAxisLeftLine;

    TimeXAxis mXAxisBar;
    YAxis mAxisLeftBar;
    YAxis mAxisRightBar;

    private int maxCount = ChartType.HK_ONE_DAY.getPointNum();//最大可见数量，即分时一天最大数据点数
    private SparseArray<String> xLabels = new SparseArray<>();//X轴刻度label
    private TimeDataManage mData;
    private int[] colorArray;

    public OneDayView(Context context) {
        this(context, null);
    }

    public OneDayView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        LayoutInflater.from(context).inflate(R.layout.view_time, this);
        mLineChart = findViewById(R.id.line_chart);
        mBarChart = findViewById(R.id.bar_chart);
        mCirCleView = findViewById(R.id.circle_frame_time);

        EventBus.getDefault().register(this);

        colorArray = new int[]{
                ContextCompat.getColor(mContext, R.color.up_color),
                ContextCompat.getColor(mContext, R.color.equal_color),
                ContextCompat.getColor(mContext, R.color.down_color)
        };

        playHeartbeatAnimation(mCirCleView.findViewById(R.id.anim_view));
    }

    /**
     * 初始化图表属性
     */
    public void initChart(boolean landscape) {
        this.landscape = landscape;
        //主图
        mLineChart.setScaleEnabled(false);
        mLineChart.setDrawBorders(true);
        mLineChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mLineChart.setBorderWidth(0.7f);
        mLineChart.setNoDataText(getResources().getString(R.string.loading));
        Legend lineChartLegend = mLineChart.getLegend();
        lineChartLegend.setEnabled(false);
        mLineChart.setDescription(null);
        //副图
        mBarChart.setScaleEnabled(false);
        mBarChart.setDrawBorders(true);
        mBarChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mBarChart.setBorderWidth(0.7f);
        mBarChart.setNoDataText(getResources().getString(R.string.loading));
        Legend barChartLegend = mBarChart.getLegend();
        barChartLegend.setEnabled(false);
        mBarChart.setDescription(null);

        //主图X轴
        mXAxisLine = (TimeXAxis) mLineChart.getXAxis();
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
        mAxisLeftLine.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisLeftLine.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisLeftLine.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return NumberUtils.keepPrecisionR(value, precision);
            }
        });

        //主图右Y轴
        mAxisRightLine = mLineChart.getAxisRight();
        mAxisRightLine.setLabelCount(5, true);
        mAxisRightLine.setDrawTopBottomGridLine(false);
        mAxisRightLine.setDrawGridLines(true);
        mAxisRightLine.setGridLineWidth(0.7f);
        mAxisRightLine.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);
        mAxisRightLine.setDrawAxisLine(false);
        mAxisRightLine.setValueLineInside(true);
        mAxisRightLine.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
        mAxisRightLine.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        mAxisRightLine.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisRightLine.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                DecimalFormat mFormat = new DecimalFormat("#0.00%");
                return mFormat.format(value);
            }
        });

        //副图X轴
        mXAxisBar = (TimeXAxis) mBarChart.getXAxis();
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
        mAxisLeftBar.setPosition(landscape ? YAxis.YAxisLabelPosition.OUTSIDE_CHART : YAxis.YAxisLabelPosition.INSIDE_CHART);
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
        mAxisRightBar.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);

        //手势联动监听
        gestureListenerLine = new CoupleChartGestureListener(mLineChart, new Chart[]{mBarChart});
        gestureListenerBar = new CoupleChartGestureListener(mBarChart, new Chart[]{mLineChart});
        mLineChart.setOnChartGestureListener(gestureListenerLine);
        mBarChart.setOnChartGestureListener(gestureListenerBar);

        mLineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mLineChart.highlightValue(h);
                mBarChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData, e.getXIndex(), true);
                }
            }

            @Override
            public void onNothingSelected() {
                mBarChart.highlightValues(null);
                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData, 0, false);
                }
            }
        });
        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mBarChart.highlightValue(h);
                mLineChart.highlightValue(new Highlight(h.getX(), h.getDataSetIndex(), -1));
                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData, e.getXIndex(), true);
                }
            }

            @Override
            public void onNothingSelected() {
                mLineChart.highlightValues(null);
                if (mHighlightValueSelectedListener != null) {
                    mHighlightValueSelectedListener.onDayHighlightValueListener(mData, 0, false);
                }
            }
        });
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
    public void setDataToChart(TimeDataManage mData) {
        this.mData = mData;
        if (mData.getDatas().size() == 0) {
            mCirCleView.setVisibility(View.GONE);
            mLineChart.setNoDataText(getResources().getString(R.string.no_data));
            mBarChart.setNoDataText(getResources().getString(R.string.no_data));
        } else {
            mCirCleView.setVisibility(landscape ? View.VISIBLE : View.GONE);
        }

        if (mData.getAssetId().endsWith(".HK")) {
            setPrecision(mData.getAssetId().contains("IDX") ? 2 : 3);
            setMaxCount(ChartType.HK_ONE_DAY.getPointNum());
        } else if (mData.getAssetId().endsWith(".US")) {
            if (Math.abs(mData.getMax()) < 1) {
                setPrecision(4);
            } else {
                setPrecision(2);
            }
            setMaxCount(ChartType.US_ONE_DAY.getPointNum());
        } else {
            setPrecision(2);
            setMaxCount(ChartType.ONE_DAY.getPointNum());
        }
        setXLabels(mData.getOneDayXLabels(landscape));
        setShowLabels(true);
        setMarkerView(mData);
        setBottomMarkerView(mData);

        mAxisLeftLine.setAxisMinimum(mData.getMin());
        mAxisLeftLine.setAxisMaximum(mData.getMax());

        //Y轴label渲染颜色
        Transformer leftYTransformer = mLineChart.getRendererLeftYAxis().getTransformer();
        ColorContentYAxisRenderer leftColorContentYAxisRenderer = new ColorContentYAxisRenderer(mLineChart.getViewPortHandler(), mAxisLeftLine, leftYTransformer);
        leftColorContentYAxisRenderer.setLabelColor(colorArray);
        leftColorContentYAxisRenderer.setClosePrice(mData.getPreClose());
        leftColorContentYAxisRenderer.setLandscape(landscape);
        mLineChart.setRendererLeftYAxis(leftColorContentYAxisRenderer);

        //Y轴label渲染颜色
        Transformer rightYTransformer = mLineChart.getRendererRightYAxis().getTransformer();
        ColorContentYAxisRenderer rightColorContentYAxisRenderer = new ColorContentYAxisRenderer(mLineChart.getViewPortHandler(), mAxisRightLine, rightYTransformer);
        rightColorContentYAxisRenderer.setLabelColor(colorArray);
        rightColorContentYAxisRenderer.setClosePrice(mData.getPreClose());
        rightColorContentYAxisRenderer.setLandscape(landscape);
        mLineChart.setRendererRightYAxis(rightColorContentYAxisRenderer);

        if (Float.isNaN(mData.getPercentMax()) || Float.isNaN(mData.getPercentMin()) || Float.isNaN(mData.getVolMaxTime())) {
            mAxisLeftBar.setAxisMaximum(0);
            mAxisRightLine.setAxisMinimum(-0.01f);
            mAxisRightLine.setAxisMaximum(0.01f);
        } else {
            mAxisLeftBar.setAxisMaximum(mData.getVolMaxTime());
            mAxisRightLine.setAxisMinimum(mData.getPercentMin());
            mAxisRightLine.setAxisMaximum(mData.getPercentMax());
        }

        mAxisLeftBar.setValueFormatter(new VolFormatter(mContext, mData.getAssetId()));

        ArrayList<Entry> lineCJEntries = new ArrayList<>();
        ArrayList<Entry> lineJJEntries = new ArrayList<>();
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        for (int i = 0, j = 0; i < mData.getDatas().size(); i++, j++) {
            TimeDataModel t = mData.getDatas().get(j);
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
        mD1 = new LineDataSet(lineCJEntries, "分时线");
        mD2 = new LineDataSet(lineJJEntries, "均价");
        mD1.setDrawCircleDashMarker(landscape);
        mD2.setDrawCircleDashMarker(false);
        mD1.setDrawValues(false);
        mD2.setDrawValues(false);
        mD1.setLineWidth(0.7f);
        mD2.setLineWidth(0.7f);
        mD1.setColor(ContextCompat.getColor(mContext, R.color.minute_blue));
        mD2.setColor(ContextCompat.getColor(mContext, R.color.minute_yellow));
        mD1.setDrawFilled(true);
        mD1.setFillColor(ContextCompat.getColor(mContext, R.color.fill_Color));
        mD1.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        mD1.setHighlightEnabled(landscape);
        mD2.setHighlightEnabled(false);
        mD1.setDrawCircles(false);
        mD2.setDrawCircles(false);
        mD1.setAxisDependency(YAxis.AxisDependency.LEFT);
        mD1.setPrecision(precision);
        mD1.setTimeDayType(1);//设置分时图类型
        mD2.setTimeDayType(1);
        ArrayList<ILineDataSet> sets = new ArrayList<>();
        sets.add(mD1);
        sets.add(mD2);
        LineData cd = new LineData(sets);
        mLineChart.setData(cd);

        mBarDataSet = new BarDataSet(barEntries, "成交量");
        mBarDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        mBarDataSet.setHighlightEnabled(landscape);
        mBarDataSet.setDrawValues(false);
        mBarDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        mBarDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        mBarDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        mBarDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        mBarDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        BarData barData = new BarData(mBarDataSet);
        mBarChart.setData(barData);

        //请注意，修改视口的所有方法需要在为Chart设置数据之后调用。
        //设置当前视图四周的偏移量。 设置这个，将阻止图表自动计算它的偏移量。使用 resetViewPortOffsets()撤消此设置。
        if (landscape) {
            float volwidth = Utils.calcTextWidthForVol(mPaint, mData.getVolMaxTime());
            float pricewidth = Utils.calcTextWidth(mPaint, NumberUtils.keepPrecision(Float.isNaN(mData.getMax()) ? "0" : mData.getMax() + "", precision) + "#");
            float left = CommonUtil.dip2px(mContext, pricewidth > volwidth ? pricewidth : volwidth);
            float right = CommonUtil.dip2px(mContext, Utils.calcTextWidth(mPaint, "-10.00%"));
            mLineChart.setViewPortOffsets(left, CommonUtil.dip2px(mContext, 5), right, CommonUtil.dip2px(mContext, 15));
            mBarChart.setViewPortOffsets(left, 0, right, CommonUtil.dip2px(mContext, 15));
        } else {
            mLineChart.setViewPortOffsets(CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 15));
            mBarChart.setViewPortOffsets(CommonUtil.dip2px(mContext, 5), 0, CommonUtil.dip2px(mContext, 5), CommonUtil.dip2px(mContext, 5));
        }

        //下面方法需在填充数据后调用
        mXAxisLine.setXLabels(getXLabels());
        mXAxisLine.setLabelCount(getXLabels().size(), false);
        mXAxisBar.setXLabels(getXLabels());
        mXAxisBar.setLabelCount(getXLabels().size(), false);
        mLineChart.setVisibleXRange(maxCount, maxCount);
        mBarChart.setVisibleXRange(maxCount, maxCount);
        //moveViewTo(...) 方法会自动调用 invalidate()
        mLineChart.moveViewToX(mData.getDatas().size() - 1);
        mBarChart.moveViewToX(mData.getDatas().size() - 1);

    }

    public void dynamicsAddOne(TimeDataModel timeDatamodel, int length) {
        int index = length - 1;
        LineData lineData = mLineChart.getData();
        ILineDataSet d1 = lineData.getDataSetByIndex(0);
        d1.addEntry(new Entry(index, index, (float) timeDatamodel.getNowPrice()));
        ILineDataSet d2 = lineData.getDataSetByIndex(1);
        d2.addEntry(new Entry(index, index, (float) timeDatamodel.getAveragePrice()));

        BarData barData = mBarChart.getData();
        IBarDataSet barDataSet = barData.getDataSetByIndex(0);
        barDataSet.addEntry(new BarEntry(index, index, timeDatamodel.getVolume()));
        lineData.notifyDataChanged();
        mLineChart.notifyDataSetChanged();
        barData.notifyDataChanged();
        mBarChart.notifyDataSetChanged();
        mLineChart.setVisibleXRange(maxCount, maxCount);
        mBarChart.setVisibleXRange(maxCount, maxCount);
        //动态添加或移除数据后， 调用invalidate()刷新图表之前 必须调用 notifyDataSetChanged() .
        mLineChart.moveViewToX(index);
        mBarChart.moveViewToX(index);
    }

    public void dynamicsUpdateOne(TimeDataModel timeDatamodel, int length) {
        int index = length - 1;
        LineData lineData = mLineChart.getData();
        ILineDataSet d1 = lineData.getDataSetByIndex(0);
        Entry e = d1.getEntryForIndex(index);
        d1.removeEntry(e);
        d1.addEntry(new Entry(index, index, (float) timeDatamodel.getNowPrice()));

        ILineDataSet d2 = lineData.getDataSetByIndex(1);
        Entry e2 = d2.getEntryForIndex(index);
        d2.removeEntry(e2);
        d2.addEntry(new Entry(index, index, (float) timeDatamodel.getAveragePrice()));

        BarData barData = mBarChart.getData();
        IBarDataSet barDataSet = barData.getDataSetByIndex(0);
        barDataSet.removeEntry(index);
        barDataSet.addEntry(new BarEntry(index, index, timeDatamodel.getVolume()));

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
        if (mCirCleView != null) {
            mCirCleView.setVisibility(View.GONE);
        }
    }

    private void setMarkerView(TimeDataManage mData) {
        LeftMarkerView leftMarkerView = new LeftMarkerView(mContext, R.layout.my_markerview, precision);
        TimeRightMarkerView rightMarkerView = new TimeRightMarkerView(mContext, R.layout.my_markerview);
        mLineChart.setMarker(leftMarkerView, rightMarkerView, mData);
    }

    private void setBottomMarkerView(TimeDataManage kDatas) {
        BarBottomMarkerView bottomMarkerView = new BarBottomMarkerView(mContext, R.layout.my_markerview);
        mBarChart.setMarker(bottomMarkerView, kDatas);
    }

    @Override
    public void onEventMainThread(BaseEvent event) {
        if (event.method == 1) {
            CirclePositionTime position = (CirclePositionTime) event.obj;
            mCirCleView.setX(position.cx - mCirCleView.getWidth() / 2);
            mCirCleView.setY(position.cy - mCirCleView.getHeight() / 2);
        }
    }

    public void setXLabels(SparseArray<String> xLabels) {
        this.xLabels = xLabels;
    }

    public SparseArray<String> getXLabels() {
        if (xLabels.size() == 0) {
            setMaxCount(ChartType.HK_ONE_DAY.getPointNum());
            xLabels.put(0, "09:30");
            xLabels.put(60, "10:30");
            xLabels.put(120, "11:30");
            xLabels.put(180, "13:30");
            xLabels.put(240, "14:30");
            xLabels.put(300, "15:30");
            xLabels.put(330, "16:00");
        }
        return xLabels;
    }

    public void setMaxCount(int maxCount) {
        this.maxCount = maxCount;
    }

    public void eventBusUnregister() {
        EventBus.getDefault().unregister(this);
    }
}
