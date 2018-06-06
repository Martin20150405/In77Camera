package com.martin.ads.omoshiroilib.encoder.gles;

import android.content.Context;

import com.martin.ads.omoshiroilib.codec.RenderHandler;
import com.martin.ads.omoshiroilib.filter.base.PassThroughFilter;

/**
 * Created by Ads on 2017/5/27.
 */

public class EGLFilterDispatcher extends PassThroughFilter implements RenderHandler.EGLDrawer {

    public EGLFilterDispatcher(Context context) {
        super(context);
    }

    @Override
    public void deInit() {
        destroy();
    }

    @Override
    public void draw(int textureId) {
        onDrawFrame(textureId);
    }

    @Override
    public void setViewportSize(int width, int height) {
        onFilterChanged(width,height);
    }
}
