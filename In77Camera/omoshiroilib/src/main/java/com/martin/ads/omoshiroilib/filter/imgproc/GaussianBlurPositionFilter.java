package com.martin.ads.omoshiroilib.filter.imgproc;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.glessential.program.GLSimpleProgram;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/3.
 */

//TODO:blurRadius and blurCenter should be adjustable
//addFilter(new GaussianBlurPositionFilter(context)
//        .setTexelWidthOffset(BLUR_STEP_LENGTH));
//addFilter(new GaussianBlurPositionFilter(context)
//        .setTexelHeightOffset(BLUR_STEP_LENGTH));
public class GaussianBlurPositionFilter extends AbsFilter {
    protected GLSimpleProgram glSimpleProgram;

    private float texelWidthOffset;
    private float texelHeightOffset;
    private float blurRadius;

    private boolean scale;

    public GaussianBlurPositionFilter(Context context) {
        super();
        glSimpleProgram=new GLSimpleProgram(context,
                "filter/vsh/imgproc/gaussian_blur_position.glsl",
                "filter/fsh/imgproc/gaussian_blur_position.glsl");
        texelWidthOffset=texelHeightOffset=0;
        scale=false;
        //an ellipse
        blurRadius=0.3f;
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

        setUniform1f(glSimpleProgram.getProgramId(),"aspectRatio"
                ,(float)surfaceWidth/surfaceHeight);
        setUniform1f(glSimpleProgram.getProgramId(),"blurRadius"
                ,blurRadius);
        setUniform2fv(glSimpleProgram.getProgramId(),"blurCenter"
                ,new float[]{0.5f,0.5f});
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }

    public GaussianBlurPositionFilter setTexelHeightOffset(float texelHeightOffset) {
        this.texelHeightOffset = texelHeightOffset;
        return this;
    }

    public GaussianBlurPositionFilter setTexelWidthOffset(float texelWidthOffset) {
        this.texelWidthOffset = texelWidthOffset;
        return this;
    }

    @Override
    public void onFilterChanged(int surfaceWidth, int surfaceHeight) {
        if(!scale)
            super.onFilterChanged(surfaceWidth, surfaceHeight);
        else super.onFilterChanged(surfaceWidth/4, surfaceHeight/4);
    }

    public GaussianBlurPositionFilter setScale(boolean scale) {
        this.scale = scale;
        return this;
    }
}
