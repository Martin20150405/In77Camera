package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class ChromaticAberrationFilter extends ShaderToyAbsFilter{
    public ChromaticAberrationFilter(Context context) {
        super(context, "filter/fsh/shadertoy/chromatic_aberration.glsl");
    }
}
