package com.martin.ads.omoshiroilib.filter.effect.instb;

import android.content.Context;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;

/**
 * Created by Ads on 2017/4/7.
 */

public class InsSketchFilter extends MultipleTextureFilter {
    public InsSketchFilter(Context context) {
        super(context, "filter/fsh/instb/sketch.glsl");
        textureSize=0;
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",0.9f);
        setUniform2fv(glSimpleProgram.getProgramId(),"singleStepOffset",new float[]{1.0f/surfaceWidth,1.0f/surfaceHeight});
    }
}
