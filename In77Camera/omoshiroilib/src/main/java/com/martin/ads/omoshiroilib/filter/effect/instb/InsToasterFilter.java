package com.martin.ads.omoshiroilib.filter.effect.instb;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsToasterFilter extends MultipleTextureFilter {
    public InsToasterFilter(Context context) {
        super(context, "filter/fsh/instb/toaster2.glsl");
        textureSize=5;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/toastermetal.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/toastersoftlight.png");
        externalBitmapTextures[2].load(context, "filter/textures/inst/toastercurves.png");
        externalBitmapTextures[3].load(context, "filter/textures/inst/toasteroverlaymapwarm.png");
        externalBitmapTextures[4].load(context, "filter/textures/inst/toastercolorshift.png");
    }
}
