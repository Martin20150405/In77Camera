package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class RefractionFilter extends ShaderToyAbsFilter {
    public RefractionFilter(Context context) {
        super(context, "filter/fsh/shadertoy/refraction.glsl");
        textureSize=1;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context,"filter/textures/refraction.png");
    }
}
