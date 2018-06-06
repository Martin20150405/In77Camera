package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsFreudFilter extends MultipleTextureFilter {
    public InsFreudFilter(Context context) {
        super(context, "filter/fsh/insta/freud.glsl");
        textureSize=1;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/freud_rand.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
        setUniform1f(glSimpleProgram.getProgramId(),"inputImageTextureHeight",surfaceHeight);
        setUniform1f(glSimpleProgram.getProgramId(),"inputImageTextureWidth",surfaceWidth);
    }
}
