package com.martin.ads.omoshiroilib.filter.effect.instb;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsSierraFilter extends MultipleTextureFilter {
    public InsSierraFilter(Context context) {
        super(context, "filter/fsh/instb/sierra.glsl");
        textureSize=3;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures[0].load(context, "filter/textures/inst/sierravignette.png");
        externalBitmapTextures[1].load(context, "filter/textures/inst/overlaymap.png");
        externalBitmapTextures[2].load(context, "filter/textures/inst/sierramap.png");
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",1.0f);
    }
}
