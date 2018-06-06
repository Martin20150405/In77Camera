package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsKevinFilter extends MultipleTextureFilter {
    public InsKevinFilter(Context context) {
        super(context, "filter/fsh/insta/kevin.glsl");
        textureSize=1;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/kevinmap.png");
    }

}
