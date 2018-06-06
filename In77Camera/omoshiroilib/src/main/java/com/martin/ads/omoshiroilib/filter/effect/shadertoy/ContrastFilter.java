package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class ContrastFilter extends ShaderToyAbsFilter{
    public ContrastFilter(Context context) {
        super(context, "filter/fsh/shadertoy/contrast.glsl");
    }
}
