package com.example.sample.stockchart.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.example.sample.R;
import com.example.sample.stockchart.data.KLineDataManage;
import com.example.sample.stockchart.listener.CoupleChartGestureListener;
import com.example.sample.stockchart.model.KLineDataModel;
import com.example.sample.stockchart.utils.CommonUtil;
import com.example.sample.stockchart.utils.VolFormatter;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.NumberUtils;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

/**
 * @author weixia
 * @date 2019/1/15.
 */
public class KLineChart extends LinearLayout {
    private Context mContext;
    private CombinedChart mCandleChart;
    private KLineCombinedChart mBarChart;

    private YAxis mAxisLeftBar;
    private KLineDataManage kLineData;
    public CoupleChartGestureListener gestureListenerBar;
    public CoupleChartGestureListener gestureListenerCandle;

    private static final int PRECISION = 3;//小数精度
    private static final float X_RANGE = 45;

    public KLineChart(Context context) {
        this(context, null);
    }

    public KLineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        mContext = context;

        View.inflate(context, R.layout.view_chart_k_line, this);
        mCandleChart = findViewById(R.id.view_chart_k_line_candle_chart);
        mBarChart = findViewById(R.id.view_chart_k_line_bar_chart);
    }

    /**
     * 初始化图表数据
     */
    public void initChart() {
        initChartLine();
        initChartBar();
        initChartEvent();

        //初始化图表框显示（需要添加一条数据才能显示图表框）
        final KLineDataManage manage = new KLineDataManage(mContext);
        manage.getDatas().add(new KLineDataModel());
        setDataToChart(manage);
    }

    private void initChartLine() {
        //蜡烛图
        mCandleChart.setDrawBorders(true);
        mCandleChart.setBorderWidth(0.7f);
        mCandleChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mCandleChart.setDragEnabled(true);
        mCandleChart.setScaleXEnabled(true);
        mCandleChart.setScaleYEnabled(false);
        mCandleChart.setHardwareAccelerationEnabled(true);
        mCandleChart.setDragDecelerationEnabled(false);
        mCandleChart.setDragDecelerationFrictionCoef(0.6f);//0.92持续滚动时的速度快慢，[0,1) 0代表立即停止。
        mCandleChart.setDoubleTapToZoomEnabled(false);
        mCandleChart.setNoDataText(getResources().getString(R.string.loading));
        mCandleChart.setDescription(null);
        mCandleChart.setAutoScaleMinMaxEnabled(true);
        //图例
        final Legend legend = mCandleChart.getLegend();
        legend.setEnabled(false);

        //蜡烛图X轴
        final XAxis xAxisK = mCandleChart.getXAxis();
        xAxisK.setDrawLabels(true);
        xAxisK.setLabelCount(4, true);
        xAxisK.setDrawAxisLine(false);
        xAxisK.setDrawGridLines(true);
        xAxisK.setGridLineWidth(0.7f);
        xAxisK.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        xAxisK.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisK.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisK.setAvoidFirstLastClipping(true);
        xAxisK.setDrawLimitLinesBehindData(true);

        //蜡烛图左Y轴
        final YAxis axisLeftK = mCandleChart.getAxisLeft();
        axisLeftK.setDrawGridLines(true);
        axisLeftK.setDrawAxisLine(false);
        axisLeftK.setDrawLabels(true);
        axisLeftK.setLabelCount(5, true);
        axisLeftK.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);
        axisLeftK.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        axisLeftK.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisLeftK.setGridLineWidth(0.7f);
        axisLeftK.setValueLineInside(true);
        axisLeftK.setDrawTopBottomGridLine(false);
        axisLeftK.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        axisLeftK.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return NumberUtils.keepPrecisionR(value, PRECISION);
            }
        });

        //蜡烛图右Y轴
        final YAxis axisRightK = mCandleChart.getAxisRight();
        axisRightK.setDrawLabels(false);
        axisRightK.setDrawGridLines(false);
        axisRightK.setDrawAxisLine(false);
        axisRightK.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
    }

    private void initChartBar() {
        //副图
        mBarChart.setDrawBorders(true);
        mBarChart.setBorderWidth(0.7f);
        mBarChart.setBorderColor(ContextCompat.getColor(mContext, R.color.border_color));
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleXEnabled(true);
        mBarChart.setScaleYEnabled(false);
        mBarChart.setHardwareAccelerationEnabled(true);
        mBarChart.setDragDecelerationEnabled(false);
        mBarChart.setDragDecelerationFrictionCoef(0.6f);
        mBarChart.setDoubleTapToZoomEnabled(false);
        mBarChart.setNoDataText(getResources().getString(R.string.loading));
        mBarChart.setDescription(null);
        mBarChart.setAutoScaleMinMaxEnabled(true);
        //图例
        final Legend legend = mBarChart.getLegend();
        legend.setEnabled(false);

        //副图X轴
        final XAxis xAxisBar = mBarChart.getXAxis();
        xAxisBar.setDrawLabels(false);
        xAxisBar.setLabelCount(4, true);
        xAxisBar.setDrawAxisLine(false);
        xAxisBar.setDrawGridLines(true);
        xAxisBar.setGridLineWidth(0.7f);
        xAxisBar.setTextColor(ContextCompat.getColor(mContext, R.color.label_text));
        xAxisBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        xAxisBar.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxisBar.setAvoidFirstLastClipping(true);
        xAxisBar.setDrawLimitLinesBehindData(true);

        //副图左Y轴
        mAxisLeftBar = mBarChart.getAxisLeft();
        mAxisLeftBar.setAxisMinimum(0);
        mAxisLeftBar.setDrawGridLines(false);
        mAxisLeftBar.setDrawAxisLine(false);
        mAxisLeftBar.setTextColor(ContextCompat.getColor(mContext, R.color.axis_text));
        mAxisLeftBar.setDrawLabels(false);
        mAxisLeftBar.setLabelCount(2, true);
        mAxisLeftBar.setValueLineInside(true);
        mAxisLeftBar.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        //副图右Y轴
        final YAxis axisRightBar = mBarChart.getAxisRight();
        axisRightBar.setDrawLabels(false);
        axisRightBar.setDrawGridLines(true);
        axisRightBar.setDrawAxisLine(false);
        axisRightBar.setLabelCount(3, true);
        axisRightBar.setDrawTopBottomGridLine(false);
        axisRightBar.setGridColor(ContextCompat.getColor(mContext, R.color.grid_color));
        axisRightBar.setGridLineWidth(0.7f);
        axisRightBar.enableGridDashedLine(CommonUtil.dip2px(mContext, 4), CommonUtil.dip2px(mContext, 3), 0);
    }

    private void initChartEvent() {
        //手势联动监听
        gestureListenerCandle = new CoupleChartGestureListener(mCandleChart, new Chart[]{mBarChart});
        gestureListenerBar = new CoupleChartGestureListener(mBarChart, new Chart[]{mCandleChart});
        mCandleChart.setOnChartGestureListener(gestureListenerCandle);
        mBarChart.setOnChartGestureListener(gestureListenerBar);

        mCandleChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mCandleChart.highlightValue(h);
                if (mBarChart.getData().getBarData().getDataSets().size() != 0) {
                    final Highlight highlight = new Highlight(h.getX(), h.getDataSetIndex(), h.getStackIndex());
                    highlight.setDataIndex(h.getDataIndex());
                    mBarChart.highlightValues(new Highlight[]{highlight});
                } else {
                    final Highlight highlight = new Highlight(h.getX(), 2, h.getStackIndex());
                    highlight.setDataIndex(0);
                    mBarChart.highlightValues(new Highlight[]{highlight});
                }
            }

            @Override
            public void onNothingSelected() {
                mBarChart.highlightValues(null);
            }
        });

        mBarChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                mBarChart.highlightValue(h);
                final Highlight highlight = new Highlight(h.getX(), 0, h.getStackIndex());
                highlight.setDataIndex(1);
                mCandleChart.highlightValues(new Highlight[]{highlight});
            }

            @Override
            public void onNothingSelected() {
                mCandleChart.highlightValues(null);
            }
        });
    }

    /**
     * 设置K线数据
     */
    public void setDataToChart(KLineDataManage data) {
        kLineData = data;
        if (data.getDatas().size() == 0) {
            return;
        }

        //蜡烛图数据
        final CandleDataSet candleDataSet = data.getCandleDataSet();
        candleDataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return NumberUtils.keepPrecisionR(value, PRECISION);
            }
        });
        final CombinedData candleChartData = new CombinedData();
        candleChartData.setData(new CandleData(candleDataSet));
        candleChartData.setData(new LineData(data.getLineDataMA()));
        mCandleChart.setData(candleChartData);

        //成交量数据
        final CombinedData barChartData = new CombinedData();
        barChartData.setData(new BarData(data.getVolumeDataSet()));
        barChartData.setData(new LineData());
        barChartData.setData(new CandleData());
        //重新请求数据时保持副图指标还是显示原来的指标
        if (chartType1 == 1) {
            mBarChart.setData(barChartData);
        }

        mCandleChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                final int index = (int) (value - data.getOffSet());
                if (index < 0 || index >= data.getXVals().size()) {
                    return "";
                } else {
                    return data.getXVals().get(index);
                }
            }
        });
        mBarChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                final int index = (int) (value - data.getOffSet());
                if (index < 0 || index >= data.getXVals().size()) {
                    return "";
                } else {
                    return data.getXVals().get(index);
                }
            }
        });

        //请注意，修改视口的所有方法需要在为Chart设置数据之后调用。
        //设置当前视图四周的偏移量。 设置这个，将阻止图表自动计算它的偏移量。使用 resetViewPortOffsets()撤消此设置。
        mCandleChart.setViewPortOffsets(
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 15));
        mBarChart.setViewPortOffsets(
                CommonUtil.dip2px(mContext, 5),
                0,
                CommonUtil.dip2px(mContext, 5),
                CommonUtil.dip2px(mContext, 5));

        final float offset = data.getOffSet();
        final float dataSize = data.getDatas().size();
        float candleMax = candleChartData.getXMax() + offset;
        float barMax = barChartData.getXMax() + offset;
        if (dataSize < X_RANGE) {
            candleMax = barMax = X_RANGE;
        }
        mCandleChart.getXAxis().setAxisMaximum(candleMax);
        mBarChart.getXAxis().setAxisMaximum(barMax);
        //设置显示X轴个数的上下限
        mCandleChart.setVisibleXRange(X_RANGE, X_RANGE);
        mBarChart.setVisibleXRange(X_RANGE, X_RANGE);

        if (dataSize > X_RANGE) {
            final float xValue = dataSize - 1;
            mCandleChart.moveViewToAnimated(xValue, 0, YAxis.AxisDependency.LEFT, 500);
            mBarChart.moveViewToAnimated(xValue, 0, YAxis.AxisDependency.LEFT, 500);
        } else {
            mCandleChart.invalidate();
            mBarChart.animateY(500);
        }

        mCandleChart.notifyDataSetChanged();
        mBarChart.notifyDataSetChanged();
    }

    protected int chartType1 = 1;
    protected int chartTypes1 = 5;

    public void doBarChartSwitch(int chartType) {
        chartType1 = chartType;
        if (chartType1 > chartTypes1) {
            chartType1 = 1;
        }
        switch (chartType1) {
            case 1:
                setVolumeToChart();
                break;
            case 2:
                setMACDToChart();
                break;
            case 3:
                setKDJToChart();
                break;
            case 4:
                setBOLLToChart();
                break;
            case 5:
                setRSIToChart();
                break;
            default:
                break;
        }
    }

    /**
     * 副图指标成交量
     */
    public void setVolumeToChart() {
        if (mBarChart != null) {
            if (mBarChart.getBarData() != null) {
                mBarChart.getBarData().clearValues();
            }
            if (mBarChart.getLineData() != null) {
                mBarChart.getLineData().clearValues();
            }
            if (mBarChart.getCandleData() != null) {
                mBarChart.getCandleData().clearValues();
            }
            mAxisLeftBar.resetAxisMaximum();
            mAxisLeftBar.resetAxisMinimum();
            mAxisLeftBar.setAxisMinimum(0);
            mAxisLeftBar.setValueFormatter(new VolFormatter(mContext));

            final CombinedData combinedData = mBarChart.getData();
            combinedData.setData(new BarData(kLineData.getVolumeDataSet()));
            combinedData.setData(new LineData());
            mBarChart.notifyDataSetChanged();
            mBarChart.animateY(1000);
        }
    }

    /**
     * 副图指标MACD
     */
    public void setMACDToChart() {
        if (mBarChart != null) {
            if (mBarChart.getBarData() != null) {
                mBarChart.getBarData().clearValues();
            }
            if (mBarChart.getLineData() != null) {
                mBarChart.getLineData().clearValues();
            }
            if (mBarChart.getCandleData() != null) {
                mBarChart.getCandleData().clearValues();
            }

            mAxisLeftBar.resetAxisMaximum();
            mAxisLeftBar.resetAxisMinimum();
            mAxisLeftBar.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return NumberUtils.keepPrecision(value, PRECISION);
                }
            });

            final CombinedData combinedData = mBarChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataMACD()));
            combinedData.setData(new BarData(kLineData.getBarDataMACD()));
            mBarChart.notifyDataSetChanged();
            mBarChart.invalidate();
        }
    }

    /**
     * 副图指标KDJ
     */
    public void setKDJToChart() {
        if (mBarChart != null) {
            if (mBarChart.getBarData() != null) {
                mBarChart.getBarData().clearValues();
            }
            if (mBarChart.getLineData() != null) {
                mBarChart.getLineData().clearValues();
            }
            if (mBarChart.getCandleData() != null) {
                mBarChart.getCandleData().clearValues();
            }

            mAxisLeftBar.resetAxisMaximum();
            mAxisLeftBar.resetAxisMinimum();
            mAxisLeftBar.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return NumberUtils.keepPrecision(value, PRECISION);
                }
            });

            final CombinedData combinedData = mBarChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataKDJ()));
            mBarChart.notifyDataSetChanged();
            mBarChart.invalidate();
        }
    }

    /**
     * 副图指标BOLL
     */
    public void setBOLLToChart() {
        if (mBarChart != null) {
            if (mBarChart.getBarData() != null) {
                mBarChart.getBarData().clearValues();
            }
            if (mBarChart.getLineData() != null) {
                mBarChart.getLineData().clearValues();
            }
            if (mBarChart.getCandleData() != null) {
                mBarChart.getCandleData().clearValues();
            }

            mAxisLeftBar.resetAxisMaximum();
            mAxisLeftBar.resetAxisMinimum();
            mAxisLeftBar.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return NumberUtils.keepPrecision(value, PRECISION);
                }
            });

            final CombinedData combinedData = mBarChart.getData();
            combinedData.setData(new CandleData(kLineData.getBollCandleDataSet()));
            combinedData.setData(new LineData(kLineData.getLineDataBOLL()));
            mBarChart.notifyDataSetChanged();
            mBarChart.invalidate();
        }
    }

    /**
     * 副图指标RSI
     */
    public void setRSIToChart() {
        if (mBarChart != null) {
            if (mBarChart.getBarData() != null) {
                mBarChart.getBarData().clearValues();
            }
            if (mBarChart.getLineData() != null) {
                mBarChart.getLineData().clearValues();
            }
            if (mBarChart.getCandleData() != null) {
                mBarChart.getCandleData().clearValues();
            }

            mAxisLeftBar.resetAxisMaximum();
            mAxisLeftBar.resetAxisMinimum();
            mAxisLeftBar.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    return NumberUtils.keepPrecision(value, PRECISION);
                }
            });

            final CombinedData combinedData = mBarChart.getData();
            combinedData.setData(new LineData(kLineData.getLineDataRSI()));
            mBarChart.notifyDataSetChanged();
            mBarChart.invalidate();
        }
    }
}
