package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsAmaroFilter extends MultipleTextureFilter {
    public InsAmaroFilter(Context context) {
        super(context, "filter/fsh/insta/amaro.glsl");
        textureSize=3;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/brannan_blowout.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/overlaymap.png");
        externalBitmapTextures[2].load(context, "filter/textures/inst/amaromap.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
    }
}
