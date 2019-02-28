package com.github.mikephil.charting.highlight;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.List;

/**
 * Created by philipp on 12/06/16.
 */
@SuppressWarnings("WeakerAccess")
public class RadarHighlighter extends PieRadarHighlighter<RadarChart> {

    public RadarHighlighter(RadarChart chart) {
        super(chart);
    }

    @Override
    protected Highlight getClosestHighlight(int index, float x, float y) {
        final List<Highlight> highlights = getHighlightsAtIndex(index);
        final float distanceToCenter = mChart.distanceToCenter(x, y) / mChart.getFactor();
        Highlight closest = null;
        float distance = Float.MAX_VALUE;
        for (int i = 0; i < highlights.size(); i++) {
            final Highlight high = highlights.get(i);
            final float cdistance = Math.abs(high.getY() - distanceToCenter);
            if (cdistance < distance) {
                closest = high;
                distance = cdistance;
            }
        }

        return closest;
    }

    /**
     * Returns an array of Highlight objects for the given index. The Highlight
     * objects give information about the value at the selected index and the
     * DataSet it belongs to. INFORMATION: This method does calculations at
     * runtime. Do not over-use in performance critical situations.
     */
    protected List<Highlight> getHighlightsAtIndex(int index) {
        mHighlightBuffer.clear();

        final float phaseX = mChart.getAnimator().getPhaseX();
        final float phaseY = mChart.getAnimator().getPhaseY();
        final float sliceangle = mChart.getSliceAngle();
        final float factor = mChart.getFactor();
        final MPPointF pOut = MPPointF.getInstance(0, 0);

        for (int i = 0; i < mChart.getData().getDataSetCount(); i++) {
            final IDataSet<?> dataSet = mChart.getData().getDataSetByIndex(i);
            final Entry entry = dataSet.getEntryForIndex(index);
            final float y = (entry.getY() - mChart.getYChartMin());

            Utils.getPosition(
                    mChart.getCenterOffsets(), y * factor * phaseY,
                    sliceangle * index * phaseX + mChart.getRotationAngle(), pOut);

            mHighlightBuffer.add(new Highlight(index, entry.getY(), pOut.x, pOut.y, i, dataSet.getAxisDependency()));
        }

        return mHighlightBuffer;
    }
}
