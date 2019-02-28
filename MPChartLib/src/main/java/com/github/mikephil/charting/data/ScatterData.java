package com.github.mikephil.charting.data;

import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.util.List;

@SuppressWarnings("unused")
public class ScatterData extends BarLineScatterCandleBubbleData<IScatterDataSet> {

    public ScatterData() {
        super();
    }

    public ScatterData(List<IScatterDataSet> dataSets) {
        super(dataSets);
    }

    public ScatterData(IScatterDataSet... dataSets) {
        super(dataSets);
    }

    /**
     * Returns the maximum shape-size across all DataSets.
     */
    public float getGreatestShapeSize() {
        float max = 0f;
        for (IScatterDataSet set : mDataSets) {
            final float size = set.getScatterShapeSize();
            if (size > max) {
                max = size;
            }
        }
        return max;
    }
}
