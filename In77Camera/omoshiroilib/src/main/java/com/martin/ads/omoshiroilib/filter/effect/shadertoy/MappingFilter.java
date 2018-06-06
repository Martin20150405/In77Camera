package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;

/**
 * Created by Ads on 2017/4/6.
 */

public class MappingFilter extends ShaderToyAbsFilter {
    public MappingFilter(Context context) {
        super(context, "filter/fsh/shadertoy/mapping.glsl");
        textureSize=1;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context,"filter/textures/mapping.jpg");
    }
}
