package com.martin.ads.omoshiroilib.filter.beautify;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/6.
 */

public class BeautifyFilterA extends SimpleFragmentShaderFilter {
    private float texelWidthOffset;
    private float texelHeightOffset;

    public BeautifyFilterA(Context context) {
        super(context,"filter/fsh/beautify/beautify_a.glsl");
        texelWidthOffset=texelHeightOffset=2;
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"texelWidthOffset",texelWidthOffset/surfaceWidth);
        setUniform1f(glSimpleProgram.getProgramId(),"texelHeightOffset",texelHeightOffset/surfaceHeight);

        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }

    public BeautifyFilterA setStepLength(int stepLength){
        texelWidthOffset=texelHeightOffset=stepLength;
        return this;
    }
}
