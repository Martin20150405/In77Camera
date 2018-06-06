package com.martin.ads.omoshiroilib.filter.effect.instb;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsSutroFilter extends MultipleTextureFilter {
    public InsSutroFilter(Context context) {
        super(context, "filter/fsh/instb/sutro.glsl");
        textureSize=5;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/vignette_map.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/sutrometal.png");
        externalBitmapTextures[2].load(context, "filter/textures/inst/softlight.png");
        externalBitmapTextures[3].load(context, "filter/textures/inst/sutroedgeburn.png");
        externalBitmapTextures[4].load(context, "filter/textures/inst/sutrocurves.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
    }
}
