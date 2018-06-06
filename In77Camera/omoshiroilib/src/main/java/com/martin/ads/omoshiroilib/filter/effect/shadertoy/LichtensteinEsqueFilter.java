package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class LichtensteinEsqueFilter extends ShaderToyAbsFilter{
    public LichtensteinEsqueFilter(Context context) {
        super(context, "filter/fsh/shadertoy/lichtenstein_esque.glsl");
    }
}
