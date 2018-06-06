package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsHefeFilter extends MultipleTextureFilter {
    public InsHefeFilter(Context context) {
        super(context, "filter/fsh/insta/hefe.glsl");
        textureSize=5;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/edgeburn.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/hefemap.png");
        externalBitmapTextures[2].load(context, "filter/textures/inst/hefegradientmap.png");
        externalBitmapTextures[3].load(context, "filter/textures/inst/hefesoftlight.png");
        externalBitmapTextures[4].load(context, "filter/textures/inst/hefemetal.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
    }
}
