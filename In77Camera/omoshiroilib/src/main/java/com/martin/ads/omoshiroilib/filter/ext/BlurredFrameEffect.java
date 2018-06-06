package com.martin.ads.omoshiroilib.filter.ext;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.FilterGroup;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.effect.shadertoy.FastBlurFilter;
import com.martin.ads.omoshiroilib.filter.imgproc.CustomizedGaussianBlurFilter;

/**
 * Created by Ads on 2017/2/16.
 */

public class BlurredFrameEffect extends FilterGroup{

    //in pixels
    private static final int BLUR_STEP_LENGTH=2;
    private static final float SCALING_FACTOR=0.6f;
    private ScalingFilter scalingFilter;
    public BlurredFrameEffect(Context context) {
        super();
        addFilter(new FastBlurFilter(context).setScale(true));
        addFilter(CustomizedGaussianBlurFilter
                .initWithBlurRadiusInPixels(4)
                .setTexelHeightOffset(BLUR_STEP_LENGTH)
                .setScale(true));
        addFilter(CustomizedGaussianBlurFilter
                .initWithBlurRadiusInPixels(4)
                .setTexelWidthOffset(BLUR_STEP_LENGTH)
                .setScale(true));
        addFilter(new PassThroughFilter(context));
        scalingFilter=new ScalingFilter(context)
                .setScalingFactor(SCALING_FACTOR)
                .setDrawOnTop(true);
    }

    @Override
    public void onDrawFrame(int textureId) {
        super.onDrawFrame(textureId);
        scalingFilter.onDrawFrame(textureId);
    }

    @Override
    public void init() {
        super.init();
        scalingFilter.init();
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        super.onFilterChanged(surfaceWidth, surfaceHeight);
        scalingFilter.onFilterChanged(surfaceWidth,surfaceHeight);
    }

    @Override
    public void destroy() {
        super.destroy();
        scalingFilter.destroy();
    }
}
