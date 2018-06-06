package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsEarlyBirdFilter extends MultipleTextureFilter {
    public InsEarlyBirdFilter(Context context) {
        super(context, "filter/fsh/insta/early_bird.glsl");
        textureSize=5;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/earlybirdcurves.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/earlybirdoverlaymap_new.png");
        externalBitmapTextures[2].load(context, "filter/textures/inst/vignettemap_new.png");
        externalBitmapTextures[3].load(context, "filter/textures/inst/earlybirdblowout.png");
        externalBitmapTextures[4].load(context, "filter/textures/inst/earlybirdmap.png");
    }
}
