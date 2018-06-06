package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class EMInterferenceFilter extends ShaderToyAbsFilter{
    public EMInterferenceFilter(Context context) {
        super(context, "filter/fsh/shadertoy/em_interference.glsl");
    }
}
