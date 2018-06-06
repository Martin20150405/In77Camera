package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class AscIIArtFilter extends ShaderToyAbsFilter{
    public AscIIArtFilter(Context context) {
        super(context, "filter/fsh/shadertoy/ascii_art.glsl");
    }
}
