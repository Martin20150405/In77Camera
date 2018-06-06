package com.martin.ads.omoshiroilib.filter.imgproc;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.glessential.program.GLSimpleProgram;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/2/16.
 */

public class GaussianBlurFilter extends AbsFilter {
    protected GLSimpleProgram glSimpleProgram;

    private float texelWidthOffset;
    private float texelHeightOffset;

    private boolean scale;

    public GaussianBlurFilter(Context context) {
        super();
        glSimpleProgram=new GLSimpleProgram(context, "filter/vsh/imgproc/gaussian_blur.glsl", "filter/fsh/imgproc/gaussian_blur.glsl");
        texelWidthOffset=texelHeightOffset=0;
        scale=false;
    }

    @Override
    public void init() {
        glSimpleProgram.create();
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        glSimpleProgram.use();
        plane.uploadTexCoordinateBuffer(glSimpleProgram.getTextureCoordinateHandle());
        plane.uploadVerticesBuffer(glSimpleProgram.getPositionHandle());
    }

    @Override
    public void destroy() {
        glSimpleProgram.onDestroy();
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

    public GaussianBlurFilter setTexelHeightOffset(float texelHeightOffset) {
        this.texelHeightOffset = texelHeightOffset;
        return this;
    }

    public GaussianBlurFilter setTexelWidthOffset(float texelWidthOffset) {
        this.texelWidthOffset = texelWidthOffset;
        return this;
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        if(!scale)
            super.onFilterChanged(surfaceWidth, surfaceHeight);
        else super.onFilterChanged(surfaceWidth/4, surfaceHeight/4);
    }

    public GaussianBlurFilter setScale(boolean scale) {
        this.scale = scale;
        return this;
    }
}
