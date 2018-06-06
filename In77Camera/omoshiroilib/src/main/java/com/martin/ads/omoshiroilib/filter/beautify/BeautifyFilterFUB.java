package com.martin.ads.omoshiroilib.filter.beautify;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;

/**
 * Created by Ads on 2017/4/6.
 */

public class BeautifyFilterFUB extends SimpleFragmentShaderFilter {
    public BeautifyFilterFUB(Context context) {
        super(context, "filter/fsh/beautify/beautify_b.glsl");
    }
}
