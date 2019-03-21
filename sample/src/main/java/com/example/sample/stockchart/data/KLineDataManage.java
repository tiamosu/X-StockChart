package com.example.sample.stockchart.data;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;

import com.example.sample.R;
import com.example.sample.stockchart.model.BOLLEntity;
import com.example.sample.stockchart.model.KDJEntity;
import com.example.sample.stockchart.model.KLineDataModel;
import com.example.sample.stockchart.model.MACDEntity;
import com.example.sample.stockchart.model.RSIEntity;
import com.example.sample.stockchart.utils.DataTimeUtil;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.NumberUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.core.content.ContextCompat;

/**
 * @author weixia
 * @date 2019/1/15.
 */
public class KLineDataManage {
    private final Context mContext;
    private final ArrayList<KLineDataModel> mAllDatas = new ArrayList<>();
    private float offSet = 0.99f;//K线图最右边偏移量

    //MA参数
    private final static int N1 = 5;
    private final static int N2 = 10;
    private final static int N3 = 20;
    //BOLL参数
    private final static int BOLLN = 26;
    //MACD参数
    private final static int SHORT = 12;
    private final static int LONG = 26;
    private final static int M = 9;
    //KDJ参数
    private final static int KDJN = 9;
    private final static int KDJM1 = 3;
    private final static int KDJM2 = 3;
    //RSI参数
    private final static int RSIN1 = 6;
    private final static int RSIN2 = 12;
    private final static int RSIN3 = 24;

    private final ArrayList<KLineDataModel> mDatas = new ArrayList<>();
    //X轴数据
    private ArrayList<String> mXVal = new ArrayList<>();

    private CandleDataSet mCandleDataSet;//蜡烛图集合
    private BarDataSet mVolumeDataSet;//成交量集合
    private BarDataSet barDataMACD;//MACD集合
    private CandleDataSet mBollCandleDataSet;//BOLL蜡烛图集合

    private List<ILineDataSet> mLineDataMA = new ArrayList<>();
    private List<ILineDataSet> lineDataMACD = new ArrayList<>();
    private List<ILineDataSet> lineDataKDJ = new ArrayList<>();
    private List<ILineDataSet> lineDataBOLL = new ArrayList<>();
    private List<ILineDataSet> lineDataRSI = new ArrayList<>();

    public KLineDataManage(Context context) {
        mContext = context;
    }

    /**
     * 解析K线数据
     */
    public void parseKlineData(JSONObject object) {
        if (object == null) {
            return;
        }
        mAllDatas.clear();
        final JSONArray data = object.optJSONArray("data");
        if (data != null) {
            for (int i = 0; i < data.length(); i++) {
                final KLineDataModel dataModel = new KLineDataModel();
                dataModel.setDateMills(data.optJSONArray(i).optLong(0, 0L));
                dataModel.setOpen(data.optJSONArray(i).optDouble(1));
                dataModel.setHigh(data.optJSONArray(i).optDouble(2));
                dataModel.setLow(data.optJSONArray(i).optDouble(3));
                dataModel.setClose(data.optJSONArray(i).optDouble(4));
                dataModel.setVolume(NumberUtils.stringNoE10ForVol(data.optJSONArray(i).optDouble(5, 0)));
                dataModel.setTotal(NumberUtils.stringNoE10ForVol(data.optJSONArray(i).optDouble(6, 0)));
                dataModel.setMa5(data.optJSONArray(i).optDouble(7));
                dataModel.setMa10(data.optJSONArray(i).optDouble(8));
                dataModel.setMa20(data.optJSONArray(i).optDouble(9));
                dataModel.setMa30(data.optJSONArray(i).optDouble(10));
                dataModel.setMa60(data.optJSONArray(i).optDouble(11));
                dataModel.setPreClose(data.optJSONArray(i).optDouble(12));
                mAllDatas.add(dataModel);
            }

            addData();
        }
    }

    private int mLoadDataNum;

    public void addData() {
        //已加载全部数据无需继续加载
        if (mDatas.size() == mAllDatas.size()) {
            return;
        }
        mDatas.clear();
        mXVal.clear();
        mLineDataMA.clear();

        mLoadDataNum++;
        final ArrayList<CandleEntry> candleEntries = new ArrayList<>();
        final ArrayList<BarEntry> barEntries = new ArrayList<>();
        final ArrayList<Entry> line5Entries = new ArrayList<>();
        final ArrayList<Entry> line10Entries = new ArrayList<>();
        final ArrayList<Entry> line20Entries = new ArrayList<>();

        final int allDataSize = mAllDatas.size();
        final int size = (allDataSize - 100 * mLoadDataNum) >= 0 ? 100 * mLoadDataNum : allDataSize;
        for (int i = 0; i < size; i++) {
            final KLineDataModel dataModel = mAllDatas.get(i);
            mDatas.add(dataModel);

            mXVal.add(DataTimeUtil.secToDate(dataModel.getDateMills()));
            candleEntries.add(new CandleEntry(i, i + offSet, (float) dataModel.getHigh(),
                    (float) dataModel.getLow(), (float) dataModel.getOpen(), (float) dataModel.getClose()));

            float color = dataModel.getOpen() > dataModel.getClose() ? 0f : 1f;
            barEntries.add(new BarEntry(i, i + offSet, (float) dataModel.getVolume(), color));

            line5Entries.add(new Entry(i, i + offSet, (float) dataModel.getMa5()));
            line10Entries.add(new Entry(i, i + offSet, (float) dataModel.getMa10()));
            line20Entries.add(new Entry(i, i + offSet, (float) dataModel.getMa20()));
        }

        mCandleDataSet = setACandle(candleEntries);
        mBollCandleDataSet = setBOLLCandle(candleEntries);
        mVolumeDataSet = setABar(barEntries, "成交量");
        mLineDataMA.add(setALine(ColorType.blue, line5Entries, false));
        mLineDataMA.add(setALine(ColorType.yellow, line10Entries, false));
        mLineDataMA.add(setALine(ColorType.purple, line20Entries, false));
    }

    /**
     * 初始化自己计算MACD
     */
    public void initMACD() {
        final MACDEntity macdEntity = new MACDEntity(getDatas(), SHORT, LONG, M);
        final ArrayList<BarEntry> macdData = new ArrayList<>();
        final ArrayList<Entry> deaData = new ArrayList<>();
        final ArrayList<Entry> difData = new ArrayList<>();

        final int size = macdEntity.getMACD().size();
        for (int i = 0; i < size; i++) {
            macdData.add(new BarEntry(i, i + offSet, macdEntity.getMACD().get(i), macdEntity.getMACD().get(i)));
            deaData.add(new Entry(i, i + offSet, macdEntity.getDEA().get(i)));
            difData.add(new Entry(i, i + offSet, macdEntity.getDIF().get(i)));
        }
        barDataMACD = setABar(macdData);
        lineDataMACD.add(setALine(ColorType.blue, deaData));
        lineDataMACD.add(setALine(ColorType.yellow, difData));
    }

    /**
     * 初始化自己计算KDJ
     */
    public void initKDJ() {
        final KDJEntity kdjEntity = new KDJEntity(getDatas(), KDJN, KDJM1, KDJM2);
        final ArrayList<Entry> kData = new ArrayList<>();
        final ArrayList<Entry> dData = new ArrayList<>();
        final ArrayList<Entry> jData = new ArrayList<>();

        final int size = kdjEntity.getD().size();
        for (int i = 0; i < size; i++) {
            kData.add(new Entry(i, i + offSet, kdjEntity.getK().get(i)));
            dData.add(new Entry(i, i + offSet, kdjEntity.getD().get(i)));
            jData.add(new Entry(i, i + offSet, kdjEntity.getJ().get(i)));
        }
        lineDataKDJ.add(setALine(ColorType.blue, kData, "KDJ" + N1, false));
        lineDataKDJ.add(setALine(ColorType.yellow, dData, "KDJ" + N2, false));
        lineDataKDJ.add(setALine(ColorType.purple, jData, "KDJ" + N3, true));
    }

    /**
     * 初始化自己计算BOLL
     */
    public void initBOLL() {
        final BOLLEntity bollEntity = new BOLLEntity(getDatas(), BOLLN);
        final ArrayList<Entry> bollDataUP = new ArrayList<>();
        final ArrayList<Entry> bollDataMB = new ArrayList<>();
        final ArrayList<Entry> bollDataDN = new ArrayList<>();

        final int size = bollEntity.getUPs().size();
        for (int i = 0; i < size; i++) {
            bollDataUP.add(new Entry(i, i + offSet, bollEntity.getUPs().get(i)));
            bollDataMB.add(new Entry(i, i + offSet, bollEntity.getMBs().get(i)));
            bollDataDN.add(new Entry(i, i + offSet, bollEntity.getDNs().get(i)));
        }
        lineDataBOLL.add(setALine(ColorType.blue, bollDataUP, false));
        lineDataBOLL.add(setALine(ColorType.yellow, bollDataMB, false));
        lineDataBOLL.add(setALine(ColorType.purple, bollDataDN, false));
    }

    /**
     * 初始化自己计算RSI
     */
    public void initRSI() {
        final RSIEntity rsiEntity6 = new RSIEntity(getDatas(), RSIN1);
        final RSIEntity rsiEntity12 = new RSIEntity(getDatas(), RSIN2);
        final RSIEntity rsiEntity24 = new RSIEntity(getDatas(), RSIN3);

        final ArrayList<Entry> rsiData6 = new ArrayList<>();
        final ArrayList<Entry> rsiData12 = new ArrayList<>();
        final ArrayList<Entry> rsiData24 = new ArrayList<>();

        final int size = rsiEntity6.getRSIs().size();
        for (int i = 0; i < size; i++) {
            rsiData6.add(new Entry(i, i + offSet, rsiEntity6.getRSIs().get(i)));
            rsiData12.add(new Entry(i, i + offSet, rsiEntity12.getRSIs().get(i)));
            rsiData24.add(new Entry(i, i + offSet, rsiEntity24.getRSIs().get(i)));
        }
        lineDataRSI.add(setALine(ColorType.blue, rsiData6, "RSI" + RSIN1, true));
        lineDataRSI.add(setALine(ColorType.yellow, rsiData12, "RSI" + RSIN2, false));
        lineDataRSI.add(setALine(ColorType.purple, rsiData24, "RSI" + RSIN3, false));
    }

    private CandleDataSet setACandle(ArrayList<CandleEntry> candleEntries) {
        final CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(false);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        candleDataSet.setNeutralPaintStyle(Paint.Style.FILL);
        candleDataSet.setShadowColorSameAsCandle(true);
        candleDataSet.setValueTextSize(10);
        candleDataSet.setDrawValues(true);
        candleDataSet.setCandleDataTextColor(Color.RED);
        return candleDataSet;
    }

    private CandleDataSet setBOLLCandle(ArrayList<CandleEntry> candleEntries) {
        final CandleDataSet candleDataSet = new CandleDataSet(candleEntries, "KLine");
        candleDataSet.setDrawHorizontalHighlightIndicator(false);
        candleDataSet.setHighlightEnabled(false);
        candleDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        candleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        candleDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        candleDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        candleDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        candleDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        candleDataSet.setNeutralPaintStyle(Paint.Style.FILL);
        candleDataSet.setDrawValues(false);
        candleDataSet.setShowCandleBar(false);
        return candleDataSet;
    }

    private LineDataSet setALine(ColorType ma, ArrayList<Entry> lineEntries) {
        final String label = "ma" + ma;
        return setALine(ma, lineEntries, label);
    }

    private LineDataSet setALine(ColorType ma, ArrayList<Entry> lineEntries, boolean highlightEnable) {
        final String label = "ma" + ma;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    private LineDataSet setALine(ColorType ma, ArrayList<Entry> lineEntries, String label) {
        final boolean highlightEnable = false;
        return setALine(ma, lineEntries, label, highlightEnable);
    }

    private LineDataSet setALine(ColorType colorType, ArrayList<Entry> lineEntries, String label, boolean highlightEnable) {
        final LineDataSet lineDataSetMa = new LineDataSet(lineEntries, label);
        lineDataSetMa.setDrawHorizontalHighlightIndicator(false);
        lineDataSetMa.setHighlightEnabled(highlightEnable);
        lineDataSetMa.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        lineDataSetMa.setDrawValues(false);
        if (colorType == ColorType.blue) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma5));
        } else if (colorType == ColorType.yellow) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma10));
        } else if (colorType == ColorType.purple) {
            lineDataSetMa.setColor(ContextCompat.getColor(mContext, R.color.ma20));
        }
        lineDataSetMa.setLineWidth(0.6f);
        lineDataSetMa.setDrawCircles(false);
        lineDataSetMa.setAxisDependency(YAxis.AxisDependency.LEFT);
        return lineDataSetMa;
    }

    private BarDataSet setABar(ArrayList<BarEntry> barEntries) {
        final String label = "BarDataSet";
        return setABar(barEntries, label);
    }

    private BarDataSet setABar(ArrayList<BarEntry> barEntries, String label) {
        final BarDataSet barDataSet = new BarDataSet(barEntries, label);
        barDataSet.setHighlightEnabled(false);
        barDataSet.setHighLightColor(ContextCompat.getColor(mContext, R.color.highLight_Color));
        barDataSet.setValueTextSize(10);
        barDataSet.setDrawValues(false);
        barDataSet.setNeutralColor(ContextCompat.getColor(mContext, R.color.equal_color));
        barDataSet.setIncreasingColor(ContextCompat.getColor(mContext, R.color.up_color));
        barDataSet.setDecreasingColor(ContextCompat.getColor(mContext, R.color.down_color));
        barDataSet.setIncreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setDecreasingPaintStyle(Paint.Style.FILL);
        barDataSet.setNeutralPaintStyle(Paint.Style.FILL);
        return barDataSet;
    }

    public synchronized ArrayList<KLineDataModel> getDatas() {
        return mDatas;
    }

    public ArrayList<String> getXVals() {
        return mXVal;
    }

    public List<ILineDataSet> getLineDataMA() {
        return mLineDataMA;
    }

    public List<ILineDataSet> getLineDataBOLL() {
        return lineDataBOLL;
    }

    public List<ILineDataSet> getLineDataKDJ() {
        return lineDataKDJ;
    }

    public List<ILineDataSet> getLineDataRSI() {
        return lineDataRSI;
    }

    public List<ILineDataSet> getLineDataMACD() {
        return lineDataMACD;
    }

    public BarDataSet getBarDataMACD() {
        return barDataMACD = barDataMACD != null ? barDataMACD
                : new BarDataSet(new ArrayList<>(), "");
    }

    public BarDataSet getVolumeDataSet() {
        return mVolumeDataSet = mVolumeDataSet != null ? mVolumeDataSet
                : new BarDataSet(new ArrayList<>(), "");
    }

    public CandleDataSet getCandleDataSet() {
        return mCandleDataSet = mCandleDataSet != null ? mCandleDataSet
                : new CandleDataSet(new ArrayList<>(), "");
    }

    public CandleDataSet getBollCandleDataSet() {
        return mBollCandleDataSet = mBollCandleDataSet != null ? mBollCandleDataSet
                : new CandleDataSet(new ArrayList<>(), "");
    }

    public float getOffSet() {
        return offSet;
    }

    enum ColorType {
        blue,
        yellow,
        purple
    }
}
