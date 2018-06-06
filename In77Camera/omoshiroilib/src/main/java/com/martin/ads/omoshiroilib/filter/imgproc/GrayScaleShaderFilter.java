package com.martin.ads.omoshiroilib.filter.imgproc;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;

/**
 * Created by Ads on 2016/11/19.
 * GrayScaleShaderFilter (灰度)
 */

public class GrayScaleShaderFilter extends SimpleFragmentShaderFilter {
    public GrayScaleShaderFilter(Context context) {
        super(context, "filter/fsh/imgproc/gray_scale.glsl");
    }
}
