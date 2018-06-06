package com.martin.ads.omoshiroilib.filter.base;

import android.content.Context;
import android.opengl.GLES20;

import com.martin.ads.omoshiroilib.glessential.texture.BitmapTexture;
import com.martin.ads.omoshiroilib.util.TextureUtils;

/**
 * Created by Ads on 2017/4/6.
 * Textures are numbered from 2-N
 */

public abstract class MultipleTextureFilter extends SimpleFragmentShaderFilter {
    protected BitmapTexture[] externalBitmapTextures;
    protected int[] externalTextureHandles;
    protected int textureSize;
    protected Context context;

    public MultipleTextureFilter(Context context, String fragmentShaderPath) {
        super(context, fragmentShaderPath);
        this.context=context;
        textureSize=0;
    }

    @Override
    public void init() {
        super.init();
        externalBitmapTextures=new BitmapTexture[textureSize];
        for(int i=0;i<textureSize;i++){
            externalBitmapTextures[i]=new BitmapTexture();
        }
        externalTextureHandles=new int[textureSize];
        for(int i=0;i<textureSize;i++){
            externalTextureHandles[i] =
                    GLES20.glGetUniformLocation(glSimpleProgram.getProgramId(),"sTexture"+(i+2));
        }
    }

    @Override
    public void destroy() {
        glSimpleProgram.onDestroy();
        for(BitmapTexture bitmapTexture:externalBitmapTextures){
            bitmapTexture.destroy();
        }
    }

    @Override
    public void onPreDrawElements() {
        super.onPreDrawElements();
        for (int i = 0; i < textureSize; i++) {
            TextureUtils.bindTexture2D(
                    externalBitmapTextures[i].getImageTextureId(),
                    GLES20.GL_TEXTURE0+(i+1),
                    externalTextureHandles[i],
                    i+1
            );
        }
    }

    @Override
    public void onDrawFrame(int textureId) {
        onPreDrawElements();
        TextureUtils.bindTexture2D(textureId, GLES20.GL_TEXTURE0,glSimpleProgram.getTextureSamplerHandle(),0);
        GLES20.glViewport(0,0,surfaceWidth,surfaceHeight);
        plane.draw();
    }
}
