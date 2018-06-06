package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsHudsonFilter extends MultipleTextureFilter {
    public InsHudsonFilter(Context context) {
        super(context, "filter/fsh/insta/hudson.glsl");
        textureSize=3;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/hudsonbackground.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/overlaymap.png");
        externalBitmapTextures[2].load(context, "filter/textures/inst/hudsonmap.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
    }
}
