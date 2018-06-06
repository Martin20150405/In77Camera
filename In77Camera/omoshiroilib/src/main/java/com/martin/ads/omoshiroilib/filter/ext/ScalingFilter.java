package com.martin.ads.omoshiroilib.filter.ext;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;

/**
 * Created by Ads on 2017/2/16.
 */

public class ScalingFilter extends PassThroughFilter {
    private boolean drawOnTop;

    public ScalingFilter(Context context) {
        super(context);
        drawOnTop=false;
    }

    @Override
    public void onPreDrawElements() {
        if(!drawOnTop) super.onPreDrawElements();
    }

    public ScalingFilter setScalingFactor(float scalingFactor){
        plane.scale(scalingFactor);
        return this;
    }

    public ScalingFilter setDrawOnTop(boolean drawOnTop) {
        this.drawOnTop = drawOnTop;
        return this;
    }
}
