package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/2/16.
 * Similar to BoxBlur
 */

public class FastBlurFilter extends ShaderToyAbsFilter {

    private boolean scale;

    public FastBlurFilter(Context context) {
        super(context, "filter/fsh/shadertoy/fast_blur.glsl");
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        if(!scale)
            super.onFilterChanged(surfaceWidth, surfaceHeight);
        else super.onFilterChanged(surfaceWidth/4, surfaceHeight/4);
    }

    public FastBlurFilter setScale(boolean scale) {
        this.scale = scale;
        return this;
    }
}
