package com.martin.ads.omoshiroilib.filter.effect.mx;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;

/**
 * Created by Ads on 2017/1/31.
 * MoonLightFilter (月光)
 */

public class MoonLightFilter extends SimpleFragmentShaderFilter {
    public MoonLightFilter(Context context) {
        super(context, "filter/fsh/mx/mx_moon_light.glsl");
    }
}
