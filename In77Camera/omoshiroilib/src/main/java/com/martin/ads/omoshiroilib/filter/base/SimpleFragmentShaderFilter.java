package com.martin.ads.omoshiroilib.filter.base;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.glessential.program.GLSimpleProgram;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/1/31.
 */

public class SimpleFragmentShaderFilter extends AbsFilter {

    protected GLSimpleProgram glSimpleProgram;

    public SimpleFragmentShaderFilter(Context context,
                                      final String fragmentShaderPath) {
        super();
        glSimpleProgram=new GLSimpleProgram(context, "filter/vsh/base/simple.glsl",fragmentShaderPath);
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
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        //Log.d(TAG, "onDrawFrame: "+surfaceWidth+" "+surfaceHeight);
        plane.draw();
    }
}
