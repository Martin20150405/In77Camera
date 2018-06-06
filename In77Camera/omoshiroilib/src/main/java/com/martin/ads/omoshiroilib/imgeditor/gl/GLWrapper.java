package com.martin.ads.omoshiroilib.imgeditor.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.Log;

import com.martin.ads.omoshiroilib.filter.base.FilterGroup;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;
import com.martin.ads.omoshiroilib.filter.helper.FilterFactory;
import com.martin.ads.omoshiroilib.filter.helper.FilterType;
import com.martin.ads.omoshiroilib.imgeditor.MiscUtils;
import com.martin.ads.omoshiroilib.util.BitmapUtils;
import com.martin.ads.omoshiroilib.util.TextureUtils;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by Ads on 2017/8/2.
 */

public class GLWrapper implements GLSurfaceView.Renderer {
    private GLSurfaceView glImageView;
    private Context context;
    private FilterGroup filterGroup;
    private FilterGroup customizedFilters;
    private int surfaceWidth, surfaceHeight;
    private int textureId;

    private String filePath;

    private GLWrapper() {
        textureId = 0;
    }

    public static GLWrapper newInstance() {
        return new GLWrapper();
    }

    public GLWrapper init() {
        glImageView.setEGLContextClientVersion(2);
        glImageView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        glImageView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        glImageView.setRenderer(this);

        glImageView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        //glImageView.setClickable(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            glImageView.setPreserveEGLContextOnPause(true);
        }

        filterGroup = new FilterGroup();
        customizedFilters = new FilterGroup();

        filterGroup.addFilter(new FirstPassFilter(context));
        customizedFilters.addFilter(new PassThroughFilter(context));
        filterGroup.addFilter(customizedFilters);

        return this;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        filterGroup.init();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        surfaceWidth = width;
        surfaceHeight = height;
        filterGroup.onFilterChanged(width, height);
        if(!MiscUtils.isNull(filePath)){
            final Bitmap bitmap = BitmapUtils.loadBitmapFromFile(filePath);
            textureId= TextureUtils.loadTextureWithOldTexId(bitmap,0);
            Log.d("GL_THREAD",textureId+" ");
        }
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        if (textureId != 0) {
            filterGroup.onDrawFrame(textureId);
        }
    }

    public GLWrapper setGlImageView(GLSurfaceView glImageView) {
        this.glImageView = glImageView;
        return this;
    }

    public GLWrapper setContext(Context context) {
        this.context = context;
        return this;
    }

    public void setTextureId(int textureId) {
        this.textureId = textureId;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void switchLastFilterOfCustomizedFilters(FilterType filterType){
        if (filterType==null) return;
        //currentFilterType=filterType;
        customizedFilters.switchLastFilter(FilterFactory.createFilter(filterType,context));
    }
}
