package com.martin.ads.omoshiroilib.filter.effect.insta;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/6.
 */

public class InsCrayonFilter extends SimpleFragmentShaderFilter {

    //1.0 - 5.0
    private float mStrength;
    public InsCrayonFilter(Context context) {
        super(context,"filter/fsh/insta/crayon.glsl");
        mStrength=2f;
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        setUniform1f(glSimpleProgram.getProgramId(),"strength",mStrength);
        setUniform2fv(glSimpleProgram.getProgramId(),"singleStepOffset",new float[]{1.0f/surfaceWidth,1.0f/surfaceHeight});

        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }
}
