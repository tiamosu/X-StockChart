package com.android.stockapp.stockchart.model.bean;

import com.android.stockapp.stockchart.model.KLineDataModel;

import java.util.ArrayList;

/**
 * Created by loro on 2017/3/8.
 */
public class EXPMAEntity {

    private ArrayList<Float> EXPMAs;

    public EXPMAEntity(ArrayList<KLineDataModel> kLineBeens, int n) {
        EXPMAs = new ArrayList<>();

        float ema = 0.0f;
        float t = n + 1;
        float yz = 2 / t;
        if (kLineBeens != null && kLineBeens.size() > 0) {

            for (int i = 0; i < kLineBeens.size(); i++) {
                if (i == 0) {
                    ema = (float) kLineBeens.get(i).getClose();
                } else {
                    ema = (float) ((yz * kLineBeens.get(i).getClose()) + ((1 - yz) * ema));
                }
                EXPMAs.add(ema);
            }
        }
    }

    public ArrayList<Float> getEXPMAs() {
        return EXPMAs;
    }
}
