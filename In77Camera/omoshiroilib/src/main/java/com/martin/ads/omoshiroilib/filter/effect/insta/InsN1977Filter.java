package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsN1977Filter extends MultipleTextureFilter {
    public InsN1977Filter(Context context) {
        super(context, "filter/fsh/insta/n1977.glsl");
        textureSize=2;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/n1977map.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/n1977blowout.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
    }
}
