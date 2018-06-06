package com.martin.ads.omoshiroilib.glessential;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import com.martin.ads.omoshiroilib.filter.base.AbsFilter;
import com.martin.ads.omoshiroilib.filter.base.SimpleFragmentShaderFilter;
import com.martin.ads.omoshiroilib.filter.effect.mx.VignetteFilter;
import com.martin.ads.omoshiroilib.filter.helper.FilterFactory;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.glessential.texture.BitmapTexture;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ads on 2017/2/13.
 */

public class GLImageRender implements GLSurfaceView.Renderer {
    private AbsFilter filter;
    private Context context;
    private BitmapTexture bitmapTexture;
    private Bitmap bitmap;
    public GLImageRender(Context context, Bitmap bitmap,FilterType filterType) {
        this.context=context;
        filter= FilterFactory.createFilter(
                filterType,context)
                .resetPlane(false);
        bitmapTexture=new BitmapTexture();
        this.bitmap=bitmap;
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        filter.init();
        bitmapTexture.loadBitmap(bitmap);
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        filter.onDrawFrame(bitmapTexture.getImageTextureId());
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
        GLES20.glViewport(0,0,width,height);
        filter.onFilterChanged(width,height);
    }
}
