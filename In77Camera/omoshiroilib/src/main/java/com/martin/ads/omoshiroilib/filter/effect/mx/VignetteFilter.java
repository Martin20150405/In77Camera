package com.martin.ads.omoshiroilib.filter.effect.mx;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;

/**
 * Created by Ads on 2017/1/31.
 * VignetteFilter (炫影)
 */

public class VignetteFilter extends SimpleFragmentShaderFilter {
    public VignetteFilter(Context context) {
        super(context, "filter/fsh/mx/mx_vignette.glsl");
    }
}
