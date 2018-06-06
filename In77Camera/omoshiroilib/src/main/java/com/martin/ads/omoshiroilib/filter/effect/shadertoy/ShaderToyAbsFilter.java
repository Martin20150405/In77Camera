package com.martin.ads.omoshiroilib.filter.effect.shadertoy;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.MultipleTextureFilter;
import com.martin.ads.omoshiroilib.util.TextureUtils;

import java.nio.FloatBuffer;

/**
 * Created by Ads on 2017/2/16.
 */

public class ShaderToyAbsFilter extends MultipleTextureFilter {

    private final long START_TIME;

    public ShaderToyAbsFilter(Context context, String fragmentShaderPath) {
        super(context, fragmentShaderPath);
        START_TIME = System.currentTimeMillis();
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        int iResolutionLocation = GLES20.glGetUniformLocation(glSimpleProgram.getProgramId(), "iResolution");
        GLES20.glUniform3fv(iResolutionLocation, 1,
                FloatBuffer.wrap(new float[]{(float) surfaceWidth, (float) surfaceHeight, 1.0f}));

        float currentTime = ((float) (System.currentTimeMillis() - START_TIME)) / 1000.0f;
        setUniform1f(glSimpleProgram.getProgramId(), "iGlobalTime",currentTime);

        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }
}
