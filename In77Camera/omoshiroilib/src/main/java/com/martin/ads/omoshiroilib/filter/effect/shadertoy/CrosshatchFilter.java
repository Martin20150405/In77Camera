package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class CrosshatchFilter extends ShaderToyAbsFilter{
    public CrosshatchFilter(Context context) {
        super(context, "filter/fsh/shadertoy/crosshatch.glsl");
    }
}
